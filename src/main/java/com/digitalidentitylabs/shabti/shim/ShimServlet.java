package com.digitalidentitylabs.shabti.shim;

import net.shibboleth.idp.authn.ExternalAuthenticationException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class ShimServlet extends HttpServlet {

    protected Properties properties = new Properties();
    protected DemandStorage storage = null;
    protected ShibDemandProcessor processor = new ShibDemandProcessor();

    public void init() throws ServletException {

        try {

            super.init();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(getInitParameter("propertiesFile"));
            Properties properties = new Properties();
            properties.load(input);

            storage = storage == null ? getStorage(properties) : storage;



        } catch (Exception e) {
            throw new ServletException("Error creating Shabti Shim servlet!", e);
        }

    }

    public ShimServlet() {

        super();

    }

    public ShimServlet(final DemandStorage storage) {

        this.storage = storage;

    }

    private DemandStorage getStorage(Properties props) {

        DemandStorage demandStorage = null;

        if (StringUtils.isBlank(props.getProperty("password"))) {

            demandStorage = new DemandStorage(
                    props.getProperty("redis_host"),
                    Integer.parseInt(props.getProperty("redis_port")));

        } else {

            demandStorage = new DemandStorage(
                    props.getProperty("redis_host"),
                    Integer.parseInt(props.getProperty("redis_port")),
                    props.getProperty("password"));
        }

        return demandStorage;

    }

}
