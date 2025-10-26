package com.college.recharge.models;

import java.io.Serializable;

public class RechargePlan implements Serializable {
    private int planId;
    private String description;
    private double price;

    public RechargePlan(int planId, String description, double price) {
        this.planId = planId;
        this.description = description;
        this.price = price;
    }

    public int getPlanId() {
        return planId;
    }
    public String getDescription() {
        return description;
    }
    public double getPrice() {
        return price;
    }
}