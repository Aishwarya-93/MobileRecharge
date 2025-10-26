package com.college.recharge.models;

import java.io.Serializable;
import java.util.Date;

// This class holds the data for each recharge transaction
public class RechargeRecord implements Serializable {
    private double amount;
    private Date date;
    private String planDescription;

    public RechargeRecord(double amount, String planDescription) {
        this.amount = amount;
        this.planDescription = planDescription;
        this.date = new Date(); // Sets the current date
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
    
    public String getPlanDescription() {
        return planDescription;
    }
}