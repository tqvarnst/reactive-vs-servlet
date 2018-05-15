# Demo: Reactive-vs-servlet
This is a demo to compare reactive with servlet typ of application under heavy load.

The reactive framework used in this demo is **Eclipse Vert.x** and for the servlet based application we use **Spring-Boot** with Netflix libraries like Feign and Hystrix.

**NOTE:** The version of Hystrix used in the demo is actually using Java RX, which is a reactive library, however the servlet container is still using traditional servlet technologies.

**NOTE:** More modern versions of Spring-Boot also provide a web server implemented using the Reactive system principles called Flux. However, since that is still a fairly new and rarely used feature of Spring Boot. 

The demo uses Istio and particularly Jaeger tracing to visualise the calls between services.

## What is the purpose of this demo

The purpose of this demo is to show that a reactive system behaves, much better under load then a systems that scales based on threads (e.g. Most servlet engines). This is not to bash Spring-Boot, the same issue will occure in Tomcat and most Java EE implementations as well.

When a servlet engine is put under load and is running out of resources, it will start responding with `503` status code, while using the same load or higher on a reactive system can scale all the way up to the limits of the machine (e.g. CPU, memory, etc).

This demo also shows how Istio and Jaeger tracing can be used to get an greater understanding on how you systems behaves and also to inject failure scenarios.

## Pre-requisites

The installation is used as Ansible Playbook and there for require  **Ansible** and **Ansible Galaxy**.
For load-testing we will use a tool called **Siege**.

1. You need to have a OpenShift 3.9 installation, for example [Red Hat CDK](https://developers.redhat.com/products/cdk/overview/) or Minishift. 

1. Install `ansible` and `ansible-galaxy` command line tools 
  
    **Mac OSX:**
  
    ```shell
    brew install ansible
    ```

1. Install [Siege](https://www.joedog.org/siege-home/), Siege is an http load testing and benchmarking utility that supports HTTP 1.1 and HTTP 2.0, which is required by Istio. 

    **Mac OSX:**
  
    ```shell
    brew install siege
    ```

## Installation

**For Minishift use:** 

1. Run the ansible playbook:

    ```shell
    cd ansible
    ansible-galaxy install -r requirements.yml
    ansible-playbook init.yml
    ```

2. Done!

**For Other OpenShift installations:** 

1. Login via the `oc` command line tool as a user with `cluster-admin` access, for example:

    ```shell
    oc login -u system:admin
    ```

2. Run the ansible playbook:

    ```shell
    cd ansible
    ansible-galaxy install -r requirements.yml
    ansible-playbook init.yml -e minishift=false
    ```   
