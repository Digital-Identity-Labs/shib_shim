package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShabtiShimReturnTest {

    private Jedis jedis;
    private ShabtiShimReturnServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @Before
    public void setUp() {
        jedis = Mockito.mock(Jedis.class);
        servlet = new ShabtiShimReturnServlet(jedis);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setPathInfo("/Authn/Shim/Return");
        request.setParameter("token", "key");
    }

    //@Test
    public void test() throws IOException, ServletException {

        final ObjectMapper mapper = new ObjectMapper();
        ShimDemand demand = new ShimDemand();
        demand.externalAuthKey = "key";

        when(jedis.get("key")).thenReturn(mapper.writeValueAsString(demand));

        servlet.doGet(request, response);

        // TODO: what can we test?

    }

}
