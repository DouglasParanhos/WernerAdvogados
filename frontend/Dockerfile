FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG VITE_API_URL=/api
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

FROM nginx:alpine
# Instalar envsubst para substituir variáveis no nginx.conf
RUN apk add --no-cache envsubst

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/templates/default.conf.template

# Criar entrypoint script para substituir PORT no nginx.conf
RUN echo '#!/bin/sh' > /entrypoint.sh && \
    echo 'export PORT=${PORT:-80}' >> /entrypoint.sh && \
    echo 'envsubst "$$PORT" < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf' >> /entrypoint.sh && \
    echo 'exec nginx -g "daemon off;"' >> /entrypoint.sh && \
    chmod +x /entrypoint.sh

EXPOSE 80
CMD ["/entrypoint.sh"]

