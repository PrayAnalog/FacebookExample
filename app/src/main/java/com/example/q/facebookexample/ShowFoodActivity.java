package com.example.q.facebookexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.q.facebookexample.util.Food;
import com.example.q.facebookexample.util.Picture;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowFoodActivity extends AppCompatActivity {

  public ArrayList<String> whatWeather = new ArrayList<>();
  public ArrayList<String> foods = new ArrayList<>();
  public String isHumid = "";
  public  String isCloudy = "";
  public String strTemp = "";
  public int temperature = 0;
  public TextView tvWeather;

  public String time;

  public Integer columnNumber = 3;
  public ArrayList<String> emotionArrayList = new ArrayList<>();

  final String scheme = "http";
  final String host = "13.124.41.33";
  final Integer port = 1234;
  private CustomGalleryAdapter adapter = new CustomGalleryAdapter(columnNumber);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_food);
    testFood();

    ImageView showEmotionImageView = (ImageView) findViewById(R.id.showEmotionImageView);
    Glide.with(this).load(R.drawable.anger).into(showEmotionImageView);

    CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
    ctl.setTitle("Details");

    setEmotionArrayList();
    setTime();

    this.foods.add("chicken");
    this.foods.add("bossam");
    this.foods.add("chinese");
    tvWeather = (TextView) findViewById(R.id.tvWeather);
    tvWeather.setText("Hello world!");

    /**
     * let's test
     */

    new ReceiveWeather().execute();
    tvWeather.setVisibility(View.VISIBLE);

    getPictures();
  }

  /**
   * let's test
   */
  public void testFood(){
    try {
      ArrayList<String> foods = new ArrayList<>();
      foods.add("chicken");
      foods.add("bossam");
      foods.add("chinese");
      ArrayList<String> weathers = new ArrayList<>();
      weathers.add("rain");
      weathers.add("snow");
      weathers.add("hot");
      ArrayList<String> emotions = new ArrayList<>();
      emotions.add("angry");
      ArrayList<String> times = new ArrayList<>();
      times.add("night");

      JSONObject test = new Food.FoodBuilder()
          .buildFoodProperty(foods, weathers, emotions, times);
      Iterator<String> keys = test.keys();
      int max = 0;
      String strMax = "";
      int twoMax = 0;
      String strTwoMax = "";

      while (keys.hasNext()){
        String key = String.valueOf(keys.next());
        if(test.getInt(key) > max) {
          max = test.getInt(key);
          strMax = key;
        }
      }
      test.put(strMax, 0);
      keys = test.keys();
      while (keys.hasNext()){
        String key = String.valueOf(keys.next());
        if(test.getInt(key) > twoMax) {
          twoMax = test.getInt(key);
          strTwoMax = key;
        }
      }
      Log.d("[FINAL TEST]", strMax + " " + strTwoMax);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  public void setTime() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    if (hour < 13 || (hour == 13 && minute < 50))
      this.time = "lunch";
    else if (hour < 20 || (hour == 20 && minute < 15))
      this.time = "dinner";
    else
      this.time = "breakfast";

    Log.i("hour", String.valueOf(hour));
    Log.i("minute", String.valueOf(minute));
    Log.i("time", this.time);

  }
  public boolean checkEmotionExist(String emotionString) {
    if (emotionString.contains("E"))
      return false;
    Integer emotionInt = Integer.valueOf(emotionString.substring(2,4));
//    Log.e(emotionString, String.valueOf(emotionInt));
    if (emotionInt < 12)
      return false;
    return true;
  }

  public void setEmotionArrayList() {
    Intent currentIntent = getIntent();
    String anger = currentIntent.getStringExtra("anger");
    String contempt = currentIntent.getStringExtra("contempt");
    String disgust = currentIntent.getStringExtra("disgust");
    String fear = currentIntent.getStringExtra("fear");
    String happiness = currentIntent.getStringExtra("happiness");
    String neutral = currentIntent.getStringExtra("neutral");
    String sadness = currentIntent.getStringExtra("sadness");
    String surprise = currentIntent.getStringExtra("surprise");

    if (checkEmotionExist(anger))
      emotionArrayList.add("anger");
    if (checkEmotionExist(contempt))
      emotionArrayList.add("contempt");
    if (checkEmotionExist(disgust))
      emotionArrayList.add("disgust");
    if (checkEmotionExist(fear))
      emotionArrayList.add("fear");
    if (checkEmotionExist(happiness))
      emotionArrayList.add("happiness");
    if (checkEmotionExist(neutral))
      emotionArrayList.add("neutral");
    if (checkEmotionExist(sadness))
      emotionArrayList.add("sadness");
    if (checkEmotionExist(surprise))
      emotionArrayList.add("surprise");

  }

  public void setFood(String foodName) {
    /**
     * this is example of class Food using.
     * need JSONObjects.
     */
    /**
     * this is example of Food class using
     */
    try {
      JSONObject emotions = new JSONObject();
      JSONObject weathers = new JSONObject();
      JSONObject time = new JSONObject();

      emotions.put("anger", 3);
      emotions.put("happiness", 4);

      weathers.put("rain", 4);
      weathers.put("humid", 10);

      time.put("lunch", 2);

      Food chicken = new Food.Builder()
          .setName("Chicken")
          .setEmotion(emotions)
          .setWeather(weathers)
          .setDay(time)
          .build();
      Log.d("[BuilderTest]", String.valueOf(chicken.getFoodProperty()));
      int chickenProperty = chicken.getFoodProperty();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public class ReceiveWeather extends AsyncTask<URL, Integer, Long> {
    protected Long doInBackground(URL...urls) {
      String url = "http://api.openweathermap.org/data/2.5/weather?lat=37.56826&lon=126.977829&APPID=182e99c0604dd0da45f4cbe349e5f065";
      OkHttpClient client = new OkHttpClient();

      Request request = new Request.Builder()
          .url(url)
          .get()
          .build();

      Response response = null;
      try {
        response = client.newCall(request).execute();
        parseJSON(response.body().string());
        runOnUiThread(new Runnable() {
          public void run() {
            tvWeather.setText("fdsvbdsvsdcds"+isCloudy + isHumid);
//                Toast.makeText(getApplicationContext(), "Every Contacts Synchronized ", Toast.LENGTH_SHORT).show();
          }
        });

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
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

        temperature = temp.getInt("temp")-273;

        int cloudSize = cloud.getInt("all");
        if(cloudSize >= 70) {isCloudy = "very cloudy";}
        else if(cloudSize >= 50) {isCloudy = "cloudy";}
        else {isCloudy = "not cloudy";}

        if(temperature > 25) {strTemp = "hot";}
        else if(temperature > 10) {strTemp = "cold";}
        else {strTemp = "neutral";}

        int humid = temp.getInt("humidity");
        if(humid >= 70){isHumid = "humid";}
        else if(humid >= 30){isHumid = "neutral";}
        else {isHumid = "dry";}

        for(int i = 0; i < baseWeather.length(); i++) {
          JSONObject eachWeather = (JSONObject) baseWeather.get(i);
          whatWeather.add(eachWeather.getString("main"));
        }
        if(isHumid != "neutral") {whatWeather.add(isHumid);}
        whatWeather.add(strTemp);

        TextView tvWeathers = new TextView(ShowFoodActivity.this);
        Log.d("[LOOG]", isHumid+isCloudy);
        tvWeathers.setText(isHumid+isCloudy);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  // display the list by global adapter
  private void displayList() {
    GridView gridView = (GridView) findViewById(R.id.gridView2);
    gridView.setNumColumns(columnNumber);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      gridView.setNestedScrollingEnabled(true);
    }
    gridView.setAdapter(adapter);
  }



  public ArrayList<Picture> jsonToPictureList(String body) {
    ArrayList<Picture> serverPictureList = new ArrayList<>();
    try {
//      Log.i("jsonToPictureList", "parsing start");
      JSONArray items = new JSONArray(body);
//      Log.i("jsonToPictureList", "body to JSONArray");
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

//      Log.i("jsonToPictureList", "parsing finish");
      return serverPictureList;

    } catch (JSONException e) {
      Log.e("jsonToPictureList", e.getMessage());
    }
    return null;
  }

  public ArrayList<Picture> getServerDB(String userID) {
//    Log.i("getServerDB", "start api call");
    OkHttpClient client = new OkHttpClient();
//    Log.i("getServerDB", "open client");
    HttpUrl url = new HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .encodedPath("/api/photos/" + userID)
            .build();

//    Log.i("getServerDB", url.toString());
    Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

//    Log.i("getServerDB", "request build");

    try {
//      Log.i("getServerDB", "just before request execute");
      Response response = client.newCall(request).execute();
//      Log.i("getServerDB", "request sended");
      String body = response.body().string();
      response.close();
//      Log.i("getServerDB", body);
      return jsonToPictureList(body);
    } catch(Exception e) {
      Log.e("getServerDB", "error");
    }
    return null;
  }

  public String foodCategory0 = "chicken";
  public String foodCategory1 = "bossam";

  public void getPictures() {
    adapter = new CustomGalleryAdapter(columnNumber);
    AsyncTask.execute(new Runnable() {

      @Override
      public void run() {
        try {
          // get server DB
          ArrayList<Picture> serverPictureList0 = getServerDB(foodCategory0);
          Log.i("syncServerDB", String.valueOf(serverPictureList0.size()));

          ArrayList<Picture> serverPictureList1 = getServerDB(foodCategory1);
          Log.i("syncServerDB", String.valueOf(serverPictureList1.size()));
          Collections.shuffle(serverPictureList1);

          // set adapter
          adapter.setPictureViewItemList(serverPictureList0);
          for(int i = 0 ; (i < serverPictureList0.size() * 3 / 7) && (i < serverPictureList1.size()) ; i++) {
            Picture tempPicture = serverPictureList1.get(i);
            adapter.addItem(tempPicture.getPhotoID(), tempPicture.getPhotoName(), tempPicture.getPhotoDir(), tempPicture.getThumbnailDir());
          }

          Collections.shuffle(adapter.getPictureViewItemList());

          runOnUiThread(new Runnable() {
            public void run() {
              displayList();
            }
          });

        } catch (Exception e) {
          Log.e("Error", e.getMessage());
        }


      }
    });
  }


}
