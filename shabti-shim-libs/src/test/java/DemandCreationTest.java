package com.digitalidentitylabs.shabti.shim;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.mockito.internal.matchers.Matches;

import java.util.regex.*;


public class DemandCreationTest {

    private HttpServletRequest req;
    private ServletConfig config;

    @org.junit.Before
    public void setUp() throws Exception {

        this.req = mock(HttpServletRequest.class);

        when(req.getAttribute("forceAuthn")).thenReturn("false");
        when(req.getAttribute("isPassive")).thenReturn("false");
        when(req.getAttribute("authnMethod")).thenReturn(null);
        when(req.getAttribute("relyingParty")).thenReturn("https://service.example.com/shibboleth/sp");
        when(req.getRemoteAddr()).thenReturn("192.168.1.1");
        when(req.getHeader("User-Agent")).thenReturn("It's only a model");
        when(req.getServerName()).thenReturn("example.com");
        when(req.getRequestURL()).thenReturn(new StringBuffer("https://service.example.com/servlet/"));


        this.config = mock(ServletConfig.class);


    }

    @org.junit.After
    public void tearDown() throws Exception {

        this.req    = null;
        this.config = null;

    }

    @org.junit.Test
    public void canCreateDemandFromRequest() {

        assertThat( new ShabtiShimDemand(req), instanceOf(ShabtiShimDemand.class) );

    }

    @org.junit.Test
    public void demandHasToken() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat(  demand.getToken(), instanceOf(String.class) );

    }

    @org.junit.Test
    public void demandHasLargeHexToken() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        // I assume Sun added regexes to Java on the condition that they were as awkwardly implemented as possible
        Pattern md5Regex = Pattern.compile("^([a-fA-F\\d]{32})$");
        Matcher md5Matcher = md5Regex.matcher(demand.getToken());

        assertTrue( md5Matcher.matches() );
    }

    @org.junit.Test
    public void demandHasUniqueTokenEvenForSameRequest() {

        ShabtiShimDemand demand1 = new ShabtiShimDemand(req);
        ShabtiShimDemand demand2 = new ShabtiShimDemand(req);

        assertThat( demand1.getToken(), not(equalTo(demand2.getToken())));

    }

    @org.junit.Test
    public void demandShowsInactiveForcedAuthByDefault() {

        when(req.getAttribute("forceAuthn")).thenReturn(null);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertFalse( demand.getForceAuthn() );

    }


    @org.junit.Test
    public void demandShowsActiveForcedAuth() {

        when(req.getAttribute("forceAuthn")).thenReturn("true");
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertTrue( demand.getForceAuthn() );

    }

    @org.junit.Test
    public void demandShowsInactiveForcedAuth() {

        when(req.getAttribute("forceAuthn")).thenReturn("false");
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertFalse( demand.getForceAuthn() );

    }

    @org.junit.Test
    public void demandShowsInactivePassiveAuthByDefault() {

        when(req.getAttribute("isPassive")).thenReturn("false");
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertFalse( demand.getIsPassive() );

    }


    @org.junit.Test
    public void demandShowsActivePassiveAuth() {

        when(req.getAttribute("isPassive")).thenReturn(null);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertFalse( demand.getIsPassive() );


    }

    @org.junit.Test
    public void demandShowsInactivePassiveAuth() {

        when(req.getAttribute("isPassive")).thenReturn("false");
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertFalse( demand.getIsPassive() );

    }

    @org.junit.Test
    public void demandShowsAuthnMethodAsNullBeforeAuth() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat(demand.getAuthnMethod(), is(nullValue()) );

    }

    @org.junit.Test
    public void demandShowsRelyingParty() {

        String rpURL = "https://mysp.com/saml";
        when( req.getAttribute("relyingParty") ).thenReturn(rpURL);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getRelyingParty(), equalTo(rpURL) );

    }

    @org.junit.Test
    public void demandShowsCreationTime() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);
        DateTime timeNow = new DateTime();
        assertThat( demand.getCreatedAt().minuteOfDay(), equalTo(timeNow.minuteOfDay()) ); // FIX: This is not very good, really.

    }

    @org.junit.Test
    public void demandShowsUserAddress() {

        String address = "10.180.15.4";
        when(req.getRemoteAddr()).thenReturn(address);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getUserAddress(), equalTo(address) );

    }

    @org.junit.Test
    public void demandShowsAgentHash() {

        String agentString = "IBrowse/2.3 (AmigaOS 3.9)";
        String agentHash   = DigestUtils.md5Hex(agentString);
        when(req.getHeader("User-Agent")).thenReturn(agentString);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getAgentHash(), equalTo(agentHash) );

    }


    @org.junit.Test
    public void demandShowsServiceSiteDomain() {

        String domain = "worldofpossums.net";

        when(req.getServerName()).thenReturn(domain);
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getSiteDomain(), equalTo(domain) );

    }

    @org.junit.Test
    public void demandShowsLatestVersion() {

        int version = 1;

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getVersion(), equalTo(version) );

    }

    @org.junit.Test
    public void demandShowsReturnURL() {

        String servletURL = "https://sp.example.com/shim/";
        when(req.getRequestURL()).thenReturn(new StringBuffer(servletURL));
        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getReturnURL(), equalTo(servletURL) );

    }

    @org.junit.Test
    public void demandDoesNotShowPrincipalBeforeAuthention() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);
        assertThat( demand.getPrincipal(), is(nullValue()) );

    }

    @org.junit.Test
    public void demandIsNotValidAuthenticated() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);
        assertFalse( demand.isValidAuthenticatedDemand() );

    }

    @org.junit.Test
    public void demandShowsServerTagDefault() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getServerTag(), equalTo("service") );


    }


    @org.junit.Test
    public void demandShowsProtocolDefault() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getProtocol(), equalTo("shibboleth") );

    }


    @org.junit.Test
    public void demandShowsComponentDefault() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);

        assertThat( demand.getComponent(), equalTo("core") );

    }

     /*



    @org.junit.Test
    public void demandShowsServerTagFromParams() {

        assertThat(  );


    }



    @org.junit.Test
    public void demandShowsProtocolFromParams() {

        assertThat(  );

    }


    @org.junit.Test
    public void demandShowsComponentFromParams() {

        assertThat(  );

    }




     */





}
