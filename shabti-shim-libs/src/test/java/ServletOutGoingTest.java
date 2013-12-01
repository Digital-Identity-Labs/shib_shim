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

import com.digitalidentitylabs.shabti.shim.ShabtiShimServlet;


public class ServletOutGoingTest {


    private ShabtiShimServlet   servlet;
    private HttpServletRequest  request;
    private HttpServletResponse response;

    @Before
    public void setUp() {

        servlet =  new ShabtiShimServlet();
        request =  mock(HttpServletRequest.class);
        //response = new HttpServletResponse();
    }

    @Test
    public void redirectWorks() {


       // servlet.doGet(request, response);

        //assertEquals("text/html", response.getContentType());
      //  assertEquals("/maintenance.jsp",response.getLastRedirect());

    }
}
