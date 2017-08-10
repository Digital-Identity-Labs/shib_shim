package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Demand {

    @JsonProperty("external_auth_key") protected String  externalAuthKey = "";
    @JsonProperty("force")             protected Boolean forceAuthn = false;
    @JsonProperty("passive")           protected Boolean isPassive = false;
    @JsonProperty("method")            protected String  authnMethod = "";
    @JsonProperty("relying_party")     protected String  relyingParty = "";
    @JsonProperty("principal")         protected String  principal = "";


    public String toJSON() {

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
