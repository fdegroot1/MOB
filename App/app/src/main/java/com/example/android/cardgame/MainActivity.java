package com.example.android.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import mob.app.networking.MOBClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MOBClient.getInstance().start("10.0.2.2", 10_000);
        MOBClient.getInstance().setOnConnection(() -> {
            Log.i(getClass().getSimpleName(), "Connected to server");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MOBClient.getInstance().stop();
    }
}
