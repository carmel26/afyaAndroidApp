package com.example.afyacorner.Models;

public class Product {
    private String category,date,description, prodId,
            district,image,price,time,type,weight,
            productStatus,sellerName,sellerEmail,
            selectedStatus, sellerId;

    public Product() {
    }

    public Product(String category, String date, String description, String district,
                   String image, String price, String time, String type, String weight,
    String sellerName, String productStatus, String sellerEmail, String selectedStatus, String sellerId) {
        this.category = category;
        this.date = date;
        this.description = description;
        this.district = district;
        this.image = image;
        this.price = price;
        this.time = time;
        this.type = type;
        this.weight = weight;
        this.sellerEmail = sellerEmail;
        this.sellerName = sellerName;
        this.productStatus = productStatus;
        this.selectedStatus = selectedStatus;
        this.sellerId = sellerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public String getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
