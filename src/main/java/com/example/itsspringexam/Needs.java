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

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

class CalcNeedsBody {
    private UUID orderId;

    public UUID getOrderId() {
        return orderId;
    }
}

@RestController
@RequestMapping("/api/needs")
public class Needs {

    @GetMapping()
    public ArrayList<OrderNeeds> getOrderNeeds(@RequestBody CalcNeedsBody requestBody) {

        return DatabaseConnection.displayOrderNeeds(requestBody.getOrderId());

    }

    @PatchMapping()
    public String updateOrder(@RequestBody CalcNeedsBody requestBody) {
        // Your logic to update the order with the given orderId and requestBody goes
        // here

        DatabaseConnection.calculateOrderNeeds(requestBody.getOrderId());

        return "Order with id " + requestBody.getOrderId() + " has been updated";
    }

}
