package com.digitalidentitylabs.shabti.shim;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ShabtiShimServlet extends HttpServlet {

    // Path to reach the secondary external authentication service, before user interaction
    private static String outgoingPath = "/login/authn/new/";

    // Path to return to the main authentication service, after user interaction
    private static String incomingPath = "/AuthnEngine";

    // Path to go to in a huff in an invalid data is returned by secondary external auth
    private static String failPath = "/500";

    // Redis host
    private static String redisHost = "127.0.0.1";

    // Redis port
    private static int redisPort = 6379;

    // Redis password
    private static String redisPassword = null;

    // Am I doing this right? It's not actually logging anything...
    private static final  Logger logger = LoggerFactory.getLogger("ShabtiShim");

    // Redis Pool that will, I think, be threadsafe...
    private static ShabtiShimStorage storage = null;


    private static DateTimeFormatter javascriptDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z");
    private static ObjectMapper mapper = null;

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
        redisPassword = (config.getInitParameter("redisPassword") == null ? redisPassword : config.getInitParameter("redisPassword"));

        storage = redisPassword == null ?
                new ShabtiShimStorage(redisHost, redisPort) : new ShabtiShimStorage(redisHost, redisPort, redisPassword);

        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

    }

    public void destroy() {

        storage.destroy();

    }

    // Route outwards to secondary external auth, or inwards to core service, depending on token
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // It all revolves around the token...
        String token = getTokenFromRequest(request);

        // No token in path means we need to create one and redirect to external authenticator
        if ( token.isEmpty() ) {

            // Build and store a new demand using context data in the request
            ShabtiShimDemand demand = createOutgoingDemand(request);

            // Redirect browser to the secondary external authenticator
            response.sendRedirect(outgoingPath + demand.getToken());

        }

        // We have a token, so we should check it returns valid authenticated data
        else {

            ShabtiShimDemand demand = readDemand(token);

            if ( demand.isValidAndAuthenticated() ) {

                // Pass data back into request for authEngine to use
                handleGoodIncomingDemand(demand);
                informTheService(request, demand);

                // Forward (rather than redirect) to pass control directly to the Shibboleth authEngine with same context
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(incomingPath);
                dispatcher.forward(request, response);

            } else {

                handleBadIncomingDemand(demand);

                // It's all gone wrong, I've had enough of this, etc. Deal with it elsewhere.
                response.sendRedirect(failPath); // Fixme: Add error logging here too.

            }

        }

    }


    private ShabtiShimDemand createOutgoingDemand(HttpServletRequest request) {

        ShabtiShimDemand demand = new ShabtiShimDemand(request);

        logger.info(demand.getToken());

        writeDemand(demand);

        logger.debug("Outgoing! (redirecting out)");

        return demand;

    }

    private void handleGoodIncomingDemand(ShabtiShimDemand demand) {

        logger.info(String.format("Received valid authenticated demand for token %s", demand.getToken()));
        storage.delete(demand.getToken());

    }

    private void handleBadIncomingDemand(ShabtiShimDemand demand) {

        logger.warn(String.format("Received invalid authenticated demand for token %s: %s", demand.getToken(), demand.getValidationMessage()));
        //logger.warn(demand.errorMessage());

        if (demand != null && demand.getToken() != null && ! demand.getToken().isEmpty()) {

            storage.delete(demand.getToken());

        }
    }

    private boolean informTheService(HttpServletRequest request, ShabtiShimDemand demand) {

        // Update request with data from the data
        request.setAttribute("principal_name", demand.getPrincipal());
        request.setAttribute("forceAuthn", demand.getForceAuthn());
        request.setAttribute("isPassive", demand.getIsPassive());
        request.setAttribute("authnMethod", demand.getAuthnMethod());
        request.setAttribute("relyingParty", demand.getRelyingParty());

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

    private void writeDemand(ShabtiShimDemand demand) {

        String exportedDemand = null;
        try {
            logger.info(demand.getToken());
            exportedDemand = mapper.writeValueAsString(demand);
        } catch (JsonProcessingException e) {
            logger.error(String.format("Failed to write demand for token %s!", demand.getToken()), e);
        }

        logger.info("Storing ...");
        logger.info(exportedDemand);

        storage.write(demand.getToken(), exportedDemand);

    }


    private ShabtiShimDemand readDemand(String token) {

        logger.info("Reading...");

        String importedDemand = storage.read(token);

        // Create a nullobject for Demand if null is returned
        importedDemand = ((importedDemand == null) ? "{}" : importedDemand);


        ShabtiShimDemand demand = null;
        try {
            demand = mapper.readValue(importedDemand, ShabtiShimDemand.class);
        } catch (IOException e) {
            logger.error(String.format("Failed to read demand for token %s!", token), e);
        }

        return demand;

    }

}
