package com.digitalidentitylabs.shabti.shim;

import java.util.UUID;
import javax.servlet.http.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({ "validAuthenticatedDemand" })
public class ShabtiShimDemand {

    // Internal protocol revision
    private static int shimModelVersion = 1;

    @JsonProperty("token") public String token = null;

    // Core Shibboleth authentication context stuff
    @JsonProperty("force")         protected Boolean forceAuthn   = false;
    @JsonProperty("passive")       protected Boolean isPassive    = false;
    @JsonProperty("method")        protected String  authnMethod;
    @JsonProperty("relying_party") protected String  relyingParty;

    // Metadata
    @JsonProperty("created_at")   protected DateTime createdAt;

    // User agent verification data
    @JsonProperty("user_address") protected String userAddress;
    @JsonProperty("agent_hash")   protected String agentHash;

    // Service information
    @JsonProperty("site_domain") protected String siteDomain;
    @JsonProperty("server_tag")  protected String serverTag;
    @JsonProperty("component")   protected String component;
    @JsonProperty("protocol")    protected String protocol;
    @JsonProperty("version")     protected int    version;

    // A bit of glue info so the secondary auth can redirect back to here
    @JsonProperty("return_url") protected String returnURL;

    // Make sure the principal is empty.
    @JsonProperty("principal")  protected String principal;

    // Status
    @JsonIgnore
    public    String validationMessage = "";


    // Build new object from request
    public ShabtiShimDemand(HttpServletRequest request) {

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        this.token = DigestUtils.md5Hex(uuid);

        // Core attributes
        forceAuthn   = request.getAttribute("forceAuthn")   == null ? false : (Boolean) request.getAttribute("forceAuthn");
        isPassive    = request.getAttribute("isPassive")    == null ? false : (Boolean) request.getAttribute("isPassive");
        authnMethod  = request.getAttribute("authnMethod")  == null ? null  : request.getAttribute("authnMethod").toString();
        relyingParty = request.getAttribute("relyingParty") == null ? null  : request.getAttribute("relyingParty").toString();

        // Metadata
        createdAt =  new DateTime(); //.toString(javascriptDateFormat);

        // User agent verification data
        userAddress = request.getRemoteAddr();
        agentHash   = DigestUtils.md5Hex(request.getHeader("User-Agent"));

        // Service information
        siteDomain = request.getServerName();
        serverTag   = "indiid";
        component   = "core";
        protocol    = "shibboleth";
        version     = shimModelVersion;

        // A bit of glue info so the secondary auth can redirect back to here
        returnURL    = request.getRequestURL().toString(); // JSON parser seems to choke without .toString...

        // Make sure the principal is empty.
        principal  = null;


    }

    public boolean isValidAuthenticatedDemand() {

        // Must have a valid and appropriate date (redundant?)
        // ...


        // Must have a token
        if (this.token == null || this.token.isEmpty()) {
            return false;
        }

        // Must have a supported serialisation version
        if (version > shimModelVersion ) {
            return false;
        }

        // Must be a Shibboleth demand
        if (! this.protocol.equals("shibboleth")) {
            return false;
        }

        // Must have a username!
        if (this.principal == null || this.principal.isEmpty()) {
            return false;
        }

        // Can't find any other faults, so probably OK.
        return true;

    }

    public String getValidationMessage() {

        return validationMessage;

    }


}
