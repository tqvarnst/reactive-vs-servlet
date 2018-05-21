all: build push

build:
	s2i build . centos/nodejs-8-centos7 web:latest

tag:
	docker tag web:latest coolstore/web:latest

push: tag
	docker push coolstore/web:latest


