# Demo: Reactive-vs-servlet

This is a demo to compare reactive with servlet typ of application under load.

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

2. Install `ansible` and `ansible-galaxy` command line tools 
  
    **Mac OSX:**
  
    ```shell
    brew install ansible
    ```

    **RHEL 7 or CentOS 7:**

    ```shell
    sudo yum install -y ansible
    ```

3. Install [Siege](https://www.joedog.org/siege-home/), Siege is an http load testing and benchmarking utility that supports HTTP 1.1 and HTTP 2.0, which is required by Istio. 

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

1. Clone this repo `git clone https://github.com/tqvarnst/reactive-vs-servlet.git && reactive-vs-servlet`

**For Minishift use:**

1. Run the ansible playbook:

    ```shell
    cd ansible
    ansible-galaxy install -r requirements.yml 
    ansible-playbook init.yml -e openshift_cli=$(which oc)
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
    - **Web** - The web project is a NodeJS application that serve as a web server. 
    - **Istio Ingress** - The ingress router is what exposes the application to the outside (from OpenShift)
    - **Istio Route rule** - The istio route rules will route traffic from the Gateway service to either the spring-boot pod or the vertx-pod. Default this will be using the Spring Boot route
    - **Istio Delay route rule** - This is a fault injection route where a delay is introduced between the gateway and the inventory service. This rule is used to test how the application behaves when inventory is starting to respond slower.  

1. Open a browser to the application using the following command

    **For Minishift:**

    `minishift openshift service istio-ingressgateway -n istio-system --in-browser`

    **For standard OpenShift installations:** 

    Open the a browser to the result of the following command: `oc get route istio-ingressgateway -n istio-system -o jsonpath="{.spec.host}"`

    Reload the pages a couple of times to show that respond similarly. 

2. Show the Istio virtual service pointing to the gateway type-spring

    `oc get virtualservice gateway -o yaml`

3. Edit the virtual service and replace type-spring with type-vertx

    `oc edit virtualservice gateway`

4. Reload the browser tab pointing to the application and see how the **We are Reactive** logo in the right upper corner. Reload a couple of more times. 
     
5. Open the **Jaeger UI** in a browser

   **For Minishift use:**

   `minishift openshift service tracing -n istio-system --in-browser`

   **For standard OpenShift installations:**

   Run `oc get route tracing -n istio-system -o jsonpath='{.status.ingress[0].host}{"\n"}'` to get the URL

6. Select service **gateway** and find a trace that is lists 

7. Select a trace that contains **gateway-spring**, **catalog** and **inventory**

8. Review the call graph and see how request to **inventory** are called in sequence.

9. Repeat for trace that **doesn't** contain **gateway-spring**, but **does** contain **gateway**, **catalog** and **inventory**  and see how the request are called in parallel.

10. Switch back to use the **Spring-boot** version of the gateway. 

    `oc edit virtualservice gateway`
    
11. Introduce a delay to the inventory service

    `istioctl create -f files/istio/inventory-fault-delay.yml`
    
12. Open a terminal and run the following command to start putting load on the **Spring** gateway.

    `siege -r 10 -c 10 $(minishift openshift service istio-ingressgateway -n istio-system --url)/api/products`
    
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
    
13. Point out that even though **Spring-Boot** responds fast, but there are also a high number of failed calls.

14. Switch to the Vert.x version

    `oc edit virtualservice gateway`
         
15. Now, repeat the same test for **Eclipse Vert.x** using the same command:

    `siege -r 10 -c 10 $(minishift openshift service istio-ingressgateway -n istio-system --url)/api/products`

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
    
16. Point out that Vert.x has produced a result that has a 100% availability.
17. To summarize, explain that the event queue in Eclipse Vert.x automatically applies the [Back-Pressure](https://www.reactivemanifesto.org/glossary#Back-Pressure) pattern causing Vert.x to slow down when the system is under pressure rather then return `503` status code.
