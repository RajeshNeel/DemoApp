package com.gaurav.demoapp;

/**
 * Created by Gaurav Sharma on 29-08-2021 on 01:07 .
 */
public class Users {
    private String userEmail,userPassword,fullName,photoUri;



       public  Users(){

}

    public Users(String userEmail,String userPassword){
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
    public Users(String userEmail, String userPassword, String fullName, String photoUri) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.fullName = fullName;
        this.photoUri = photoUri;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
