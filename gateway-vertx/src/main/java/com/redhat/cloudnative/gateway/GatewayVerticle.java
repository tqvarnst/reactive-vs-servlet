package com.redhat.cloudnative.gateway;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import io.vertx.rxjava.servicediscovery.types.HttpEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import static com.redhat.cloudnative.gateway.TracingInterceptor.propagate;

import java.util.*;
import java.util.stream.Collectors;

public class GatewayVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(GatewayVerticle.class);

    static final List<String> TRACING_HEADERS = new ArrayList<String>() {{
        add("x-request-id");
        add("x-b3-traceid");
        add("x-b3-spanid");
        add("x-b3-parentspanid");
        add("x-b3-sampled");
        add("x-b3-flags");
        add("x-ot-span-context");
    }};

    private Map<String, String> tracingHeaders;


    private WebClient catalog;
    private WebClient inventory;


    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET));
        router.get("/health").handler(ctx -> ctx.response().end(new JsonObject().put("status", "UP").toString()));
        router.get("/api/runtime").handler(ctx -> ctx.response().end(new JsonObject().put("name", "vertx").toString()));
        router.route().handler(TracingInterceptor.create());
        router.get("/api/products").handler(this::products);

        ServiceDiscovery.create(vertx, discovery -> {
            // Catalog lookup
            Single<WebClient> catalogDiscoveryRequest = HttpEndpoint.rxGetWebClient(discovery,
                    rec -> rec.getName().equals("catalog"))
                    .onErrorReturn(t -> WebClient.create(vertx, new WebClientOptions()
                            .setDefaultHost(System.getProperty("catalog.api.host", "catalog"))
                            .setDefaultPort(Integer.getInteger("catalog.api.port", 8080))
                            .setHttp2MaxPoolSize(100)
                            .setMaxPoolSize(100)));

            // Inventory lookup
            Single<WebClient> inventoryDiscoveryRequest = HttpEndpoint.rxGetWebClient(discovery,
                    rec -> rec.getName().equals("inventory"))
                    .onErrorReturn(t -> WebClient.create(vertx, new WebClientOptions()
                            .setDefaultHost(System.getProperty("inventory.api.host", "inventory"))
                            .setDefaultPort(Integer.getInteger("inventory.api.port", 8080))
                            .setHttp2MaxPoolSize(100)
                            .setMaxPoolSize(100)));

            // Zip all 3 requests
            Single.zip(catalogDiscoveryRequest, inventoryDiscoveryRequest, (c, i) -> {
                // When everything is done
                catalog = c;
                inventory = i;
                return vertx.createHttpServer()
                        .requestHandler(router::accept)
                        .listen(Integer.getInteger("http.port", 8080));
            }).subscribe();
        });
    }

    private void products(RoutingContext rc) {
        // Retrieve catalog
        propagate(catalog,rc).get("/api/products").as(BodyCodec.jsonArray()).rxSend()
                .map(resp -> {
                    if (resp.statusCode() != 200) {
                        new RuntimeException("Invalid response from the catalog: " + resp.statusCode());
                    }
                    return resp.body();
                })
                .flatMap(products ->
                        // For each item from the catalog, invoke the inventory service
                        Observable.from(products)
                                .cast(JsonObject.class)
                                .flatMapSingle(product -> propagate(inventory,rc).get("/api/inventory/" + product.getString("itemId")).as(BodyCodec.jsonObject())
                                            .rxSend()
                                            .map(resp -> {
                                                if (resp.statusCode() != 200) {
                                                    LOG.warn("Inventory error for {}: status code {}",
                                                            product.getString("itemId"), resp.statusCode());
                                                    return product.copy();
                                                }
                                                return product.copy().put("availability",
                                                        new JsonObject().put("quantity", resp.body().getInteger("quantity")));
                                            })
                                )
                                .toList().toSingle()
                )
                .subscribe(
                        list -> {
                            rc.response().end(Json.encode(list));
                        },
                        error -> rc.response().end(new JsonObject().put("error", error.getMessage()).toString())
                );
    }
}