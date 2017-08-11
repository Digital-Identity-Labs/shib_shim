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
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;


@WebServlet(
        name = "DepartureServlet",
        description = "Servlet to create auth demand, and redirect to auth service",
        urlPatterns = {"/Shim"},
        initParams = {
                @WebInitParam(name = "outgoingPath", value = "/login/authn/new/"),
                @WebInitParam(name = "propertiesFile", value = "shim.properties"),
                @WebInitParam(name = "failPath", value = "/500")
        }
)
public class DepartureServlet extends ShimServlet {

    public DepartureServlet() throws IOException {
        super();
    }


    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        try {

            Demand demand = processor.provision(request);

            storage.write(demand.id, demand.toJSON());

            response.sendRedirect("https://auth.localhost.demo.university/" + demand.id);

        } catch (final ExternalAuthenticationException e) {
            throw new ServletException("Error preparing external authentication request", e);
        }
    }

}
