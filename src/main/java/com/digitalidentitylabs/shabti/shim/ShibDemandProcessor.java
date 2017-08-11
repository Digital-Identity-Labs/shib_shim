package com.digitalidentitylabs.shabti.shim;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;

public class ShibDemandProcessor {

    public Demand provision(HttpServletRequest request) throws ExternalAuthenticationException {

        final Demand demand = new Demand();

        // Start the external authentication process and get an ID for this authentication job
        demand.jobKey = ExternalAuthentication.startExternalAuthentication(request);

        // Extract various details from the IdP that the authenticator will need (these are the older style)
        demand.relyingParty = request.getAttribute(ExternalAuthentication.RELYING_PARTY_PARAM).toString();
        demand.authnMethod  = request.getAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM).toString();
        demand.isPassive    = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM).toString());
        demand.forceAuthn   = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM).toString());

        return demand;

    }

    public Demand authenticate(String token, String record) {

        Demand demand = null;

        return demand;

    }



}
