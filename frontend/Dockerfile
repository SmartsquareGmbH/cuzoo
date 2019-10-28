#
# Builder Container
#
FROM node:11-alpine as builder
ARG mode=production
WORKDIR /app

# copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

COPY yarn.lock ./

RUN yarn

COPY . .

RUN yarn build --mode ${mode}

#
# Run Container
#
FROM nginx:1.13.12-alpine as runtime
# install simple http server for serving static content
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
