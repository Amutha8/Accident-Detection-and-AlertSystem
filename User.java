package com.application.accidentdetection;

public class User {
    private String name;
private String userId;

    private String phone;
    private String address;
    private String emergencyNumber;
    private String email;
    private String value;

    // Required default constructor for Firebase
    public User() {}

    public User(String name, String phone, String address, String emergencyNumber, String email,String value) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.emergencyNumber = emergencyNumber;
        this.email = email;
        this.value=value;

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

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String email) {
        this.value = value;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId=userId;
    }

}
