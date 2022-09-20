package com.example.afyacorner.Models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class User {
    private String name,phone,password,email,address,typeUser,dataOperation,
             image, statusOfUser, cash;

    public User() {}

    public User(String name, String phone, String password, String address,
                String image, String typeUser, String email, String statusOfUser, String cash) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.image = image;
        this.email = email;
        this.typeUser = typeUser;
        this.statusOfUser = statusOfUser;
        this.cash = cash;

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        this.statusOfUser = "active";
        this.dataOperation = timeStamp;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataOperation() {
        return dataOperation;
    }

    public void setDataOperation(String dataOperation) {
        this.dataOperation = dataOperation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatusOfUser() {
        return statusOfUser;
    }

    public void setStatusOfUser(String statusOfUser) {
        this.statusOfUser = statusOfUser;
    }
}
