all: build push

build:
	s2i build . registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.3 gateway-spring:latest

tag:
	docker tag gateway-spring:latest coolstore/gateway-spring:latest

push: tag
	docker push coolstore/gateway-spring:latest


