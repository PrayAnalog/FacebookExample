package com.example.q.facebookexample;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.AccessToken;

import java.util.ArrayList;

public class DisplayContactActivity extends AppCompatActivity {
    private EditText searchBox;
    private String searchKeyword = null;
    private CustomContactAdapter adapter = new CustomContactAdapter();


    // return true if name contains keyword, else return false
    public boolean stringStartsContainsKeyword(String name, String keyword) {
        if (name.toLowerCase().contains(keyword.toLowerCase())) {
            return true;
        }
        return false;
    }

    // crawling contacts.
    // combine name and phoneNumber and add the ArrayList
    // finally return the ArrayList<String>
    public void addContacts(String searchKeyword) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//        if (searchKeyword == null) {
//            phones =
//        } else {
//            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " CONTAINS";
//            String[] selectionArgs = new String[]{searchKeyword};
//            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, selection, selectionArgs, null);
//        }

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            // if searchKeyword null or name does not contains keyword, pass (not add to the list)
            if (searchKeyword != null && !stringStartsContainsKeyword(name, searchKeyword))
                continue;
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

            adapter.addItem(name, phoneNumber, contactId);
        }

        phones.close();
    }


    // display the list by global adapter
    private void displayList() {
        ListView listview = (ListView) findViewById(R.id.listView1);
        listview.setAdapter(adapter);
    }


    // hide soft keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }



    // when this show up create the ContentView and call addContacts() function to collect name and phone-number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
//
//        searchBox = (EditText) findViewById(R.id.contactSearchBox);
//        searchBox.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable arg0) {
//                // ignore
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//            public void onTextChanged(CharSequence s, int start, int before, int count) { // redisplay at all times when type something
//                adapter = new CustomContactAdapter();
//                searchKeyword = s.toString();
//
////                if (s == Character(""))
////                    searchKeyword = null;
//                Log.i("input", String.valueOf(s));
//                Log.i("keyword", searchKeyword);
//                addContacts(searchKeyword);
//                displayList();
//            }
//
//        });

        //(getApplicationContext(), R.layout.address_row, addContacts());
        addContacts(searchKeyword);
        displayList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    // 누르면 전화하기
    public void callWithNumber(View view) {
        hideKeyboard();
//
//        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumber);
//        String phoneNumber = String.valueOf(phoneNumberTextView.getText());
//        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
    }


    public void syncServerDB() {
        ArrayList<Contact> contactList = adapter.getContactViewItemList();
    }


}
