package com.example.sic;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class HomePageActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fragmentList = new ArrayList<>();


        fragmentList.add(new NewsFeedFragment());
        fragmentList.add(new MessagesFragment());
        fragmentList.add(new PostFragment());
        fragmentList.add(new ProfileFragment());
        fragmentList.add(new SettingsFragment());

        tabLayout = findViewById(R.id.home_page_tab_layout);
        viewPager = findViewById(R.id.home_page_view_pager);

        tabLayout.setupWithViewPager(viewPager, true);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentList);
        viewPager.setAdapter(adapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_feed).getIcon().
                setColorFilter(ContextCompat.getColor(HomePageActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_messages);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_post);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_profile);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_settings);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(HomePageActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(HomePageActivity.this, android.R.color.black), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
