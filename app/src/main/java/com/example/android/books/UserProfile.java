package com.example.android.books;

public class UserProfile {
    public String userPhoneNumber;
    public String userEmail;
    public String userName;
    public String userAddress;

    public UserProfile(){

    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public UserProfile(String userPhoneNumber, String userEmail, String userName, String userAddress) {

        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userAddress = userAddress;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
