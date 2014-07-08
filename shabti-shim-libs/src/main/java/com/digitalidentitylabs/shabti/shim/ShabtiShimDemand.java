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

@JsonIgnoreProperties({ "validAndAuthenticated", "validOutgoing", "validIncoming"})
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

    public ShabtiShimDemand() {
        // Dummy constructor
    }

    // Build new object from request
    public ShabtiShimDemand(HttpServletRequest request) {

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        this.token = DigestUtils.md5Hex(uuid);

        // Core attributes
        forceAuthn   = (request.getAttribute("forceAuthn"))   == null ? false : Boolean.valueOf((String) request.getAttribute("forceAuthn").toString());
        isPassive    = (request.getAttribute("isPassive"))    == null ? false : Boolean.valueOf((String) request.getAttribute("isPassive").toString());
        authnMethod  = (request.getAttribute("authnMethod")   == null) ? null  : (String)  request.getAttribute("authnMethod");
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

    private boolean isValidCommon() {

        // Must always have a token
        if (this.getToken() == null || this.getToken().isEmpty()) {
            setValidationMessage("Missing token!");
            return false;
        }

        // Must have a supported serialisation version
        if (getVersion() > getShimModelVersion()) {
            setValidationMessage("Incorrect demand version");
            return false;
        }

        // Must match protocol of this instance of class
        if (! this.getProtocol().equals("shibboleth")) {
            setValidationMessage("Incorrect demand protocol type");
            return false;
        }

        return true;

    }

    public boolean isValidOutgoing() {

        if (! this.isValidCommon()) {
            return false;
        }

        // Must *not* have a username!
        if (this.getPrincipal() != null) {
            setValidationMessage("Principal was set - demand is not new?");
            return false;
        }

        return true;

    }

    public boolean isValidIncoming() {

        if (! this.isValidCommon()) {
            return false;
        }

        return true;

    }

    public boolean isValidAndAuthenticated() {

        if (! this.isValidIncoming()) {
            return false;
        }

        // Must have a username! Existence of principal indicates authentication
        if (this.getPrincipal() == null || this.getPrincipal().isEmpty()) {
            setValidationMessage("Principal was not set: not authenticated");
            return false;
        }

        // Can't find any other faults, so probably OK.
        return true;

    }

    private void setValidationMessage(String message) {

        validationMessage = message;

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
