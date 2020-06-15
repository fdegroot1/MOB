package com.example.android.cardgame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_options);
        Button testResetButton = findViewById(R.id.testReset);
        testResetButton.setOnClickListener(v -> {
            reset();
        });
    }

    private void reset() {
        //TODO add ui button to clear cards
        SavedCardSettings.INSTANCE.clear();

        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.TUTORIAL_SHARED_PREFERENCES, 0).edit();
        editor.putBoolean(MainActivity.TUTORIAL_SHARED_PREFERENCES_TUTORIAL_FINISHED, false);
        editor.apply();
    }
}
