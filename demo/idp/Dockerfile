FROM ghcr.io/digital-identity-labs/ishigaki:2.1.0-base

LABEL description="Indiid Primary SAML IdP" \
      maintainer="pete@digitalidentitylabs.com"

ENV IDP_HOSTNAME=idp.indiid.net \
    IDP_ID=https://indiid.net/idp/shibboleth \
    IDP_SCOPE=indiid.net \
    WRITE_MD=0

ARG MODULES="idp.admin.Hello,idp.authn.External"

COPY optfs /opt

RUN for plugin in $PLUGINS; do $IDP_HOME/bin/plugin.sh -i $plugin ; done && \
    $IDP_HOME/bin/module.sh -i $MODULES ; $IDP_HOME/bin/module.sh -e $MODULES && \
    rm -rf $IDP_HOME/war/*

RUN cd $IDP_HOME && ./bin/build.sh -Didp.target.dir=$IDP_HOME

