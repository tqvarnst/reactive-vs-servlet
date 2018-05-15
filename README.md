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
    
    **RHEL 7 or CentOS 7:**
    
    ```shell
    sudo yum install -y ansible
    ```

1. Install [Siege](https://www.joedog.org/siege-home/), Siege is an http load testing and benchmarking utility that supports HTTP 1.1 and HTTP 2.0, which is required by Istio. 

    **Mac OSX:**
  
    ```shell
    brew install siege
    ```
    
    **RHEL 7 or CentOS 7:**
        
    ```shell
    sudo yum install -y epel-release
    sudo yum install -y siege
    ```

## Installation

**For Minishift use:** 

1. Run the ansible playbook:

    ```shell
    cd ansible
    ansible-galaxy install -r requirements.yml -e openshift_cli=$(which oc)
    ansible-playbook init.yml
    ```

2. Done!

**For standard OpenShift installations:** 

1. Login via the `oc` command line tool as a user with `cluster-admin` access, for example:

    ```shell
    oc login -u system:admin
    ```

2. Run the ansible playbook:

    ```shell
    cd ansible
    ansible-galaxy install -r requirements.yml
    ansible-playbook init.yml -e minishift=false -e openshift_cli=$(which oc)
    ```   
3. Done!

## To run the demo

I strongly recommend that you find your own way to present this demo, but below is an example story board for running the demo.

1. Open a browser to the OpenShift console and login as user **developer**.
1. Go into the **Coolstore** project.
1. Explain the different services in the **Coolstore** project like this: 

    - **Catalog** - A service that returns a list of products that are currently offered on the web site.
    - **Inventory** - A service that returns the inventory status for a product.
    - **Gateway-XXX** - There are two different version of this service that is acting as a microservices gateway and aggregates content from the catalog service and then calls the inventory service to collect the inventory status for each product. One is implemented using **Spring-Boot** and the other one using **Eclipse Vert.x**.
    - **Web-XXX** - There are two different version of the web service. One that will connect to the **gateway-spring** service and one that connects to the **gateway-vertx** service.  

1. Click on both the **web-spring** and **web-vertx** exposed routes and show that both renders a web page with products, including inventory information.
    
    - Reload both pages a couple of times to show that respond similarly. 
    
1. Open the **Jaeger UI** in a browser

   **For Minishift use:**
   
   `minishift openshift service jaeger-query -n istio-system --in-browser`
   
   **For standard OpenShift installations:**
   
   Run `oc get route jaeger-query -n istio-system -o jsonpath='{.status.ingress[0].host}{"\n"}'` to get the URL
   
1. Select service **gateway-spring** and click find trace.

1. Select a trace that contains **gateway-spring**, **catalog** and **inventory**.

1. Review the call graph and see how request to **inventory** are called in sequence.

1. Repeat for service **gateway-vertx** and see how the request are called in parallel.

1. Open a terminal and run the following command to start putting load on the **Spring** gateway.

    ```shell
    siege -r 40 -c 20 $(oc get route gateway-spring -n coolstore -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/products
    ```
    
    This should produce a mixture of **200** and **504** and print a summary that looks like this:
    
    ```shell
    Transactions:		         756 hits
    Availability:		       94.50 %
    Elapsed time:		       17.98 secs
    Data transferred:	        1.33 MB
    Response time:		        0.12 secs
    Transaction rate:	       42.05 trans/sec
    Throughput:		        0.07 MB/sec
    Concurrency:		        5.12
    Successful transactions:         756
    Failed transactions:	          44
    Longest transaction:	        5.04
    Shortest transaction:	        0.02
    ```
    
    **NOTE:** You might see much higher or lower availability depending on how fast your system is. Try increasing or decressing the number of users (E.g. `-c`) im the command above if you are not seeing similar results.
    
1. Point out that even though **Spring-Boot** responds fast, but there are also a high number of failed calls.     
         
1. Now, repeat the same for **Eclipse Vert.x** using the following command:

    ```shell
        siege -r 40 -c 20 $(oc get route gateway-vertx -n coolstore -o jsonpath='{.status.ingress[0].host}{"\n"}')/api/products
    ```
    
    This should produce a result with only **200** and print a summary that looks like this:
    
    ```shell
    Transactions:		         800 hits
    Availability:		      100.00 %
    Elapsed time:		       20.09 secs
    Data transferred:	        1.50 MB
    Response time:		        0.20 secs
    Transaction rate:	       39.82 trans/sec
    Throughput:		        0.07 MB/sec
    Concurrency:		        8.13
    Successful transactions:         800
    Failed transactions:	           0
    Longest transaction:	        0.46
    Shortest transaction:	        0.02
    ```
    
1. Point out that Vert.x has produced a result that has a 100% availability.
1. To summarize, explain that the event queue in Eclipse Vert.x automatically applies the [Back-Pressure](https://www.reactivemanifesto.org/glossary#Back-Pressure) pattern causing Vert.x to slow down when the system is under pressure rather then return `503` status code.     