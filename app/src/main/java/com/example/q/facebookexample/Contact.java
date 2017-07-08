package com.example.q.facebookexample;

import android.util.Log;

/**
 * Created by q on 2017-07-08.
 */

public class Contact {
    private String name;
    private String phoneNumber;
    private String contactID;
    private String ID;

    public void setName(String name) {
        this.name = name;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setContactID(String contactID) { this.contactID = contactID; }
    public void setID(String ID) { this.ID = ID; }

    public String getName() {
        return this.name;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    public String getContactID() {
        return this.contactID;
    }
    public String getID() { return this.ID; }

    public void printContactContent() {
        Log.i("Contact", this.ID);
        Log.i("Contact", this.contactID);
        Log.i("Contact", this.name);
        Log.i("Contact", this.phoneNumber);
    }

//    @Override
    public boolean equals(Contact contact) {
//        return getContactID().equals(contact.getContactID()) && getName().equals(contact.getName());
        return getName().equals(contact.getName()) && this.phoneNumber.equals(contact.getPhoneNumber()) && this.ID.equals(contact.getID());
    }
}
