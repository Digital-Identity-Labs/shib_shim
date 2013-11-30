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


public class DemandStateValidationTest extends CommonDemandTest {

    @org.junit.Test
    public void demandIsNotValidAuthenticatedWhenCreatedFromRequest() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);
        assertFalse( demand.isValidAndAuthenticated() );

    }

    @org.junit.Test
    public void demandIsValidOutgoingWhenCreatedFromRequest() {

        ShabtiShimDemand demand = new ShabtiShimDemand(req);
        assertTrue( demand.isValidOutgoing() );

    }


}

