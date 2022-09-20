package com.example.afyacorner.Models;

public class Cart {
    private String prodId,productType,price,description
            ,quantity,discount,weight,date,time,productSellerID
            ,productSellerName;


    public Cart(String prodId, String productType, String price, String description,
                String quantity, String discount, String weight,
                String date, String time, String productSellerID, String productSellerName) {
        this.prodId = prodId;
        this.productType = productType;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.discount = discount;
        this.weight = weight;
        this.date = date;
        this.time = time;
        this.productSellerID = productSellerID;
        this.productSellerName = productSellerName;
    }

    public Cart() {
    }

    public String getProductSellerID() {
        return productSellerID;
    }

    public void setProductSellerID(String productSellerID) {
        this.productSellerID = productSellerID;
    }

    public String getProductSellerName() {
        return productSellerName;
    }

    public void setProductSellerName(String productSellerName) {
        this.productSellerName = productSellerName;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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
}
