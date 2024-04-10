package com.example.itsspringexam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class Orders {

    @GetMapping
    public String getAllOrders() {
        // Your logic to retrieve all products goes here
        return "This is the list of all orders";
    }

    // @RequestMapping(value="/data/{itemid}", method = RequestMethod.GET)
    // public @ResponseBody
    // item getitem(@PathVariable("itemid") String itemid) {

    // @RequestMapping(value = "/api/orders/{itemid}", method = RequestMethod.GET)
    // @ResponseBody
    @PostMapping("/api/orders/{articleId}")
    public String insertProduct(@PathVariable UUID articleId, @RequestBody Map<String, Object> requestBody) {

        UUID orderId = null;

        int quantity = (int) requestBody.get("quantity");

        OrderDetails newOrder = new OrderDetails(articleId, quantity);

        orderId = DatabaseConnection.insertNewOrder(newOrder);

        System.out.println("New order inserted successfully.");

        System.out.println("OrderID: " + orderId);
        // Your logic to insert a new product with the given id and quantity goes here
        return "Product with id " + articleId + " and quantity " + quantity + " has been inserted";
    }

}
