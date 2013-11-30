package com.digitalidentitylabs.shabti.shim;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.http.HttpServletRequest;

public class ShabtiShimDemandTest {

    private HttpServletRequest req;


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


    }

    @org.junit.After
    public void tearDown() throws Exception {

        req = null;

    }



    @org.junit.Test
    public void thisShouldPass() {

        assertTrue(true);
        assertThat( "Hello World", containsString("Hello") );

    }

    @org.junit.Test
    public void canCreateDemandFromRequest() {

        assertThat( new ShabtiShimDemand(req), instanceOf(ShabtiShimDemand.class) );

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

               /*

    @org.junit.Test
    public void demandShowsCreationTime() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsUserAddress() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsAgentHash() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsServiceSiteDomain() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsServerTag() {

        assertThat(  );


    }

    @org.junit.Test
    public void demandShowsProtocol() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsComponent() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsVersion() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsLatestVersion() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandShowsReturnURL() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandDoesNotShowPrincipalBeforeAuthention() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandIsNotValidAuthenticated() {

        assertThat(  );

    }

    @org.junit.Test
    public void demandIsN() {

        assertThat(  );

    }
    @org.junit.Test
    public void x() {

        assertThat(  );

    }

    @org.junit.Test
    public void x() {

        assertThat(  );

    }

    @org.junit.Test
    public void x() {

        assertThat(  );

    }


     */





}
