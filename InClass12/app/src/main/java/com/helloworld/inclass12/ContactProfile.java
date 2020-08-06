package com.helloworld.inclass12;

import java.io.Serializable;

public class ContactProfile implements Serializable {
    String firstname, lastname, email, url, imageName;
    Long phonenumber;
    String contactId;

    public ContactProfile(String firstname, String lastname, String email, String url, String imageName, Long phonenumber, String contactId) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.url = url;
        this.imageName = imageName;
        this.phonenumber = phonenumber;
        this.contactId = contactId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(Long phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
