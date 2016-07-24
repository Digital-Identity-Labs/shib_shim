FROM unicon/shibboleth-idp

MAINTAINER test@example.com

ADD ./sandbox/shibboleth-idp/ /opt/shibboleth-idp/
RUN chmod u+x /opt/jre-home/bin/keytool
RUN /opt/jre-home/bin/keytool -keystore /opt/shibboleth-idp/credentials/idp-browser.p12 \
    -alias jetty -genkey -storetype PKCS12 -keyalg RSA \
    -storepass abc123 -keypass abc123 \
    -dname "cn=localhost, ou=IT, o=Continuent, c=US" -noprompt
