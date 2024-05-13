package com.WebApplcation.MyDietPlan.Model;

import java.sql.Date;

public class Subscription {

private int subscriptionID;
private String subscriptionType;
private Date subscriptionStartDate;
private Date subscriptionEndDate;
private Boolean subscriptionStatus;
private double subscriptionPrice;


public Subscription() {
}

public Subscription(int subscriptionID, String subscriptionType, Date subscriptionStartDate, Date subscriptionEndDate, Boolean subscriptionStatus, double subscriptionPrice) {
    this.subscriptionID = subscriptionID;
    this.subscriptionType = subscriptionType;
    this.subscriptionStartDate = subscriptionStartDate;
    this.subscriptionEndDate = subscriptionEndDate;
    this.subscriptionStatus = subscriptionStatus;
    this.subscriptionPrice = subscriptionPrice;
}





public int getSubscriptionID() {
    return subscriptionID;
}
public void setSubscriptionID(int subscriptionID) {
    this.subscriptionID = subscriptionID;
}
public String getSubscriptionType() {
    return subscriptionType;
}
public void setSubscriptionType(String subscriptionType) {
    this.subscriptionType = subscriptionType;
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
public Boolean getSubscriptionStatus() {
    return subscriptionStatus;
}
public void setSubscriptionStatus(Boolean subscriptionStatus) {
    this.subscriptionStatus = subscriptionStatus;
}
public double getSubscriptionPrice() {
    return subscriptionPrice;
}
public void setSubscriptionPrice(double subscriptionPrice) {
    this.subscriptionPrice = subscriptionPrice;
}
@Override
public String toString() {
    return "Subscription [subscriptionID=" + subscriptionID + ", subscriptionType=" + subscriptionType
            + ", subscriptionStartDate=" + subscriptionStartDate + ", subscriptionEndDate=" + subscriptionEndDate
            + ", subscriptionStatus=" + subscriptionStatus + ", subscriptionPrice=" + subscriptionPrice + "]";
}





}