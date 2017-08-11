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
        urlPatterns = {"/Authn/Shim/Return"},
        initParams={
                @WebInitParam(name="failPath",      value="/500"),
                @WebInitParam(name="propertiesFile", value="shim.properties")
        }
)
public class ReturnServlet extends ShimServlet {

    public ReturnServlet() throws IOException {
        super();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final String demandId = request.getParameter("token");

        final Demand demand = storage.read(demandId);

        HttpSession session = request.getSession();
        session.setAttribute("conversationemyconv1", new ExternalAuthentication() {
            @Override
            protected void doStart(HttpServletRequest request) throws ExternalAuthenticationException {
                // surely this needs to be real?
            }
        });

        try {

            request.setAttribute(ExternalAuthentication.PRINCIPAL_NAME_KEY, demand.principal);

            ExternalAuthentication.finishExternalAuthentication(demand.jobKey, request, response);

        } catch (ExternalAuthenticationException e) {

            throw new ServletException(e);

        }

    }



}
