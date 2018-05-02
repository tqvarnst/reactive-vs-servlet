package com.redhat.cloudnative.coolstore.gw;

import com.redhat.cloudnative.coolstore.gw.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/services")
@CrossOrigin
public class GatewayService {

    @Autowired
    InventoryClient inventoryClient;

    @Autowired
    ProductClient productClient;

    @ResponseBody
    @GetMapping("/products")
    public List<Product> readAll() {
        List<Product> productList = productClient.getProducts();
        productList.stream()
                .forEach(p -> {
                    p.setAvailability(inventoryClient.getInventory());
                });
        return productList;
    }

}
