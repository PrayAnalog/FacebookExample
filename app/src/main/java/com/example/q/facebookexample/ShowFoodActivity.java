package com.example.q.facebookexample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.q.facebookexample.util.Food;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowFoodActivity extends AppCompatActivity {

  public ArrayList<String> whatWeather = new ArrayList<>();
  public String isHumid = "";
  public  String isCloudy = "";
  public int temperature = 0;
  public TextView tvWeather;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_food);

    Log.d("[LOG]", "LOOOOOOOOOOOOOOOOOG");
    tvWeather = (TextView) findViewById(R.id.tvWeather);
    tvWeather.setText("Hello world!");
//
    new ReceiveWeather().execute();



    tvWeather.setVisibility(View.VISIBLE);

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

      time.put("day", 0);

      Food chicken = new Food.Builder()
          .setName("Chicken")
          .setEmotion(emotions)
          .setWeather(weathers)
          .setDay(time)
          .build();

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

        int humid = temp.getInt("humidity");
        if(humid >= 70){isHumid = "very humid";}
        else if(humid >= 30){isHumid = "so - so";}
        else {isHumid = "dry";}

        for(int i = 0; i < baseWeather.length(); i++) {
          JSONObject eachWeather = (JSONObject) baseWeather.get(i);
          whatWeather.add(eachWeather.getString("main"));
        }
        TextView tvWeathers = new TextView(ShowFoodActivity.this);
        Log.d("[LOOG]", isHumid+isCloudy);
        tvWeathers.setText(isHumid+isCloudy);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


}
