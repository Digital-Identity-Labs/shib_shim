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
import java.net.URISyntaxException;
import java.util.Properties;
import java.io.InputStream;
import java.net.URI;


@WebServlet(
        name = "DepartureServlet",
        description = "Servlet to create auth demand, and redirect to auth service",
        urlPatterns = {"/Authn/shim/init"},
        initParams = {
                @WebInitParam(name = "propertiesFile", value = "shim.properties")
        }
)
public class DepartureServlet extends ShimServlet {

    public DepartureServlet() throws IOException {
        super();
    }

    public DepartureServlet(DemandStorage storage) throws IOException {
        super(storage);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        try {

            Demand demand = processor.provision(request);

            storage.write(demand);

            String authentication_url = buildAuthenticationURL(properties.getProperty("auth_url"), demand);

            log.info("Redirecting to external authentication service at {} for demand {}", authentication_url, demand.id );

            response.sendRedirect(authentication_url);

        } catch (final Exception e) {
            throw new ServletException("Error preparing external authentication request", e);
        }
    }

    protected String buildAuthenticationURL(String baseURL, Demand demand) throws URISyntaxException {

        URI url = new URI(baseURL + "/" + demand.id);
        url = url.normalize();

        return url.toString();
    }

}
