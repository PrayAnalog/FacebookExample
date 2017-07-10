package com.example.q.facebookexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FoodPickerActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_food_picker);
  }


  public void takePhoto(View view) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, 1);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i("picture", String.valueOf(resultCode));
    Log.i("picture", String.valueOf(Activity.RESULT_OK));
    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
      Bitmap picture = (Bitmap) data.getExtras().get("data");

      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          OkHttpClient client = new OkHttpClient();
          Log.i("postServerDB", "open client");
          HttpUrl url = new HttpUrl.Builder()
                  .scheme("https")
                  .host("westus.api.cognitive.microsoft.com")
                  .encodedPath("/emotion/v1.0/recognize")
                  .build();

          Log.i("postServerDB", url.toString());

          RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{ \"url\": \"http://pds27.egloos.com/pds/201401/12/35/f0364335_52d243d70a27d.jpg\" }");

          Request request = new Request.Builder()
                  .url(url)
                  .addHeader("Content-Type", "application/json")
                  .addHeader("Ocp-Apim-Subscription-Key", "74be6ba4f6c64b7696d2fd5754cb07c9")
                  .post(body)
                  .build();


          Log.i("postServerDB", request.method());

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
      });


    }
  }

}
