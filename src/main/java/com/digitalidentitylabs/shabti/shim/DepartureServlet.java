package com.digitalidentitylabs.shabti.shim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


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

            log.info("Properties:");
            log.info("are: {}", properties);
            log.info("url1: {}", properties.getProperty("return_url"));
            log.info("url2: {}", properties.getProperty("auth_url"));

            Demand demand = processor.provision(request);

            demand.returnURL = buildReturnURL(properties.getProperty("return_url"), demand, request);

            storage.write(demand);

            String authentication_url = buildAuthenticationURL(properties.getProperty("auth_url"), demand);

            log.info("Redirecting to external authentication service at {} for demand {}/{}", authentication_url, demand.id, demand.jobKey );

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

    protected String buildReturnURL(String baseURL, Demand demand, HttpServletRequest request) throws URISyntaxException {

        if (baseURL.toLowerCase().startsWith("http")) {
            baseURL = new URI(baseURL).normalize().toString();
        } else {
            baseURL = new URI(request.getScheme(), request.getServerName(), baseURL.toString()).normalize().toString();
        }

        return new URI(baseURL + "/" + demand.id).normalize().toString();
    }

}
