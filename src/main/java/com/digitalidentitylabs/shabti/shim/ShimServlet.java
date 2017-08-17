package com.digitalidentitylabs.shabti.shim;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ShimServlet extends HttpServlet {

    protected Properties properties = null;
    protected DemandStorage storage = null;
    protected ShibDemandProcessor processor = new ShibDemandProcessor();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void init() throws ServletException {

        try {

            super.init();

            properties = properties == null ? setupProperties(getInitParameter("propertiesFile")) : properties;
            storage    = storage    == null ? setupStorage(properties) : storage;

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

    protected Properties setupProperties(String propFile) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(propFile);
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading properties file {}!", propFile);
            throw e;
        }
        return properties;
    }

    private DemandStorage setupStorage(Properties props) {

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
