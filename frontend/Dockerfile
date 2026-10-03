FROM node:22 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ARG API_URL=http://localhost:8080/api
RUN sed -i "s|__API_URL__|${API_URL}|g" src/environments/environment.prod.ts
RUN npm run build -- --configuration production

FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
