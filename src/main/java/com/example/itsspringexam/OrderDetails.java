package com.example.itsspringexam;

import java.util.UUID;

public class OrderDetails {
    private final UUID articleId;
    private final int quantityToProduce;
    private final boolean produced;
    private int totalCost;
    private UUID id;

    public OrderDetails(UUID articleId, int quantityToProduce) {
        this.articleId = articleId;
        this.quantityToProduce = quantityToProduce;
        this.produced = false;
    }

    // getters and setters

    public UUID getArticleId() {
        return articleId;
    }

    public int getQuantityToProduce() {
        return quantityToProduce;
    }

    public boolean isProduced() {
        return produced;
    }

    public int getTotalCost() {
        return totalCost;
    }
}
