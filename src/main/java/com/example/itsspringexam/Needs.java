package com.example.itsspringexam;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.System;
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

        ArrayList<OrderNeeds> arrayList = new ArrayList<OrderNeeds>();
        try {
            arrayList = DatabaseConnection.displayOrderNeeds(requestBody.getOrderId());
            return arrayList;
        } catch (Exception e) {
            return arrayList;
        }
    }

    @GetMapping("/cost")
    public double getOrderCost(@RequestBody CalcNeedsBody requestBody) {
        ArrayList<OrderNeeds> arrayList = new ArrayList<OrderNeeds>();
        UUID orderId = requestBody.getOrderId();
        try {
            // display needs for an order
            //
            // get the article cost from thearticle did, using hte id from the needs
            //
            // art cost * artq from the needs per il singolo, * q dell' ordine per costo
            // ordine
            //
            // update order db col totcost

            // Having ArrayList<OrderNeeds> calc the total cost of the order
            arrayList = DatabaseConnection.displayOrderNeeds(orderId);
            ArrayList<Article> articlelist = DatabaseConnection.displayArticles();

            double totalUnitaryOrderCost = 0;
            for (OrderNeeds orderNeeds : arrayList) {
                UUID articleId = orderNeeds.getArticleId();

                double articleQ = orderNeeds.getQuantityNeeded();

                double articleTCost = 0.0;

                for (Article article : articlelist) {
                    if (article.getId().equals(articleId)) {
                        // totalCost += article. * orderNeeds.getQuantityNeeded();
                        double articleUnitaryCost = article.getCost();
                        articleTCost += articleUnitaryCost * articleQ;
                    }
                }

                totalUnitaryOrderCost += articleTCost;
            }

            Order order = DatabaseConnection.getOrder(orderId);

            int orderQuantity = order.getQuantityToProduce();

            double totalOrderCost = totalUnitaryOrderCost * orderQuantity;

            // get the order, multiply the totalOrderUnCost * orderq

            DatabaseConnection.updateOrderTotalCost(orderId, totalOrderCost);

            return totalOrderCost;

        } catch (Exception e) {

            throw e;
        }
    }

    @PatchMapping()
    public String updateOrder(@RequestBody CalcNeedsBody requestBody) {
        // Your logic to update the order with the given orderId and requestBody goes
        // here

        try {
            DatabaseConnection.calculateOrderNeeds(requestBody.getOrderId());

            return "Order with id " + requestBody.getOrderId() + " has been updated";
        } catch (Exception e) {
            return "Order with id " + requestBody.getOrderId() + " does not exist";
        }

    }

}
