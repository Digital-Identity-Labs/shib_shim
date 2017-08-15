package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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

        final String demandId = request.getParameter("token");

        final Demand demand = storage.read(demandId);

        try {
            processor.authenticate(demand, request, response);
        } catch (ExternalAuthenticationException e) {
            throw new ServletException("Error authenticating external authentication details", e);
        }

    }



}
