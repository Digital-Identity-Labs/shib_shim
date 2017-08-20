package com.digitalidentitylabs.shabti.shim;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.shibboleth.idp.authn.ExternalAuthentication;
import net.shibboleth.idp.authn.ExternalAuthenticationException;

import java.util.StringTokenizer;
import java.io.IOException;
import java.lang.System;

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

        // Information about the user/agent
        demand.userAddress = getClientIpAddress(request);

        // Information about this service
        demand.siteDomain  = request.getServerName();
        
        return demand;

    }

    public boolean isSatisfied(Demand demand) {
        if (!demand.isValid()) { return false; }
        return true;
    }

    public void authnSuccess(Demand demand, HttpServletRequest request) throws ExternalAuthenticationException, IOException {

        if (demand.state() != Demand.DemandState.ACCEPTED ) {
            throw new IllegalArgumentException("Demand has not been accepted and cannot be used for authentication!");
        }

        HttpSession session = request.getSession();

        // Pass data from the Demand back into the request
        request.setAttribute(ExternalAuthentication.PRINCIPAL_NAME_KEY, demand.principal);
        request.setAttribute(ExternalAuthentication.AUTHENTICATION_INSTANT_KEY, demand.decidedAt);
        request.setAttribute(ExternalAuthentication.DONOTCACHE_KEY, demand.doNotCache);
        request.setAttribute(ExternalAuthentication.PREVIOUSRESULT_KEY, false);

    }

    public void authnError(Demand demand, HttpServletRequest request) throws ExternalAuthenticationException, IOException {

        if (demand.state() != Demand.DemandState.REJECTED ) {
            throw new IllegalArgumentException("Demand has not been accepted and cannot be used for authentication!");
        }

        HttpSession session = request.getSession();

        // Pass data from the Demand back into the request
        request.setAttribute(ExternalAuthentication.AUTHENTICATION_ERROR_KEY,   demand.errorMessage);
        request.setAttribute(ExternalAuthentication.AUTHENTICATION_INSTANT_KEY, demand.decidedAt);
        request.setAttribute(ExternalAuthentication.DONOTCACHE_KEY, true);
    }


    public void finish(Demand demand, HttpServletRequest request, HttpServletResponse response) throws ExternalAuthenticationException, IOException  {
        // Pass control back to the Shibboleth IdP
        ExternalAuthentication.finishExternalAuthentication(demand.jobKey, request, response);
    }

    private static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
        }
    }


}
