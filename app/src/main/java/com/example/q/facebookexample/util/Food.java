package com.example.q.facebookexample.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by q on 2017-07-11.
 */

public class Food {
  private String name;
  JSONObject emotions = new JSONObject();
  JSONObject weather = new JSONObject();
  JSONObject time = new JSONObject();

  /**
   * these are properties for the status.
   * may not be right.
   * PLEASE CHANGE THESE STATUS TO GOOGLE API'S STATUS VARIABLES
   */
  private void setEmotions(){
    try {
    JSONObject emotions = new JSONObject();
    emotions.put("anger", 1);
    emotions.put("happiness", 1);
    emotions.put("contempt", 1);
    emotions.put("sadness", 1);
    emotions.put("surprize", 1);
  } catch (JSONException e) {
    e.printStackTrace();
  }
  }

  /**
   * these are properties for the weather status.
   * have to change after 회의
   * Now it's for test only.
   */
  private void setWeather(){
    try {
      JSONObject emotions = new JSONObject();
      emotions.put("hot", 1);
      emotions.put("cold", 1);
      emotions.put("modernTemp", 1);
      emotions.put("rain", 1);
      emotions.put("snow", 1);
      emotions.put("humid", 1);
      emotions.put("dry", 1);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * these are properties for the time status.
   * for day and night.
   */
  private void setDay(){
    try {
      JSONObject emotions = new JSONObject();
      emotions.put("day", 1);
      emotions.put("night", 1);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private Food(String name) {
    this.name = name;
    setEmotions();
    setWeather();
    setDay();
  }
  public int getFoodProperty() {
    try {
      int getProperty = 1;
      final Iterator<String> emotionKeys = emotions.keys();
      final Iterator<String> weatherKeys = weather.keys();
      final Iterator<String> timeKeys = time.keys();
      do {
        String key = String.valueOf(emotionKeys.next());
        getProperty *= emotions.getInt(key);
      } while (emotionKeys.hasNext());
      do {
        String key = String.valueOf(weatherKeys.next());
        getProperty *= weather.getInt(key);
      } while (weatherKeys.hasNext());
      do {
        String key = String.valueOf(timeKeys.next());
        getProperty *= time.getInt(key);
      } while (timeKeys.hasNext());
      return getProperty;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * builder for Food
   * Try builder
   * Food inside functions are private
   */
  public static class Builder {
    public String name;
    public Food food = new Food("");

    public Builder(){};
    public Builder setName(String name) {
      this.food.name = name;
      return this;
    }
    public Builder setEmotion(JSONObject emotions) {
      try{
        final Iterator<String> keys = emotions.keys();
        do {
          String key = String.valueOf(keys.next());
          this.food.emotions.put(key, emotions.getInt(key));
        } while (keys.hasNext());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return this;
    }
    public Builder setWeather(JSONObject weathers) {
      try{
        final Iterator<String> keys = weathers.keys();
        do {
          String key = String.valueOf(keys.next());
          this.food.weather.put(key, weathers.getInt(key));
        } while (keys.hasNext());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return this;
    }
    public Builder setDay(JSONObject days) {
      try{
        final Iterator<String> keys = days.keys();
        do {
          String key = String.valueOf(keys.next());
          this.food.time.put(key, days.getInt(key));
        } while (keys.hasNext());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return this;
    }

    public Food build() { return this.food;}
  }

}
