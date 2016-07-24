#!/bin/bash

set -ex

CONTAINER_NAME=shib-sandbox-idp
IMAGE_NAME=shib-sandbox-idp

docker build --tag="${IMAGE_NAME}" .

docker stop "$CONTAINER_NAME" || true
docker rm "$CONTAINER_NAME" || true
docker run -p 4443:4443 -p 8443:8443 \
    -e JETTY_BROWSER_SSL_KEYSTORE_PASSWORD=abc123 \
    -e JETTY_BACKCHANNEL_SSL_KEYSTORE_PASSWORD=abc \
    -it --name="$CONTAINER_NAME" "${IMAGE_NAME}"



docker build --tag="<org_id>/<image_name>" .
