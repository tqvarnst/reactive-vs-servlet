all: build push

build:
	s2i build . registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.3 gateway-vertx:latest

tag:
	docker tag gateway-vertx:latest coolstore/gateway-vertx:latest

push: tag
	docker push coolstore/gateway-vertx:latest


