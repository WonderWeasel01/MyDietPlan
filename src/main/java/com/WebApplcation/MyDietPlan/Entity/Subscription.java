package com.WebApplcation.MyDietPlan.Entity;

import java.sql.Date;

public class Subscription {

private int subscriptionID;
private Date subscriptionStartDate;
private Date subscriptionEndDate;
private Boolean activeSubscription;
private double subscriptionPrice;

private int userID;


public Subscription() {
}

public Subscription(int subscriptionID, Date subscriptionStartDate, Date subscriptionEndDate, Boolean activeSubscription, double subscriptionPrice, int userID) {
    this.subscriptionID = subscriptionID;
    this.subscriptionStartDate = subscriptionStartDate;
    this.subscriptionEndDate = subscriptionEndDate;
    this.activeSubscription = activeSubscription;
    this.subscriptionPrice = subscriptionPrice;
    this.userID = userID;
}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getSubscriptionID() {
    return subscriptionID;
}
public void setSubscriptionID(int subscriptionID) {
    this.subscriptionID = subscriptionID;
}
public Date getSubscriptionStartDate() {
    return subscriptionStartDate;
}
public void setSubscriptionStartDate(Date subscriptionStartDate) {
    this.subscriptionStartDate = subscriptionStartDate;
}
public Date getSubscriptionEndDate() {
    return subscriptionEndDate;
}
public void setSubscriptionEndDate(Date subscriptionEndDate) {
    this.subscriptionEndDate = subscriptionEndDate;
}
public Boolean getActiveSubscription() {
    return activeSubscription;
}
public void setActiveSubscription(Boolean activeSubscription) {
    this.activeSubscription = activeSubscription;
}
public double getSubscriptionPrice() {
    return subscriptionPrice;
}
public void setSubscriptionPrice(double subscriptionPrice) {
    this.subscriptionPrice = subscriptionPrice;
}
@Override
public String toString() {
    return "Subscription [subscriptionID=" + subscriptionID + ", subscriptionType=" +
            ", subscriptionStartDate=" + subscriptionStartDate + ", subscriptionEndDate=" + subscriptionEndDate
            + ", subscriptionStatus=" + activeSubscription + ", subscriptionPrice=" + subscriptionPrice + "]";
}





}
