package com.example.q.facebookexample.util;

import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
      emotions.put("neutral", 1);
      emotions.put("fear", 1);
      emotions.put("disgust", 1);
      emotions.put("sadness", 1);
      emotions.put("surprise", 1);
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
      emotions.put("neutral", 1);
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
      emotions.put("lunch", 1);
      emotions.put("dinner", 1);
      emotions.put("breakfast", 1);
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
      while (emotionKeys.hasNext()) {
        String key = String.valueOf(emotionKeys.next());
        getProperty *= emotions.getInt(key);
      }
      while (weatherKeys.hasNext()) {
        String key = String.valueOf(weatherKeys.next());
        getProperty *= weather.getInt(key);
      }
      while (timeKeys.hasNext()) {
        String key = String.valueOf(timeKeys.next());
        getProperty *= time.getInt(key);
      }
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
        while(keys.hasNext()) {
          String key = String.valueOf(keys.next());
          this.food.emotions.put(key, emotions.getInt(key));
        }
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
        while(keys.hasNext()){
          String key = String.valueOf(keys.next());
          this.food.time.put(key, days.getInt(key));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return this;
    }

    public Food build() { return this.food;}
  }

  /**
   * this function is for Food Json building.
   * if jsonBuild(), it returns JSONArray that
   */
  public static class FoodBuilder {
    public String name;

    public static String[] weatherArray = {"hot", "cold", "neutral", "rain", "snow",
        "humid", "dry"};
    public static String[] emotionsArray = {"anger", "contempt", "disgust", "fear",
        "happiness", "neutral", "sadness", "surprise"};
    public static String[] timeArray = {"lunch", "dinner", "breakfast"};


    ArrayList<int[]> chicken = new ArrayList<>();
    ArrayList<int[]> bossam = new ArrayList<>();
    ArrayList<int[]> chinese = new ArrayList<>();

    /**
     * for recognition
     * (hot cold neutral rain snow humid dry)
     * (anger contempt disgust fear happiness neutral sadness surprise
     * (lunch dinner breakfast)
     */
    public void setChicken(){
      int[] weather = {1, 2, 3, 2, 2, 2, 2};
      int[] emotions = {3, 1, 1, 1, 3, 2, 2, 2};
      int[] time = {1, 1, 5};
      chicken.add(weather);
      chicken.add(emotions);
      chicken.add(time);
    }

    /**
     * for recognition
     * (hot cold neutral rain snow humid dry)
     * (anger contempt disgust fear happiness neutral sadness surprise
     * (lunch dinner breakfast)
     */
    public void setBossam(){
      int[] weather = {1, 3, 1, 1, 2, 1, 1};
      int[] emotions = {1, 1, 2, 1, 2, 3, 1, 1};
      int[] time = {1, 5, 1};
      bossam.add(weather);
      bossam.add(emotions);
      bossam.add(time);
    }

    /**
     * for recognition
     * (hot cold neutral rain snow humid dry)
     * (anger contempt disgust fear happiness neutral sadness surprise
     * (lunch dinner breakfast)
     */
    public void setChinese(){
      int[] weather = {1, 1, 1, 2, 2, 1, 2};
      int[] emotions = {1, 1, 1, 1, 2, 2, 3, 1};
      int[] time = {5, 2, 1};
      chinese.add(weather);
      chinese.add(emotions);
      chinese.add(time);
    }

    public FoodBuilder() {
      setChicken();
      setBossam();
      setChinese();
    }

    public JSONObject buildFoodProperty(ArrayList<String> foodNames, ArrayList<String> weathers,
                                 ArrayList<String> emotions, ArrayList<String> times) {
      JSONObject properties = new JSONObject();
      try {
        for (String foodName : foodNames) {
          int[] weatherBase;
          int[] emotionBase;
          int[] timeBase;
          switch (foodName) {
            case "chicken":
              weatherBase = chicken.get(0);
              emotionBase = chicken.get(1);
              timeBase = chicken.get(2);
              break;
            case "bossam":
              weatherBase = bossam.get(0);
              emotionBase = bossam.get(1);
              timeBase = bossam.get(2);
              break;
            case "chinese":
              weatherBase = chinese.get(0);
              emotionBase = chinese.get(1);
              timeBase = chinese.get(2);
              break;
            default:
              weatherBase = chicken.get(0);
              emotionBase = chicken.get(1);
              timeBase = chicken.get(2);
          }
          Builder foodBuilder = new Builder();
          JSONObject weather = new JSONObject();
          JSONObject emotion = new JSONObject();
          JSONObject time = new JSONObject();
          for(String eachW: weathers) {
            for(int i = 0; i<weatherArray.length; i++) {
              if(eachW == weatherArray[i]) {
                weather.put(eachW, weatherBase[i]);
              }
            }
          }

          for(String eachE: emotions) {
            for(int i = 0; i<emotionsArray.length; i++) {
              if(eachE == emotionsArray[i]) {
                emotion.put(eachE, emotionBase[i]);
              }
            }
          }

          for(String eachT: times) {
            for(int i = 0; i<timeArray.length; i++) {
              if(eachT == timeArray[i]) {
                time.put(eachT, timeBase[i]);
              }
            }
          }
          Food newFood = new Builder()
              .setName(foodName)
              .setWeather(weather)
              .setEmotion(emotion)
              .setDay(time)
              .build();
          int foodProperty = newFood.getFoodProperty();
          properties.put(foodName, foodProperty);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return properties;
    }
  }

}
