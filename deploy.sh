oc new-project coolstore-vertx
oc policy add-role-to-user view -n $(oc project -q) -z default
oc process -f catalog-template.yaml | oc create -f -
mvn -f gateway-vertx clean package fabric8:deploy
oc new-app nodejs~web-nodejs \
        --name=web
oc start-build web --from-dir=web-nodejs
oc expose svc/web
oc new-project coolstore-spring
oc policy add-role-to-user view -n $(oc project -q) -z default
oc process -f catalog-template.yaml | oc create -f -
mvn -f gateway-spring clean package fabric8:deploy
oc new-app nodejs~web-nodejs \
        --name=web
oc start-build web --from-dir=web-nodejs
oc expose svc/web