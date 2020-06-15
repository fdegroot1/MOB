package com.example.android.cardgame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.android.cardgame.ui.TutorialSlideFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TutorialActivity extends AppCompatActivity implements TutorialSlideFragment.TutorialFinishListener, TutorialSlideFragment.TutorialNextListener {
    private static final int PAGES = 3;
    private final List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
            TutorialSlideFragment.getFirst(this),
            TutorialSlideFragment.getSecond(this),
            TutorialSlideFragment.getThird(this)
    ));

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        this.viewPager = findViewById(R.id.tutorialViewPager);
        this.pagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onFinish() {
        setResult(MainActivity.TUTORIAL_FINISH_CODE);
        finish();
    }

    @Override
    public void onNext() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    private class TutorialPagerAdapter extends FragmentStatePagerAdapter {
        public TutorialPagerAdapter(@NonNull FragmentManager fragmentManager, List<Fragment> fragmentList) {
            super(fragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return PAGES;
        }
    }
}