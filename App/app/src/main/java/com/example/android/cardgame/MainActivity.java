package com.example.android.cardgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String TUTORIAL_SHARED_PREFERENCES = "TutorialSettings";
    public static final String TUTORIAL_SHARED_PREFERENCES_TUTORIAL_FINISHED = "finished";
    public static final int TUTORIAL_REQUEST_CODE = 0;
    public static final int TUTORIAL_FINISH_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(TUTORIAL_SHARED_PREFERENCES, 0);
        boolean tutorialFinished = sharedPreferences.getBoolean(TUTORIAL_SHARED_PREFERENCES_TUTORIAL_FINISHED, false);

        if (!tutorialFinished) {
            launchTutorial();
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavController NavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, NavController);
        SavedCardSettings.INSTANCE.setContext(this.getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gamerules_item:
                launchGameRules();
                return true;
            case R.id.options_item:
                launchOptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TUTORIAL_REQUEST_CODE && resultCode == TUTORIAL_FINISH_CODE) {
            SharedPreferences.Editor editor = getSharedPreferences(TUTORIAL_SHARED_PREFERENCES, 0).edit();
            editor.putBoolean(TUTORIAL_SHARED_PREFERENCES_TUTORIAL_FINISHED, true);
            editor.apply();
        }
    }

    private void launchTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivityForResult(intent, TUTORIAL_REQUEST_CODE);
    }

    private void launchOptions() {
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivity(intent);
    }

    private void launchGameRules() {
        Intent intent = new Intent(getApplicationContext(), GamerulesActivity.class);
        startActivity(intent);
    }
}
