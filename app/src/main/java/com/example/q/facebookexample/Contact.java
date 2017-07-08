package com.example.q.facebookexample;

/**
 * Created by q on 2017-07-08.
 */

public class Contact {
    private String name;
    private String phoneNumber;
    private String contactId;

    public void setName(String name) {
        this.name = name;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getName() {
        return this.name;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    public String getContactId() {
        return this.contactId;
    }
}
