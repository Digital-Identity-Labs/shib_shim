package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Demand {

    protected static final String DEMAND_VERSION = "2.0.0";

    public enum DemandState {
        INVALID, WAITING, ACCEPTED, REJECTED, MISSING
    }

    @JsonProperty("token")             protected String  id           = null;
    @JsonProperty("auth_key")          protected String  jobKey       = null;

    @JsonProperty("force")             protected Boolean forceAuthn   = false;
    @JsonProperty("passive")           protected Boolean isPassive    = false;
    @JsonProperty("method")            protected String  authnMethod  = null;
    @JsonProperty("relying_party")     protected String  relyingParty = "";

    @JsonProperty("principal")         protected String  principal    = null;
    @JsonProperty("error_message")     protected String  errorMessage = "";

    @JsonProperty("created_at")        protected DateTime  createdAt  = null;
    @JsonProperty("updated_at")        protected DateTime  updatedAt  = null;
    @JsonProperty("decided_at")        protected DateTime decidedAt  = null;

    @JsonProperty("protocol")          protected String  protocol     = "shibboleth";
    @JsonProperty("version")           protected String  version      = DEMAND_VERSION;
    @JsonProperty("return_url")        protected String  returnURL    = null;

    @JsonProperty("dnc")               protected Boolean doNotCache  = true;


    @JsonProperty("server_tag")        protected String  serverTag    = "service";
    @JsonProperty("component")         protected String  component    = "core";

    @JsonProperty("user_address")      protected String  userAddress  = null;
    @JsonProperty("site_domain")       protected String  siteDomain   = null;


    protected DateTimeFormatter javascriptDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z");
    protected ObjectMapper mapper = new ObjectMapper();


    // Build new object from request
    public Demand() {

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

    @JsonIgnore public DemandState state() {
        return DemandState.INVALID;
    }

    @JsonIgnore public boolean isValid() {
        return false;
    }

}
