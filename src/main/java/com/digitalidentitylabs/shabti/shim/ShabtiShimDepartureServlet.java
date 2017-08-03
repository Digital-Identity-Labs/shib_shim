package com.digitalidentitylabs.shabti.shim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import org.apache.commons.lang.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Map;

@WebServlet(
        name = "ShabtiShimDepartureServlet",
        description = "Servlet to create auth demand, and redirect to auth service",
        urlPatterns = {"/Authn/Shim"},
        initParams={
                @WebInitParam(name="outgoingPath",  value="/login/authn/new/"),
                @WebInitParam(name="failPath",      value="/500"),
                @WebInitParam(name="redisHost",     value="127.0.0.1"),
                @WebInitParam(name="redisPort",     value="6379"),
                @WebInitParam(name="redisPassword", value="")
        }
)
public class ShabtiShimDepartureServlet extends HttpServlet {

    private final Jedis jedis;

    public ShabtiShimDepartureServlet() {
        jedis = new Jedis("redis");
    }

    public ShabtiShimDepartureServlet(final Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        try {

            final ShimDemand demand = new ShimDemand();
            demand.externalAuthKey = ExternalAuthentication.startExternalAuthentication(request);
            demand.relyingParty = request.getAttribute(ExternalAuthentication.RELYING_PARTY_PARAM).toString();;
            demand.authnMethod = request.getAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM).toString();
            demand.isPassive = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM).toString());
            demand.forceAuthn = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM).toString());

            // Write serialized Demand bean to Redis
            final ObjectMapper mapper = new ObjectMapper();
            final String key = "key"; // TODO: Generate this randomly
            this.jedis.set(key, mapper.writeValueAsString(demand));

            response.sendRedirect("https://auth.localhost.demo.university/" + key);

        } catch (final ExternalAuthenticationException e) {
            throw new ServletException("Error processing external authentication request", e);
        }
    }

}
