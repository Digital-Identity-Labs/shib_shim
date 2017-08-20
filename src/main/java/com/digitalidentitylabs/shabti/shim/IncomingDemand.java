package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.codec.digest.DigestUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomingDemand extends Demand {

    @JsonProperty("dnc")               protected Boolean doNotCache  = true;


    @JsonProperty("server_tag")        protected String  serverTag    = "service";
    @JsonProperty("component")         protected String  component    = "core";

    @JsonProperty("user_address")      protected String  userAddress  = null;
    @JsonProperty("site_domain")       protected String  siteDomain   = null;


    // Build new object from request
    public IncomingDemand() {
        super();
    }

    public IncomingDemand(String jsonText) {
        this();
        this.id        = null;
        this.createdAt = null;

        mapper.registerModule(new JodaModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

        try {
            mapper.readerForUpdating(this).readValue(jsonText);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isValid() {
        if (principal == null) { return false; };
        return true;
    }

    @JsonIgnore @Override public DemandState state() {
        if (!isValid()) { return DemandState.INVALID; }
        if (!errorMessage.toString().isEmpty()) { return DemandState.REJECTED; }
        if (!principal.toString().isEmpty())    { return DemandState.ACCEPTED; }
        return DemandState.INVALID;
    }

}
