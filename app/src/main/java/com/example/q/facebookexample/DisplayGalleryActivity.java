package com.example.q.facebookexample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.q.facebookexample.util.Picture;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayGalleryActivity extends AppCompatActivity {
    final String scheme = "http";
    final String host = "13.124.41.33";
    final Integer port = 1234;
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public boolean changeSomething = false;

    private CustomGalleryAdapter adapter = new CustomGalleryAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_gallery);

        getPictures();
        displayList();

        GridView pictureGridView = (GridView) findViewById(R.id.gridView1);

        final Intent intent = new Intent(this, DisplayPictureDetailActivity.class);
        pictureGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                changeSomething = true;
                intent.putExtra("photoID", ((Picture) adapter.getItem(position)).getPhotoID());
                intent.putExtra("photoDir", ((Picture) adapter.getItem(position)).getPhotoDir());
                startActivity(intent);
            }
        });
    }

//
    @Override
    protected void onResume() {
        super.onResume();
        if (changeSomething) {
            Log.e("differ", "differ");
            getPictures();
            changeSomething = false;
        }

//        setContentView(R.layout.activity_display_gallery);
    }

//
//    public void showPictureDetail(View view){
//
//        view.getVerticalScrollbarPosition()
//        String pictureDir = "";
//        Log.i("showPictureDetail", pictureDir);
//        startActivity(intent);
//    }


    public void nothingNoticeGalleryTextViewGone() {
        TextView nothingNoticeGalleryTextView = (TextView) findViewById(R.id.nothingNoticeGalleryTextView);
        nothingNoticeGalleryTextView.setVisibility(View.GONE);
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

                newPicture.setPhotoID(item.getString("_id"));
                newPicture.setPhotoDir(scheme + "://" + host + ":" + String.valueOf(port) + "/" + item.getString("photoDir"));
                newPicture.setPhotoName(item.getString("photoName"));
                if (!item.has("thumbDir"))
                    continue;
                newPicture.setThumbnailDir(scheme + "://" + host + ":" + String.valueOf(port) + "/" + item.getString("thumbDir"));

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
                .encodedPath("/api/photos/" + userID)
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
//            Log.i("getServerDB", body);
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
//                userID = "krista";

                // get server DB
                ArrayList<Picture> serverPictureList = getServerDB(userID);
                Log.i("syncServerDB", String.valueOf(serverPictureList.size()));

                // set adapter
                adapter.setPictureViewItemList(serverPictureList);

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


    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    private String encodedImage;
    private String filename;
    private byte[] imageBytes;
    private File file;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("picture", String.valueOf(resultCode));
        Log.i("picture", String.valueOf(Activity.RESULT_OK));
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            changeSomething = true;
            Bitmap picture = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            filename = "ascvdfv";
//            filename = encodedImage.substring(1005, 1010).replace('/', 'e').replace('+', 'e').replace('%', 'e');
            String a = MediaStore.Images.Media.insertImage(getContentResolver(), picture, filename, "");
//            Log.e("filename",encodedImage);
//            Log.e("filename",a);
            Uri uri = Uri.parse(a);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToNext();
            encodedImage = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
//            file = new File(path);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                try {
                    String userID = AccessToken.getCurrentAccessToken().getUserId().toString();
//                    userID = "krista";

                    postServerDB(userID, filename, encodedImage);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }

                try {
                    String userID = AccessToken.getCurrentAccessToken().getUserId().toString();
                    ArrayList<Picture> serverPictureList = getServerDB(userID);
                    Log.i("syncServerDB", String.valueOf(serverPictureList.size()));

                    // set adapter
                    adapter.setPictureViewItemList(serverPictureList);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }


                }
            });

        }
    }


    public void postServerDB(String userID, String pictureName, String picture) {
        Log.i("postServerDB", "start api call : ");
        OkHttpClient client = new OkHttpClient();
        Log.i("postServerDB", "open client");
        HttpUrl url = new HttpUrl.Builder()
                .scheme(scheme)
                .host(host)
                .port(port)
                .encodedPath("/api/photo/" + userID)
                .build();

        Log.i("postServerDB", url.toString());



        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userfile", pictureName + ".png", RequestBody.create(null, new File(picture)))
                .build();

//        Log.i("body", picture);
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



}
