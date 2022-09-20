package com.example.afyacorner.Models;

public class AdminOrders {
    private String name, phone, address, city, state, date, time,
            totalAmount,statusAnimal, orderId, sellerFoneNumber, sellerName;

    public AdminOrders() {
    }

    public AdminOrders(String name, String phone, String address, String city, String state,
                       String date, String time, String totalAmount,
                       String statusAnimal, String orderId, String sellerFoneNumber, String sellerName) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.statusAnimal = statusAnimal;
        this.orderId = orderId;
        this.sellerFoneNumber = sellerFoneNumber;
        this.sellerName = sellerName;
    }

    public String getSellerFoneNumber() {
        return sellerFoneNumber;
    }

    public void setSellerFoneNumber(String sellerFoneNumber) {
        this.sellerFoneNumber = sellerFoneNumber;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getStatusAnimal() {
        return statusAnimal;
    }

    public void setStatusAnimal(String statusAnimal) {
        this.statusAnimal = statusAnimal;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
