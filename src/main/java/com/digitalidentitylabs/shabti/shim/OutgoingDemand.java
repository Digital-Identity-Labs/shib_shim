package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OutgoingDemand extends Demand {

    @JsonIgnoreProperties(ignoreUnknown = true)

    // Build new object from request
    public OutgoingDemand() {

        super();

        mapper.registerModule(new JodaModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        this.id = DigestUtils.md5Hex(uuid);

        // Metadata
        this.createdAt = new DateTime();//.toString(javascriptDateFormat);

    }

    @Override @JsonIgnore
    public boolean isValid() {
        if (principal != null) { return false; };
        return true;
    }

}
