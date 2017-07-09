package com.example.q.facebookexample;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

public class DisplayPictureDetailActivity extends AppCompatActivity {
    final String scheme = "http";
    final String host = "13.124.41.33";
    final Integer port = 1234;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String photoID;
    public String photoDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture_detail);
//        Intent intent = getIntent();

        Bundle extras = getIntent().getExtras();
//
        photoID = extras.getString("photoID");
        photoDir = extras.getString("photoDir");


        ImageView detailPictureImageView = (ImageView) findViewById(R.id.detailPictureImageView);
        ImageButton deleteImageButton = (ImageButton) findViewById(R.id.deleteImageButton);


        Picasso.with(getApplicationContext()).load(photoDir).into(detailPictureImageView);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(detailPictureImageView);
    }

    public JSONArray pictureIDListToJsonArray(ArrayList<String> pictureIDList) {
//        JSONObject jObject = new JSONObject();
        try {
            JSONArray jArray = new JSONArray();
            for (String ID : pictureIDList) {
                JSONObject contactJson = new JSONObject();
                contactJson.put("_id", ID);
                jArray.put(contactJson);
            }
            return jArray;
        } catch (Exception e) {
            Log.e("pictureIDList", e.getMessage());
        }
        return null;
    }

    public void deleteServerDB(String userID, ArrayList<String> deletePictureIDList) {
        Log.i("deleteServerDB", "start api call : " + String.valueOf(deletePictureIDList.size()));
        OkHttpClient client = new OkHttpClient();
        Log.i("deleteServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/photo/" + userID)
                .build();

        Log.i("deleteServerDB", url.toString());


        RequestBody body = RequestBody.create(JSON, pictureIDListToJsonArray(deletePictureIDList).toString());

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

    public void deleteImage(View view) {
        AlertDialog.Builder alertDelete = new AlertDialog.Builder(DisplayPictureDetailActivity.this);
        alertDelete.setMessage("Delete this picture").setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        Log.i("photoID", "yes");
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String userID = AccessToken.getCurrentAccessToken().getUserId().toString();
                                    userID = "krista";
                                    ArrayList<String> deletePictureIDList = new ArrayList<String>();
                                    deletePictureIDList.add(photoID);
//                                    deleteServerDB(userID, deletePictureIDList);
                                } catch (Exception e) {
                                    Log.e("Error", e.getMessage());
                                }

                            }
                        });
                        finish();

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'

                        Log.i("photoDir", "no");
                        return;
                    }
                });
        AlertDialog alert = alertDelete.create();
        alert.show();

//        Alert.show("do you want to delete this?", "Confirm Delete", Alert.YES | Alert.NO, null, alertListener, null, Alert.NO);
//        Log.i("photoID", photoID);
//        Log.i("photoDir", photoDir);


    }

}
