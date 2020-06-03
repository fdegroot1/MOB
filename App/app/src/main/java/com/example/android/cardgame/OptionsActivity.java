package com.example.android.cardgame;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
