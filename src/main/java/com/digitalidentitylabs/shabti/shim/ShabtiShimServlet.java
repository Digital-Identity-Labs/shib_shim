package com.digitalidentitylabs.shabti.shim;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShabtiShimServlet extends HttpServlet {

    // Path to reach the secondary external authentication service, before user interaction
    private static String outgoingPath = "/login/authn/new/";

    // Path to return to the main authentication service, after user interaction
    private static String incomingPath = "/AuthnEngine";

    // Path to go to in a huff in an invalid demand is returned by secondary external auth
    private static String failPath = "/500";

    // Redis host
    private static String redisHost = "127.0.0.1";

    // Redis port
    private static int redisPort = 6379;

    // Am I doing this right? It's not actually logging anything...
    private static final  Logger logger = LoggerFactory.getLogger("ShabtiShim");

    // Redis Pool that will, I think, be threadsafe...
    private static JedisPool redisPool = null;

    // Redis Pool that will, I think, be threadsafe...
    private static int shimVersion = 1;

    // Use default values or load values from web.xml
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        // Set configuration details if they are specified in web.xml, or leave alone if not
        outgoingPath = config.getInitParameter("outgoingPath") == null ?
                outgoingPath : config.getInitParameter("outgoingPath");
        incomingPath = config.getInitParameter("incomingPath") == null ?
                incomingPath : config.getInitParameter("incomingPath");
        failPath = config.getInitParameter("failPath") == null ?
                failPath : config.getInitParameter("failPath");

        redisHost = config.getInitParameter("redisHost") == null ?
                redisHost : config.getInitParameter("redisHost");
        redisPort = config.getInitParameter("redisPort") == null ?
                redisPort : Integer.parseInt(config.getInitParameter("redisPort"));




        redisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);

    }

    public void destroy() {

        redisPool.destroy();

    }

    // Route outwards to secondary external auth, or inwards to core service, depending on token
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // It all revolves around the token...
        String token = getTokenFromRequest(request);

        // No token in path means we need to create one and redirect to external authenticator
        if ( token.isEmpty() ) {

            writeDemand(request);

            logger.debug("Outgoing! (redirecting out)");
            response.sendRedirect(outgoingPath);

        }

        // A token, so we should check it returns valid authenticated data
        else {

            JSONObject demand = readDemand(token);

            if ( isValidAuthenticatedDemand(demand) ) {

                // Update request with data from the demand
                request.setAttribute("principal_name", demand.get("principal"));
                request.setAttribute("forceAuthn",     demand.get("force"));
                request.setAttribute("isPassive",      demand.get("passive"));
                request.setAttribute("authnMethod",    demand.get("method"));
                request.setAttribute("relyingParty",   demand.get("relying_party"));

                // Forward (rather than redirect) to pass control directly to the Shibboleth authEngine with same context
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(incomingPath);
                dispatcher.forward(request, response);

            } else {

                // It's all gone wrong, I've had enough of this, etc. Deal with it elsewhere.
                response.sendRedirect(failPath);

            }

        }

    }

    private Boolean isValidAuthenticatedDemand(JSONObject demand) {

         logger.info("Validating");

         if (demand == null) {
             return false;
         }



         return true;

    }

    private String getTokenFromRequest(HttpServletRequest request) {

        // Get rest of path as token
        String token = request.getPathInfo();

        // We are not interested in a lone slash or a leading slash.
        token = token == null ? "" : token;
        token = token.startsWith("/") ? token.substring(1) : token;

        return token;
    }

    @SuppressWarnings("unchecked")
    private void writeDemand(HttpServletRequest request) {

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        String token = DigestUtils.md5Hex(uuid);

        JSONObject demand = new JSONObject();

        // Core attributes
        demand.put("forceAuthn",   request.getAttribute("forceAuthn")   );
        demand.put("isPassive",    request.getAttribute("isPassive")    );
        demand.put("authnMethod",  request.getAttribute("authnMethod")  );
        demand.put("relyingParty", request.getAttribute("relyingParty") );

        // Copy of the token (this is also the key when stored)
        demand.put("token", token);

        // Metadata
        demand.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // User agent verification data
        demand.put("user_address", request.getRemoteAddr() );
        demand.put("agent_hash",   DigestUtils.md5Hex(request.getHeader("User-Agent")));

        // Service information
        demand.put("site_domain",  request.getServerName() );
        demand.put("server_tag",   "indiid"                );
        demand.put("component",    "core"                  );
        demand.put("protocol",     "shibboleth"            );
        demand.put("version",      shimVersion             );

        // A bit of glue info
        demand.put("return_url",   request.getRequestURL() );

        String exportedDemand = demand.toJSONString();

        logger.info("Storing...");
        logger.info(exportedDemand);

        Jedis redis = null;

        try {
            redis = redisPool.getResource();
            redis.set(token, exportedDemand);
        } finally {
            redisPool.returnResource(redis);
        }

    }

    private JSONObject readDemand(String token) {

        logger.info("Reading...");

        // Scoping on try?
        Jedis redis = null;
        String importedDemand = null;

        try {

            redis = redisPool.getResource();
            importedDemand = redis.get(token);

            logger.info(importedDemand);

        } finally {

            redisPool.returnResource(redis);

        }

        // Check if anything was found. If not, return null (assuming that's the correct thing to do...)
        if (importedDemand == null) {

            return null;

        }

        // Convert recovered string into a JSON-aware HashMap

            logger.info("MONKEY");
            logger.info(importedDemand);

            Object obj= JSONValue.parse(importedDemand);
            JSONObject demand=(JSONObject)obj;


            return demand;

    }

}
