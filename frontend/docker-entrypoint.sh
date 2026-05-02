#!/bin/sh
set -e
if [ -z "$PORT" ]; then
  PORT=80
fi
if [ -z "$BACKEND_URL" ]; then
  BACKEND_URL=http://backend:8081
fi
# API batch routes (ex.: DataJud movimentos) podem levar vários minutos; Nginx padrão 60s causava 504.
# Railway edge: duração máxima de requisição HTTP ~15 min (https://docs.railway.com/reference/public-networking)
if [ -z "$PROXY_API_TIMEOUT" ]; then
  PROXY_API_TIMEOUT=600s
fi
export PORT
export BACKEND_URL
export PROXY_API_TIMEOUT
envsubst '$PORT $BACKEND_URL $PROXY_API_TIMEOUT' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf
exec nginx -g "daemon off;"
