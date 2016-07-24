#!/bin/sh

set -ex

mvn package

mkdir -p sandbox/idp/shibboleth-idp/edit-webapp/WEB-INF/lib
cp target/shabti-shim.jar sandbox/idp/shibboleth-idp/edit-webapp/WEB-INF/lib/

docker-compose up -d --build

#python test.py

#docker-compose -f logs
