#!/bin/bash

A1SERVER=$1

echo -n Enter username:
read USERNAME

echo -n Enter password:
read -s PASSWORD

FILENAME=`echo ${A1SERVER} | tr '/' ' ' | cut -f 3 -d ' ' | sed 's/:/_/g'`
FILENAME=conf/jwt.${FILENAME}.json
#set -x
curl -v -o ${FILENAME} \
  "${A1SERVER}/api/login" \
  -H 'Connection: keep-alive' \
  -H 'Pragma: no-cache' \
  -H 'Cache-Control: no-cache' \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-binary "{\"username\": \"${USERNAME}\",  \"password\": \"${PASSWORD}\"}"

#cat ${FILENAME}
echo Wrote token in ${FILENAME}
