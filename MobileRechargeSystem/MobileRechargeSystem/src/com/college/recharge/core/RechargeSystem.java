package com.college.recharge.core;

import com.college.recharge.models.User;
import com.college.recharge.models.RechargePlan;
import com.college.recharge.models.RechargeRecord;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RechargeSystem implements Serializable {
    private List<User> userList;
    private List<RechargePlan> plans;
    private String currentMobileNumber;

    public RechargeSystem() {
        userList = new ArrayList<>();
        plans = new ArrayList<>();

        // Add more plans here
        plans.add(new RechargePlan(1, "1GB/day, 28 days", 199.0));
        plans.add(new RechargePlan(2, "2GB/day, 56 days", 399.0));
        plans.add(new RechargePlan(3, "1.5GB/day, 84 days", 666.0)); // NEW PLAN
        plans.add(new RechargePlan(4, "2GB/day, 30 days", 299.0)); // NEW PLAN
        plans.add(new RechargePlan(5, "Talktime only, unlimited validity", 100.0)); // NEW PLAN
    }

    public User findUser(String mobileNumber) {
        for (User user : userList) {
            if (user.getMobileNumber().equals(mobileNumber)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        userList.add(user);
    }
    
    public List<User> getUserList() {
        return userList;
    }

    public void setCurrentUser(String mobileNumber) {
        this.currentMobileNumber = mobileNumber;
    }

    public User getCurrentUser() {
        return findUser(currentMobileNumber);
    }

    public List<RechargePlan> getAvailablePlans() {
        return plans;
    }

    public boolean recharge(int planId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        RechargePlan selectedPlan = null;
        for (RechargePlan plan : plans) {
            if (plan.getPlanId() == planId) {
                selectedPlan = plan;
                break;
            }
        }

        if (selectedPlan != null && currentUser.getWalletBalance() >= selectedPlan.getPrice()) {
            double newBalance = currentUser.getWalletBalance() - selectedPlan.getPrice();
            currentUser.setWalletBalance(newBalance);
            currentUser.addHistory(new RechargeRecord(selectedPlan.getPrice(), selectedPlan.getDescription()));
            return true;
        }
        return false;
    }
}