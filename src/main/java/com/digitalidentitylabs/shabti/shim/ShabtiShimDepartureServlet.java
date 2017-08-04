package com.digitalidentitylabs.shabti.shim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;


@WebServlet(
        name = "ShabtiShimDepartureServlet",
        description = "Servlet to create auth demand, and redirect to auth service",
        urlPatterns = {"/Shim"},
        initParams = {
                @WebInitParam(name = "outgoingPath", value = "/login/authn/new/"),
                @WebInitParam(name = "propertiesFile", value = "shim.properties"),
                @WebInitParam(name = "failPath", value = "/500")
        }
)
public class ShabtiShimDepartureServlet extends HttpServlet {

    private final Properties properties = new Properties();
    private JedisPool jedisPool = null;

    public ShabtiShimDepartureServlet() throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(properties.getProperty("propertiesFile"));
        Properties properties = new Properties();
        properties.load(input);

        JedisPool jedisPool = new JedisPool(
                new JedisPoolConfig(),
                properties.getProperty("redis_host"),
                Integer.parseInt(properties.getProperty("redis_port"))
        );

    }

    public ShabtiShimDepartureServlet(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        try {

            final ShabtiShimDemand demand = new ShabtiShimDemand();
            demand.externalAuthKey = ExternalAuthentication.startExternalAuthentication(request);
            demand.relyingParty = request.getAttribute(ExternalAuthentication.RELYING_PARTY_PARAM).toString();
            demand.authnMethod = request.getAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM).toString();
            demand.isPassive = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM).toString());
            demand.forceAuthn = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM).toString());

            // Write serialized Demand bean to Redis
            final ObjectMapper mapper = new ObjectMapper();
            final String key = "key"; // TODO: Generate this randomly
            Jedis jedis = jedisPool.getResource();
//            jedis.auth("password");
            jedis.set(key, mapper.writeValueAsString(demand));

            response.sendRedirect("https://auth.localhost.demo.university/" + key);

        } catch (final ExternalAuthenticationException e) {
            throw new ServletException("Error processing external authentication request", e);
        }
    }

}
