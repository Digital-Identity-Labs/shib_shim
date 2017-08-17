package com.digitalidentitylabs.shabti.shim;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ShimServlet extends HttpServlet {

    protected Properties properties = new Properties();
    protected DemandStorage storage = null;
    protected ShibDemandProcessor processor = new ShibDemandProcessor();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void init() throws ServletException {

        try {

            super.init();

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(getInitParameter("propertiesFile"));
            Properties properties = new Properties();
            properties.load(input);

            storage = storage == null ? getStorage(properties) : storage;

            log.info("Here we go!");

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

        String  hostname = props.getProperty("redis_hostname");
        Integer port     = Integer.parseInt(props.getProperty("redis_port"));
        String  secret   = props.getProperty("password");

        if (StringUtils.isBlank(secret)) {
            log.info("Connecting to Redis service {} on port {}", hostname, port );
            demandStorage = new DemandStorage(hostname, port);
        } else {
            log.info("Connecting to Redis service {} on port {} with password [XXXXXXXXXX]", hostname, port );
            demandStorage = new DemandStorage(hostname, port, secret);
        }

        return demandStorage;

    }

}
