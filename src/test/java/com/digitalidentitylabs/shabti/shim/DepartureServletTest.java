package com.digitalidentitylabs.shabti.shim;

import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import redis.clients.jedis.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class DepartureServletTest {

    private JedisPool jedisPool;
    private DepartureServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @Before
    public void setUp() {
        jedisPool = Mockito.mock(JedisPool.class);
        servlet = new DepartureServlet(jedisPool);
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

//    @Test
//    public void testSetsSensibleKeyOnRedisObject() throws IOException, ServletException {
//
//        final ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
//
//        servlet.doGet(request, response);
//
//        verify(jedisPool).set(nameCaptor.capture(), anyString());
//
//        // This could do a regex to check for hex
//        assertNotNull(nameCaptor.getValue());
//
//    }
//
//    @Test
//    public void testSetRelyingPartyProperlyOnRedisObject() throws IOException, ServletException {
//
//        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
//
//        servlet.doGet(request, response);
//
//        verify(jedisPool).set(anyString(), valueCaptor.capture());
//
//        // Parse key sent to Redis assuming JSON
//        final ObjectMapper mapper = new ObjectMapper();
//        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());
//
//        final ObjectNode demand = (ObjectNode) jsonNode;
//        assertEquals("http://example.com/rp", demand.get("relying_party").asText());
//
//    }
//
//    @Test
//    public void testSetMethodProperlyOnRedisObject() throws IOException, ServletException {
//
//        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
//
//        servlet.doGet(request, response);
//
//        verify(jedisPool).set(anyString(), valueCaptor.capture());
//
//        // Parse key sent to Redis assuming JSON
//        final ObjectMapper mapper = new ObjectMapper();
//        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());
//
//        final ObjectNode demand = (ObjectNode) jsonNode;
//        assertEquals("http://example.com/password", demand.get("method").asText());
//
//    }
//
//    @Test
//    public void testSetPassiveProperlyOnRedisObject() throws IOException, ServletException {
//
//        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
//
//        servlet.doGet(request, response);
//
//        verify(jedisPool).set(anyString(), valueCaptor.capture());
//
//        // Parse key sent to Redis assuming JSON
//        final ObjectMapper mapper = new ObjectMapper();
//        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());
//
//        final ObjectNode demand = (ObjectNode) jsonNode;
//        assertEquals(true, demand.get("passive").asBoolean());
//
//    }
//
//    @Test
//    public void testSetForceProperlyOnRedisObject() throws IOException, ServletException {
//
//        final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
//
//        servlet.doGet(request, response);
//
//        verify(jedisPool).set(anyString(), valueCaptor.capture());
//
//        // Parse key sent to Redis assuming JSON
//        final ObjectMapper mapper = new ObjectMapper();
//        final JsonNode jsonNode = mapper.reader().readTree(valueCaptor.getValue());
//
//        final ObjectNode demand = (ObjectNode) jsonNode;
//        assertEquals(true, demand.get("force").asBoolean());
//
//    }
}
