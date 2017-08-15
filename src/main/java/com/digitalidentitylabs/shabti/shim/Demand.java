package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.codec.digest.DigestUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Demand {

    @JsonProperty("token")             protected String  id           = null;
    @JsonProperty("auth_key")          protected String  jobKey       = null;

    @JsonProperty("force")             protected Boolean forceAuthn   = false;
    @JsonProperty("passive")           protected Boolean isPassive    = false;
    @JsonProperty("method")            protected String  authnMethod  = null;
    @JsonProperty("relying_party")     protected String  relyingParty = "";

    @JsonProperty("principal")         protected String  principal    = null;

    @JsonProperty("created_at")        protected DateTime createdAt  = null;

    @JsonProperty("user_address")      protected String  userAddress  = null;
    @JsonProperty("agent_hash")        protected String  agentHash    = null;
    @JsonProperty("site_domain")       protected String  siteDomain   = null;
    @JsonProperty("server_tag")        protected String  serverTag    = "service";
    @JsonProperty("component")         protected String  component    = "core";
    @JsonProperty("protocol")          protected String  protocol     = "shibboleth";
    @JsonProperty("version")           protected String  version      = "1.0.0";
    @JsonProperty("return_url")        protected String  returnURL    = null;

    private   DateTimeFormatter javascriptDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z");
    protected ObjectMapper mapper = new ObjectMapper();


    // Build new object from request
    public Demand() {

        mapper.registerModule(new JodaModule());
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        this.id = DigestUtils.md5Hex(uuid);

        // Metadata
        this.createdAt =  new DateTime(); //.toString(javascriptDateFormat);

    }

    public Demand(String jsonText) {
        this();
        this.id        = null;
        this.createdAt = null;
        try {
            mapper.readerForUpdating(this).readValue(jsonText);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public String toJSON() {

        String json = null;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}

//            final ObjectMapper mapper = new ObjectMapper();

//            return mapper.readValue(jsonText, Demand.class);