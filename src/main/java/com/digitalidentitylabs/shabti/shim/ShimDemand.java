package com.digitalidentitylabs.shabti.shim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShimDemand {

    @JsonProperty("external_auth_key")        protected String  externalAuthKey = "";
    @JsonProperty("force")         protected Boolean forceAuthn = false;
    @JsonProperty("passive")       protected Boolean isPassive = false;
    @JsonProperty("method")        protected String  authnMethod = "";
    @JsonProperty("relying_party") protected String  relyingParty = "";
    @JsonProperty("principal")        protected String  principal = "";
}
