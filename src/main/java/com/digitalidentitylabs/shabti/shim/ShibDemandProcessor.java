package com.digitalidentitylabs.shabti.shim;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;

import java.io.IOException;

public class ShibDemandProcessor {

    public Demand provision(HttpServletRequest request) throws ExternalAuthenticationException {

        final Demand demand = new OutgoingDemand();

        // Start the external authentication process and get an ID for this authentication job
        demand.jobKey = ExternalAuthentication.startExternalAuthentication(request);

        // Extract various details from the IdP that the authenticator will need (these are the older style)
        demand.relyingParty = request.getAttribute(ExternalAuthentication.RELYING_PARTY_PARAM).toString();
        demand.authnMethod = request.getAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM).toString();
        demand.isPassive = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM).toString());
        demand.forceAuthn = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM).toString());

        return demand;

    }

    public boolean isValid(Demand demand) {

        return true;
    }

    public void authenticate(Demand demand, HttpServletRequest request, HttpServletResponse response) throws ExternalAuthenticationException, IOException {

        HttpSession session = request.getSession();

        // Pass data from the Demand back into the request
        request.setAttribute(ExternalAuthentication.PRINCIPAL_NAME_KEY, demand.principal);

        // Pass control back to the Shibboleth IdP
        ExternalAuthentication.finishExternalAuthentication(demand.jobKey, request, response);

    }


}
