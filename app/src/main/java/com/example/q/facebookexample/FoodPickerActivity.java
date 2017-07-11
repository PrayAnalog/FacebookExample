package com.example.q.facebookexample;

import android.app.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
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
    intent.putExtra("camerasensortype", 2);
    startActivityForResult(intent, 1);
  }

  public byte[] imageBytes;
  public String scores;
  public Intent intent;

  public ArrayList<String> whatWeather = new ArrayList<>();
  public String isHumid = "";
  public String isCloudy = "";
  public String strTemp = "";


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i("picture", String.valueOf(resultCode));
    Log.i("picture", String.valueOf(Activity.RESULT_OK));
    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
      final Intent newIntent = new Intent(this, EmotionLauncherActivity.class);
      startActivity(newIntent);
      Bitmap picture = (Bitmap) data.getExtras().get("data");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      imageBytes = baos.toByteArray();
      intent = new Intent(this, ShowFoodActivity.class);

      AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
          receiveWeather();

          OkHttpClient client = new OkHttpClient();
          Log.i("postServerDB", "open client");
          HttpUrl url = new HttpUrl.Builder()
                  .scheme("https")
                  .host("westus.api.cognitive.microsoft.com")
                  .encodedPath("/emotion/v1.0/recognize")
                  .build();

          Log.i("postServerDB", url.toString());

//          RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{ \"url\": \"http://pds27.egloos.com/pds/201401/12/35/f0364335_52d243d70a27d.jpg\" }");
          RequestBody body = RequestBody.create(null, imageBytes);

          Request request = new Request.Builder()
                  .url(url)
                  .addHeader("content-type", "application/octet-stream")
                  .addHeader("ocp-apim-subscription-key", "74be6ba4f6c64b7696d2fd5754cb07c9")
                  .post(body)
                  .build();

          try {
            Response response = client.newCall(request).execute();
            Log.i("postServerDB", "request sended");
            String getBody = response.body().string();
            response.close();
            scores = getBody;
            Log.i("postServerDB", getBody);

            JSONArray jsonArray = new JSONArray(scores);
            if (jsonArray.length() == 0) {
              Log.i("postServerDB", "length 0");
              runOnUiThread(new Runnable() {
                public void run() {
                  EmotionLauncherActivity.a.finish();
                  Toast.makeText(getApplicationContext(), "take picture with face", Toast.LENGTH_SHORT).show();
                }
              });
            }
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            jsonObject = jsonObject.getJSONObject("scores");
            intent.putExtra("anger", jsonObject.getString("anger"));
            intent.putExtra("contempt", jsonObject.getString("contempt"));
            intent.putExtra("disgust", jsonObject.getString("disgust"));
            intent.putExtra("fear", jsonObject.getString("fear"));
            intent.putExtra("happiness", jsonObject.getString("happiness"));
            intent.putExtra("neutral", jsonObject.getString("neutral"));
            intent.putExtra("sadness", jsonObject.getString("sadness"));
            intent.putExtra("surprise", jsonObject.getString("surprise"));

            intent.putExtra("isHumid", isHumid);
            intent.putExtra("isCloudy", isCloudy);
            intent.putExtra("strTemp", strTemp);
            intent.putStringArrayListExtra("whatWeather", whatWeather);

            runOnUiThread(new Runnable() {
              public void run() {
                finish();
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Every Contacts Synchronized ", Toast.LENGTH_SHORT).show();
              }
            });


          } catch(Exception e) {
            Log.e("postServerDB", e.getMessage());
          }

        }
      });
    }
  }

  public void receiveWeather() {
    String url = "http://api.openweathermap.org/data/2.5/weather?lat=37.56826&lon=126.977829&APPID=182e99c0604dd0da45f4cbe349e5f065";
    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

    try {
      Response response = client.newCall(request).execute();
      parseJSON(response.body().string());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * String -> get weather information.
   * @param weatherBase
   */

  protected void parseJSON(String weatherBase) {
    try {
      JSONObject base = new JSONObject(weatherBase);
      JSONObject temp = (JSONObject) base.getJSONObject("main");
      JSONObject cloud = (JSONObject) base.getJSONObject("clouds");

      JSONArray baseWeather = base.getJSONArray("weather");

      Integer temperature = temp.getInt("temp")-273;

      int cloudSize = cloud.getInt("all");
      if(cloudSize >= 70) {isCloudy = "very cloudy";}
      else if(cloudSize >= 50) {isCloudy = "cloudy";}
      else {isCloudy = "not cloudy";}

      int humid = temp.getInt("humidity");
      if(humid >= 70){isHumid = "humid";}
      else if(humid >= 30){isHumid = "neutral";}
      else {isHumid = "dry";}


      if(temperature >= 25) {strTemp = "hot";}
      else if(temperature >= 10) {strTemp = "neutral"; }
      else {strTemp = "cold";}

      for(int i = 0; i < baseWeather.length(); i++) {
        JSONObject eachWeather = (JSONObject) baseWeather.get(i);
        whatWeather.add(eachWeather.getString("main"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
