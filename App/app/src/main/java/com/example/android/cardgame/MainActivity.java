package com.example.android.cardgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void launchOptions() {
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivity(intent);
    }

    private void launchGameRules() {
        Intent intent = new Intent(getApplicationContext(), GamerulesActivity.class);
        startActivity(intent);
    }
}
