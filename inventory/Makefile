# Go parameters
GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get
BINARY_NAME=inventory-app
BINARY_LINUX=$(BINARY_NAME)-linux-amd64
BINARY_MAC=$(BINARY_NAME)-darwin-amd64

all: test build

build: deps
	$(GOBUILD) -o $(BINARY_NAME) -v

test:
	$(GOTEST) -v ./...

clean:
	$(GOCLEAN)
	rm -f $(BINARY_NAME)
	rm -f $(BINARY_UNIX)

run: build
	$(GOBUILD) -o $(BINARY_NAME) -v ./...
	./$(BINARY_NAME)

deps:
	$(GOGET) github.com/gorilla/mux


# Cross compilation
build-linux:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 $(GOBUILD) -o $(BINARY_LINUX) -v

# Docker
docker-build:
	docker build -t inventory .

docker-run:
	docker run -it -p 8080:8080 inventory:latest

docker-push:
	docker tag inventory:latest coolstore/inventory:latest
	docker push coolstore/inventory:latest


