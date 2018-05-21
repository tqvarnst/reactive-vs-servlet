# Go parameters
GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get
BINARY_NAME=catalog-app
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
docker-build: build
	docker build -t catalog .

docker-run: docker-build
	docker run -it -p 8000:8080 catalog:latest

docker-push: docker-build
	docker tag catalog:latest coolstore/catalog:latest
	docker push coolstore/catalog:latest



