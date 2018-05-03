package com.redhat.cloudnative.coolstore.gw;

import com.redhat.cloudnative.coolstore.gw.model.Product;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name="product")
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/products", consumes = {MediaType.APPLICATION_JSON_VALUE})
    List<Product> getProducts();

}
