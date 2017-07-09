package com.example.q.facebookexample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayGalleryActivity extends AppCompatActivity {
    final String scheme = "http";
    final String host = "13.124.41.33";
    final Integer port = 1234;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private CustomGalleryAdapter adapter = new CustomGalleryAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_gallery);
    }

    public void nothingNoticeGalleryTextViewGone() {
        TextView nothingNoticeGalleryTextView = (TextView) findViewById(R.id.nothingNoticeGalleryTextView);
        nothingNoticeGalleryTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPictures();
    }

    // display the list by global adapter
    private void displayList() {
        GridView gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(adapter);
    }


    public ArrayList<Picture> jsonToPictureList(String body) {
        ArrayList<Picture> serverPictureList = new ArrayList<>();
        try {
            Log.i("jsonToPictureList", "parsing start");
            JSONArray items = new JSONArray(body);
            Log.i("jsonToPictureList", "body to JSONArray");
            for(int i = 0 ; i < items.length() ; i++) {
                JSONObject item = (JSONObject) items.get(i);
                Picture newPicture = new Picture();
                newPicture.setPhotoDir(scheme + "://" + host + ":" + String.valueOf(port) + "/" + item.getString("photoDir"));
                newPicture.setPhotoName(item.getString("photoName"));
//                newPicture.setThumbnailDir(item.getString("thumbDir"));

                serverPictureList.add(newPicture);
            }

            Log.i("jsonToPictureList", "parsing finish");
            return serverPictureList;

        } catch (JSONException e) {
            Log.e("jsonToPictureList", e.getMessage());
        }
        return null;
    }


    public ArrayList<Picture> getServerDB(String userID) {
        Log.i("getServerDB", "start api call");
        OkHttpClient client = new OkHttpClient();
        Log.i("getServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/photo/" + userID)
                .build();

        Log.i("getServerDB", url.toString());
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Log.i("getServerDB", "request build");

        try {
            Log.i("getServerDB", "just before request execute");
            Response response = client.newCall(request).execute();
            Log.i("getServerDB", "request sended");
            String body = response.body().string();
            response.close();
            Log.i("getServerDB", body);
            return jsonToPictureList(body);
        } catch(Exception e) {
            Log.e("getServerDB", "error");
        }
        return null;
    }


    public void getPictures() {
        adapter = new CustomGalleryAdapter();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String userID = AccessToken.getCurrentAccessToken().getUserId().toString();
                    userID = "krista";

                    // get server DB
                    ArrayList<Picture> serverPictureList = getServerDB(userID);

                    // after all toast the alarm that every contact sync
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Every Contacts Synchronized ", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.i("syncServerDB", String.valueOf(serverPictureList.size()));

                    // set adapter
                    adapter.setPictureViewItemList(serverPictureList);

                    displayList();
                    if (adapter.getCount() != 0) { // there is any picture
                        nothingNoticeGalleryTextViewGone();  // hide notice take a picture or uploading text view
                        for(int i = 0 ; i < adapter.getCount() ; i++) {
                            Log.i("photoDir", adapter.getPictureViewItemList().get(i).getPhotoDir());

                        }
                    }

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }


            }
        });


    }
}
