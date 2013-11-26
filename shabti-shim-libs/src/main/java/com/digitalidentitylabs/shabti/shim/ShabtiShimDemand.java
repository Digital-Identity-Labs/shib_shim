package com.digitalidentitylabs.shabti.shim;

import java.util.UUID;
import javax.servlet.http.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;


public class ShabtiShimDemand {

    // Internal protocol revision
    private static int shimModelVersion = 1;

    public String token = null;

    // Core Shibboleth authentication context stuff
    protected Boolean forceAuthn   = false;
    protected Boolean isPassive    = false;
    protected String  authnMethod;
    protected String  relyingParty;

    // Metadata
    protected DateTime createdAt;

    // User agent verification data
    protected String userAddress;
    protected String agentHash;

    // Service information
    protected String siteDomain;
    protected String serverTag;
    protected String component;
    protected String protocol;
    protected int    version;

    // A bit of glue info so the secondary auth can redirect back to here
    protected String returnURL;

    // Make sure the principal is empty.
    protected String principal;

    // Status
    public    String validationMessage = "";


    // Build new object from request
    public ShabtiShimDemand(HttpServletRequest request) {

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        this.token = DigestUtils.md5Hex(uuid);

        // Core attributes
        forceAuthn   = (Boolean) request.getAttribute("forceAuthn");
        isPassive    = (Boolean) request.getAttribute("isPassive");
        authnMethod  = request.getAttribute("authnMethod")  == null ? null :  request.getAttribute("authnMethod").toString();
        relyingParty = request.getAttribute("relyingParty") == null ? null :  request.getAttribute("relyingParty").toString();

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

        return "There have been errors";

    }


}
