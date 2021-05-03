package com.digitalidentitylabs.shabti.shim;

//import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
            storage = storage == null ? setupStorage(properties) : storage;

            log.info("External Authentication Shim service {} is available", getClass().toString());

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

    protected Properties defaultProperties() {

        Properties defaults = new Properties();
        defaults.setProperty("redis_hostname", "127.0.0.1");
        defaults.setProperty("redis_port", "6379");
        defaults.setProperty("redis_password", "");
        defaults.setProperty("auth_url", "/login/authn/new/");
        defaults.setProperty("return_url", "/idp/Authn/shim/return/");
        defaults.setProperty("x-check", "true");

        return defaults;
    }

    protected Properties setupProperties(String propFile) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(propFile);
        Properties properties = new Properties(defaultProperties());
        try {
            if (input != null) {
                properties.load(input);
            } else {
                defaultProperties();
            }
        } catch (IOException e) {
            log.error("Error loading properties file {}!", propFile);
            throw e;
        }
        return properties;
    }

    private DemandStorage setupStorage(Properties props) {

        DemandStorage demandStorage = null;

        String hostname = props.getProperty("redis_hostname");
        Integer port = Integer.parseInt(props.getProperty("redis_port"));
        String secret = props.getProperty("password");

        log.info("Hostname: {}", hostname);
        log.info("port: {}", port);
        log.info("secret: {}", secret);

        if (secret == null || secret.equals("")) {
            log.debug("Connecting to Redis service {} on port {}", hostname, port);
            demandStorage = new DemandStorage(hostname, port);
        } else {
            log.debug("Connecting to Redis service {} on port {} with password [XXXXXXXXXX]", hostname, port);
            demandStorage = new DemandStorage(hostname, port, secret);
        }

        return demandStorage;

    }


}
