package com.digitalidentitylabs.shabti.shim;

import net.shibboleth.idp.authn.ExternalAuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@WebServlet(
        name = "ReturnServlet",
        description = "Servlet to accept and process completed demand from an authentication service",
        urlPatterns = {"/Authn/shim/return/*"},
        initParams={
                @WebInitParam(name="propertiesFile", value="shim.properties")
        }
)
public class ReturnServlet extends ShimServlet {

    public ReturnServlet() throws IOException {
        super();
    }

    public ReturnServlet(DemandStorage storage) throws IOException {
        super(storage);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final String token = extractTokenFromURL(request.getRequestURL().toString());

        log.info("Authentication using demand {} has been requested...", token);

        final Demand demand = storage.read(token);

        try {

            if (processor.isSatisfied(demand)) {
                log.info("Authenticating demand {}/{} for user {}", demand.id, demand.jobKey, demand.principal);
                processor.authnSuccess(demand, request);
            } else {

                switch (demand.state()){
                    case REJECTED:
                        log.error("Rejected authentication demand {} - {}", demand.id, demand.errorMessage);
                        processor.authnError(demand, request);
                        break;
                    case MISSING:
                        log.error("Expired/missing demand {}!", demand.id);
                        throw new ServletException("Expired Demand!");
                    default:
                        log.error("Invalid demand {}!", demand.id);
                        throw new ServletException("Invalid Demand!");
                }


            }

            processor.finish(demand, request, response);

        } catch (ExternalAuthenticationException e) {
            throw new ServletException("Error authenticating external authentication details", e);
        } finally {
            storage.delete(demand);
        }

    }

    protected String extractTokenFromURL(String raw_url) {

        URI uri = null;
        try {
            uri = new URI(raw_url);
        } catch (URISyntaxException e) {
            return "error";
        }
        String[] segments = uri.getPath().split("/");
        String token = segments[segments.length-1];

        return token;

    }


}
