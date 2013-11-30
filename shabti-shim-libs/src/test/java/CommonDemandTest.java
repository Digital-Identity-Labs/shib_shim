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


public class CommonDemandTest {

    protected HttpServletRequest req;
    protected ServletConfig config;

    @org.junit.Before
    public void setUp() throws Exception {


        this.req       = mock(HttpServletRequest.class);

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


}
