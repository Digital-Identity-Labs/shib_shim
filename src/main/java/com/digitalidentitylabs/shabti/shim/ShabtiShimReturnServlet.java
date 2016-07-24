package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ShabtiShimReturnServlet", urlPatterns = {"/Authn/Shim/Return"})
public class ShabtiShimReturnServlet extends HttpServlet {

    private final Jedis jedis;

    public ShabtiShimReturnServlet() {
        jedis = new Jedis("redis");
    }

    public ShabtiShimReturnServlet(final Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final ObjectMapper mapper = new ObjectMapper();
        final ShimDemand demand = mapper.readValue(jedis.get("key"), ShimDemand.class);

        HttpSession session = request.getSession();
        session.setAttribute("conversationemyconv1", new ExternalAuthentication() {
            @Override
            protected void doStart(HttpServletRequest request) throws ExternalAuthenticationException {
                // surely this needs to be real?
            }
        });

        try {

            ExternalAuthentication.finishExternalAuthentication(demand.externalAuthKey, request, response);

        } catch (ExternalAuthenticationException e) {

            throw new ServletException(e);

        }

    }


}