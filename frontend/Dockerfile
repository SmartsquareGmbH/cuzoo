#
# Builder Container
#
FROM node:11-alpine as builder

WORKDIR /app

# copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

COPY yarn.lock ./

RUN yarn

COPY . .

RUN yarn build

#
# Run Container
#
FROM node:11-alpine
# install simple http server for serving static content
RUN yarn global add http-server
WORKDIR /app/
COPY --from=builder /app/dist .
EXPOSE 8080
CMD [ "http-server" ]
