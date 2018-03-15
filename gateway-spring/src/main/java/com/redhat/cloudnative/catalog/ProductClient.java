package com.redhat.cloudnative.catalog;

import com.redhat.cloudnative.catalog.model.Product;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name="product")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/catalog.json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    List<Product> getProducts();

}
