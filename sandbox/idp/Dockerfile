FROM digitalidentity/ishigaki:latest

MAINTAINER test@example.com

ADD ./shibboleth-idp/ /opt/shibboleth-idp/

USER root
RUN keytool -keystore /opt/shibboleth-idp/credentials/idp-browser.p12 \
    -alias jetty -genkey -storetype PKCS12 -keyalg RSA \
    -storepass abc123 -keypass abc123 \
    -dname "cn=localhost, ou=IT, o=Continuent, c=US" -noprompt

RUN /opt/shibboleth-idp/bin/build.sh


USER jetty