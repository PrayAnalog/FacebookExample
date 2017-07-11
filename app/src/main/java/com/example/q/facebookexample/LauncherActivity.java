package com.example.q.facebookexample;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;

public class LauncherActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.activity_launcher);
    }
    ImageView imgChicken = (ImageView) findViewById(R.id.imageView);
    Glide.with(this).load(R.drawable.chicken).into(imgChicken);

//    imgChicken.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
    imgChicken.setScaleType(ImageView.ScaleType.CENTER_CROP);

    Handler hd = new Handler();
    hd.postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(new Intent(getApplication(), MainActivity.class));
        finish();
      }
    }, 3000);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.activity_launcher);
    }
  }
}
