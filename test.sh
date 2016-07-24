#!/bin/sh

set -ex

docker-compose up -d --build

cucumber

docker-compose logs
