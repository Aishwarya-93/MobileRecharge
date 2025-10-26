package com.college.recharge.models;

import java.io.Serializable;
import java.util.ArrayList; // NEW IMPORT
import java.util.List; // NEW IMPORT

public class User implements Serializable {
    private String mobileNumber;
    private String name;
    private double walletBalance;
    private List<RechargeRecord> history; // NEW: Each user has their own history

    public User(String mobileNumber, String name, double walletBalance) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.walletBalance = walletBalance;
        this.history = new ArrayList<>(); // Initialize the history list
    }

    public String getMobileNumber() { return mobileNumber; }
    public String getName() { return name; }
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    // NEW METHODS: Get the user's history and add a new record
    public List<RechargeRecord> getHistory() { return history; }
    public void addHistory(RechargeRecord record) { history.add(record); }
}