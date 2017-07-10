package com.example.q.facebookexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class FoodPickerActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_food_picker);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.tbFoodPick);
    setSupportActionBar(myToolbar);
    myToolbar.setTitle(R.string.food_picker_photo_title);
  }
}
