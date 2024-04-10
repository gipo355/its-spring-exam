package com.example.itsspringexam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

class PostBody {
    private UUID articleId;
    private int quantity;

    public UUID getArticleId() {
        return articleId;
    }

    public int getQuantity() {
        return quantity;
    }
}

class ProcessOrderBody {
    private UUID orderId;

    public UUID getOrderId() {
        return orderId;
    }
}

@RestController
@RequestMapping("/api/orders")
public class Orders {

    @GetMapping
    public String getAllOrders() {
        // Your logic to retrieve all products goes here
        return "This is the list of all orders";
    }

    // item getitem(@PathVariable("itemid") String itemid) {
    // @RequestMapping(value = "/api/orders/{itemid}", method = RequestMethod.GET)
    // @ResponseBody
    // @PostMapping("/{articleId}")
    // @RequestMapping(value = "/{articleId}", method = RequestMethod.POST)
    // public @ResponseBody
    // public String insertProduct(@PathVariable String articleId,
    // @RequestBody Map<String, Object> requestBody) {
    @PostMapping()
    public String insertProduct(@RequestBody PostBody requestBody) {

        // System.out.println("Inserting new order...");
        // System.out.println("Request body: " + requestBody);
        // System.out.println("Article ID: " + articleId);

        UUID orderId = null;

        int quantity = requestBody.getQuantity();
        UUID articleId = requestBody.getArticleId();

        System.out.println("Article ID: " + articleId);

        // UUID articleUUID = UUID.fromString(articleId);

        // OrderDetails newOrder = new OrderDetails(articleUUID, quantity);
        OrderDetails newOrder = new OrderDetails(articleId, quantity);

        orderId = DatabaseConnection.insertNewOrder(newOrder);

        System.out.println("New order inserted successfully.");

        System.out.println("OrderID: " + orderId);
        // Your logic to insert a new product with the given id and quantity goes here
        return "Product with id " + articleId + " and quantity " + quantity + " has been inserted";

    }

    @PostMapping("/process")
    public String processOrder(@RequestBody ProcessOrderBody requestBody) {
        try {

            UUID orderId = requestBody.getOrderId();

            DatabaseConnection.processOrder(orderId);

            return "Order with id " + orderId + " has been processed";
        } catch (Exception e) {
            // TODO: handle exception
            return "Error processing order";
        }
    }

}
