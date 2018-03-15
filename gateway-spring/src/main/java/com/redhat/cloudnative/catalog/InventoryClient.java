package com.redhat.cloudnative.catalog;

import com.redhat.cloudnative.catalog.model.Inventory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="inventory")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory.json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    Inventory getInventory();

}
