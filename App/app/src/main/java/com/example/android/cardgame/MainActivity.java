package com.example.android.cardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.cardgame.ui.BattleFragment;
import com.example.android.cardgame.ui.CardFinderFragment;
import com.example.android.cardgame.ui.CatalogusFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_catalogus:
                            openFragment(CatalogusFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_cardFinder:
                            openFragment(CardFinderFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_battle:
                            openFragment(BattleFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gamerules_item:
               setContentView(R.layout.activity_gamerules);
                return true;
            case R.id.options_item:
                setContentView(R.layout.activity_options);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
