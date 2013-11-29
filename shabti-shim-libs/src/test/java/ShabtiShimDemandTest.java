package com.digitalidentitylabs.shabti.shim;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.*;

public class ShabtiShimDemandTest {
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }


    @org.junit.Test
    public void thisAlwaysPasses() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        assertTrue(true);
    }


}
