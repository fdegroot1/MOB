package com.example.android.cardgame;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Button testResetButton = findViewById(R.id.testReset);
        testResetButton.setOnClickListener(v -> {
            clearCards();
        });
    }

    private void clearCards() {
        //TODO add ui button to clear cards
        SavedCardSettings.INSTANCE.clear();
    }
}
