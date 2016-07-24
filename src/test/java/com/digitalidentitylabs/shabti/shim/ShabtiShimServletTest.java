package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.core.CollectionFactory;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

public class ShabtiShimServletTest {

    private Jedis jedis;
    private ShabtiShimServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @Before
    public void setUp() {
        jedis = Mockito.mock(Jedis.class);
        servlet = new ShabtiShimServlet(jedis);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // ExternalAuthentication.startExternalAuthentication insists we
        // have the conversation param on the URL and a matching key in the
        // session... this was unpicked by trial and error -- no docs?
        final HttpSession session = request.getSession();
        session.setAttribute("conversationmyconv1", new ExternalAuthentication() {
            @Override
            protected void doStart(HttpServletRequest request) throws ExternalAuthenticationException {
                // Seems to pass with this doing nothing... whatever
            }
        });
        request.setPathInfo("/Authn/Shim");
        request.setParameter("conversation", "myconv1");
        request.setAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM, true);
        request.setAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM, true);
        request.setAttribute(ExternalAuthentication.RELYING_PARTY_PARAM, "http://example.com/rp");
        request.setAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM, "http://example.com/password");
    }

    @Test
    public void testSetsSensibleKeyOnRedisObject() throws IOException, ServletException {

        final ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        servlet.doGet(request, response);

        verify(jedis).set(nameCaptor.capture(), anyString());

        // This could do a regex to check for hex
        assertNotNull(nameCaptor.getValue());

    }

    @Test
    public void testSetRelyingPartyProperlyOnRedisObject() throws IOException, ServletException {

        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        servlet.doGet(request, response);

        verify(jedis).set(anyString(), valueCaptor.capture());

        // Parse key sent to Redis assuming JSON
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());

        final ObjectNode demand = (ObjectNode) jsonNode;
        assertEquals("http://example.com/rp", demand.get("relying_party").asText());

    }

    @Test
    public void testSetMethodProperlyOnRedisObject() throws IOException, ServletException {

        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        servlet.doGet(request, response);

        verify(jedis).set(anyString(), valueCaptor.capture());

        // Parse key sent to Redis assuming JSON
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());

        final ObjectNode demand = (ObjectNode) jsonNode;
        assertEquals("http://example.com/password", demand.get("method").asText());

    }

    @Test
    public void testSetPassiveProperlyOnRedisObject() throws IOException, ServletException {

        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        servlet.doGet(request, response);

        verify(jedis).set(anyString(), valueCaptor.capture());

        // Parse key sent to Redis assuming JSON
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());

        final ObjectNode demand = (ObjectNode) jsonNode;
        assertEquals(true, demand.get("passive").asBoolean());

    }

    @Test
    public void testSetForceProperlyOnRedisObject() throws IOException, ServletException {

        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        servlet.doGet(request, response);

        verify(jedis).set(anyString(), valueCaptor.capture());

        // Parse key sent to Redis assuming JSON
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());

        final ObjectNode demand = (ObjectNode) jsonNode;
        assertEquals(true, demand.get("force").asBoolean());

    }
}
