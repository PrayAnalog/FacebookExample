package com.example.q.facebookexample;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayContactActivity extends AppCompatActivity {
    final String scheme = "http";
    final String host = "13.124.41.33";
    final Integer port = 1234;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
        adapter = new CustomContactAdapter();
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
            String contactID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String ID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

            adapter.addItem(name, phoneNumber, contactID, ID);
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

        searchBox = (EditText) findViewById(R.id.contactSearchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable arg0) {
                // ignore
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) { // redisplay at all times when type something
                adapter = new CustomContactAdapter();
                searchKeyword = s.toString();

                if (s.equals(""))
                    searchKeyword = null;
                Log.i("input", String.valueOf(s));
                Log.i("keyword", searchKeyword);
                addContacts(searchKeyword);
                displayList();
            }

        });

        //(getApplicationContext(), R.layout.address_row, addContacts());
    }

    @Override
    protected void onResume() {
        super.onResume();
        addContacts(searchKeyword);
        displayList();
        syncServerDB();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public void touchedForKeyboardHiding(View view) {
        hideKeyboard();
    }

    // 누르면 전화하기
    public void callWithNumber(View view) {
        TextView phoneNumberTextView = (TextView) ((LinearLayout) view.getParent()).findViewById(R.id.contactPhoneNumberTextView);
        String phoneNumber = String.valueOf(phoneNumberTextView.getText());
        Log.i("callWithNumber", phoneNumber);
        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
    }

    public ArrayList<Contact> jsonToList(String body) {
        ArrayList<Contact> serverContactList = new ArrayList<>();
        try {
            Log.i("jsonToList", "parsing start");
            JSONArray items = new JSONArray(body);
            Log.i("jsonToList", "body to JSONArray");
            for(int i = 0 ; i < items.length() ; i++) {
                JSONObject item = (JSONObject) items.get(i);
                Contact newContact = new Contact();
                newContact.setID(item.getString("contactID"));
                newContact.setPhoneNumber(item.getString("phoneNum"));
//                newContact.setEmail(item.getString("email"));
                newContact.setName(item.getString("name"));
//                newContact.printContactContent();
                serverContactList.add(newContact);
            }

            Log.i("jsonToList", "parsing finish");
            return serverContactList;

        } catch (JSONException e) {
            Log.e("jsonToList", e.getMessage());
        }
        return null;
    }


    public ArrayList<Contact> getServerDB(String userID) {
        Log.i("getServerDB", "start api call");
        OkHttpClient client = new OkHttpClient();
        Log.i("getServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/contacts/" + userID)
                .build();

        Log.i("getServerDB", url.toString());
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Log.i("getServerDB", "request build");

        try {
            Response response = client.newCall(request).execute();
            Log.i("getServerDB", "request sended");
            String body = response.body().string();
            response.close();
            Log.i("getServerDB", body);
            return jsonToList(body);
        } catch(Exception e) {
            Log.e("getServerDB", e.getMessage());
        }
        return null;
    }

    public void checkDifference(ArrayList<Contact> contactList,
                                ArrayList<Contact> serverContactList,
                                ArrayList<Contact> postContactList,
                                ArrayList<Contact> putContactList,
                                ArrayList<String> deleteContactIDList) {

        ArrayList<Boolean> deleteContactCheckList = new ArrayList<>(Collections.nCopies(serverContactList.size(), true));

        for(int i = 0 ; i < contactList.size() ; i++) {
//            Log.i("checkDifference", contactList.get(i).getName());
            boolean thereIsIt = false;
            for(int j = 0 ; j < serverContactList.size() ; j++) {
//                Log.i("checkDifference", contactList.get(i).getContactID());
//                Log.i("checkDifference", contactList.get(i).getName());
//                Log.i("checkDifference", serverContactList.get(j).getContactID());
//                Log.i("checkDifference", serverContactList.get(j).getName());
//                Log.i("checkDifference", String.valueOf(contactList.get(i).equals(serverContactList.get(j))));
//                Log.i("checkDifference", String.valueOf(contactList.get(i).getContactID().equals(serverContactList.get(j).getContactID())));
                if (contactList.get(i).equals(serverContactList.get(j))) { // same contact in phone and server
//                    Log.i("checkDifference", "same contact");
                    deleteContactCheckList.set(j, false);
                    thereIsIt = true;
                    break;
                }
                else if (contactList.get(i).getID().equals(serverContactList.get(j).getID())) {  // something difference, sync by PUT
//                    Log.i("checkDifference", serverContactList.get(j).getContactID());
//                    Log.i("checkDifference", "same contact id but differnce exist");
                    deleteContactCheckList.set(j, false);
                    putContactList.add(contactList.get(i));
                    thereIsIt = true;
                    break;
                }
            }
            if (!thereIsIt) {
                postContactList.add(contactList.get(i));
            }
        }

        for(int j = 0 ; j < serverContactList.size() ; j++) {
            if (deleteContactCheckList.get(j)) {
//                Log.i("checkDifference", serverContactList.get(j).getContactID());
                deleteContactIDList.add(serverContactList.get(j).getID());
            }
        }
    }

    public JSONArray contactListToJsonArray(ArrayList<Contact> contactList) {
//        JSONObject jObject = new JSONObject();
        try {
            JSONArray jArray = new JSONArray();
            for (Contact contact : contactList) {
                JSONObject contactJson = new JSONObject();
                contactJson.put("contactID", contact.getID());
                contactJson.put("name", contact.getName());
                contactJson.put("phoneNum", contact.getPhoneNumber());
//                contactJson.put("email", contact.getEmail());
                jArray.put(contactJson);
            }
            return jArray;
        } catch (Exception e) {
            Log.e("contactListToJson", e.getMessage());
        }
        return null;
    }

    public JSONArray contactIDListToJsonArray(ArrayList<String> contactIDList) {
//        JSONObject jObject = new JSONObject();
        try {
            JSONArray jArray = new JSONArray();
            for (String ID : contactIDList) {
                JSONObject contactJson = new JSONObject();
                contactJson.put("contactID", ID);
                jArray.put(contactJson);
            }
            return jArray;
        } catch (Exception e) {
            Log.e("contactListToJson", e.getMessage());
        }
        return null;
    }

    public void postServerDB(String userID, ArrayList<Contact> postContactList) {
        Log.i("postServerDB", "start api call : " + String.valueOf(postContactList.size()));
        OkHttpClient client = new OkHttpClient();
        Log.i("postServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/contacts/" + userID)
                .build();

        Log.i("postServerDB", url.toString());


        RequestBody body = RequestBody.create(JSON, contactListToJsonArray(postContactList).toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Log.i("postServerDB", "request build");

        try {
            Response response = client.newCall(request).execute();
            Log.i("postServerDB", "request sended");
            String getBody = response.body().string();
            response.close();
            Log.i("postServerDB", getBody);
        } catch(Exception e) {
            Log.e("postServerDB", e.getMessage());
        }
    }

    public void putServerDB(String userID, ArrayList<Contact> putContactList) {
        Log.i("putServerDB", "start api call : " + String.valueOf(putContactList.size()));
        OkHttpClient client = new OkHttpClient();
        Log.i("putServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/contacts/" + userID)
                .build();

        Log.i("putServerDB", url.toString());


        RequestBody body = RequestBody.create(JSON, contactListToJsonArray(putContactList).toString());

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        Log.i("putServerDB", "request build");

        try {
            Response response = client.newCall(request).execute();
            Log.i("putServerDB", "request sended");
            String getBody = response.body().string();
            response.close();
            Log.i("putServerDB", getBody);
        } catch(Exception e) {
            Log.e("putServerDB", e.getMessage());
        }
    }

    public void deleteServerDB(String userID, ArrayList<String> deleteContactIDList) {
        Log.i("deleteServerDB", "start api call : " + String.valueOf(deleteContactIDList.size()));
        OkHttpClient client = new OkHttpClient();
        Log.i("deleteServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/contacts/" + userID)
                .build();

        Log.i("deleteServerDB", url.toString());


        RequestBody body = RequestBody.create(JSON, contactIDListToJsonArray(deleteContactIDList).toString());

        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        Log.i("deleteServerDB", "request build");

        try {
            Response response = client.newCall(request).execute();
            Log.i("deleteServerDB", "request sended");
            String getBody = response.body().string();
            response.close();
            Log.i("deleteServerDB", getBody);
        } catch(Exception e) {
            Log.e("deleteServerDB", e.getMessage());
        }
    }

    public void syncServerDB() {
        Log.i("Sync", "syncServerDB start");
        // current phone's DS contact list


        AsyncTask.execute(new Runnable() {
//        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // get user id
                    String userID = AccessToken.getCurrentAccessToken().getUserId().toString();
//                    userID = "krista";

                    // get server DB
                    ArrayList<Contact> serverContactList = getServerDB(userID);
                    Log.i("syncServerDB", String.valueOf(serverContactList.size()));

                    // what is difference between phone's DB and server's DB
                    ArrayList<Contact> contactList = adapter.getContactViewItemList();
                    ArrayList<String> deleteContactIDList = new ArrayList<String>();
                    ArrayList<Contact> postContactList = new ArrayList<Contact>();
                    ArrayList<Contact> putContactList = new ArrayList<Contact>();
                    checkDifference(contactList, serverContactList, postContactList, putContactList, deleteContactIDList);

                    // send API call for sync
                    if (postContactList.size() != 0)
                        postServerDB(userID, postContactList);
                    if (putContactList.size() != 0)
                        putServerDB(userID, putContactList);
                    if (deleteContactIDList.size() != 0)
                        deleteServerDB(userID, deleteContactIDList);

                    // after all toast the alarm that every contact sync
//                    Toast.makeText(this, "Every Contacts Synchronized ", Toast.LENGTH_SHORT).show();



                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }


            }
        });


    }


}
