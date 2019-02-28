#
# Builder Container
#
FROM node:11-alpine as builder
WORKDIR /app

COPY package.json ./

COPY yarn.lock ./

RUN yarn

COPY . .

RUN yarn build

#
# Run Container
#
FROM nginx:1.13.12-alpine as runtime
# install simple http server for serving static content
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
