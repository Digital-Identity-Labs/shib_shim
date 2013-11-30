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
    @JsonProperty("force")         protected Boolean forceAuthn;
    @JsonProperty("passive")       protected Boolean isPassive;
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
        forceAuthn   = (request.getAttribute("forceAuthn"))   == null ? false : Boolean.valueOf((String) request.getAttribute("forceAuthn"));
        isPassive    = (request.getAttribute("isPassive"))    == null ? false : Boolean.valueOf((String) request.getAttribute("isPassive"));
        authnMethod  = (request.getAttribute("authnMethod")  == null) ? null  : (String)  request.getAttribute("authnMethod");
        relyingParty = ((request.getAttribute("relyingParty") == null) ? "EH" : request.getAttribute("relyingParty").toString());

        // Metadata
        createdAt =  new DateTime(); //.toString(javascriptDateFormat);

        // User agent verification data
        userAddress = request.getRemoteAddr();
        agentHash   = DigestUtils.md5Hex(request.getHeader("User-Agent"));

        // Service information
        siteDomain = request.getServerName();
        serverTag   = "service";
        component   = "core";
        protocol    = "shibboleth";
        version     = getShimModelVersion();

        // A bit of glue info so the secondary auth can redirect back to here
        returnURL    = request.getRequestURL().toString(); // JSON parser seems to choke without .toString...

        // Make sure the principal is empty.
        principal  = null;


    }

    public static int getShimModelVersion() {
        return shimModelVersion;
    }

    public boolean isValidAuthenticatedDemand() {

        // Must have a valid and appropriate date (redundant?)
        // ...


        // Must have a token
        if (this.getToken() == null || this.getToken().isEmpty()) {
            return false;
        }

        // Must have a supported serialisation version
        if (getVersion() > getShimModelVersion()) {
            return false;
        }

        // Must be a Shibboleth demand
        if (! this.getProtocol().equals("shibboleth")) {
            return false;
        }

        // Must have a username!
        if (this.getPrincipal() == null || this.getPrincipal().isEmpty()) {
            return false;
        }

        // Can't find any other faults, so probably OK.
        return true;

    }

    public String getValidationMessage() {

        return validationMessage;

    }


    public String getToken() {
        return token;
    }

    public Boolean getForceAuthn() {
        return forceAuthn;
    }

    public Boolean getIsPassive() {
        return isPassive;
    }

    public String getAuthnMethod() {
        return authnMethod;
    }

    public String getRelyingParty() {
        return relyingParty;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public String getSiteDomain() {
        return siteDomain;
    }

    public String getServerTag() {
        return serverTag;
    }

    public String getComponent() {
        return component;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getVersion() {
        return version;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public String getPrincipal() {
        return principal;
    }
}
