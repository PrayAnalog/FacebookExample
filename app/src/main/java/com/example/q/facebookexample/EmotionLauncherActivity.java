package com.example.q.facebookexample;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class EmotionLauncherActivity extends AppCompatActivity {
    public static Activity a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        a = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_launcher);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

}
