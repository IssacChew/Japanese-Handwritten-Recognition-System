package com.example.japaneserecognitionsystem.model;

public class User {

    private String userName, emailAddress, imageURL;

    public User() {
    }

    public User(String userName, String emailAddress) {
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.imageURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScSc2b1SgH7LH8wFw_vrAX85vVftQ0c8Pc3SxrU71e0Fa2SwXikvhS_LekmWu-pj26CVE&usqp=CAU";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
