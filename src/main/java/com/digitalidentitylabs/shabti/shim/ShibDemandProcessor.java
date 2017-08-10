package com.digitalidentitylabs.shabti.shim;

import net.shibboleth.idp.authn.ExternalAuthentication;

public class ShibDemandProcessor {


    public void provision(String token, String record) {

        final Demand demand = new Demand();
        demand.externalAuthKey = ExternalAuthentication.startExternalAuthentication(request);
        demand.relyingParty = request.getAttribute(ExternalAuthentication.RELYING_PARTY_PARAM).toString();
        demand.authnMethod = request.getAttribute(ExternalAuthentication.AUTHN_METHOD_PARAM).toString();
        demand.isPassive = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.PASSIVE_AUTHN_PARAM).toString());
        demand.forceAuthn = Boolean.parseBoolean(request.getAttribute(ExternalAuthentication.FORCE_AUTHN_PARAM).toString());

    }

    public void authenticate(String token, String record) {

    }



}
