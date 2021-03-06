package com.satish.facebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.satish.facebook.R;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    int currentTab = 0;
    ViewGroup container;
    LayoutInflater inflater;
    private Toolbar toolbar;
    private SessionManager session;
    private FloatingActionButton floatingActionButton;
    private String profileImageUrl;
    int tab_position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        profileImageUrl = i.getStringExtra("profileImageUrl");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        setUpViewPager();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FeedCreateActivity.class);
                startActivity(i);
            }
        });

        // set the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setUpTabs();

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppController.getInstance().verifySession();
    }

    @Override
    public void onBackPressed() {
        if (currentTab != 0) {
            viewPager.setCurrentItem(0);
            return;
        }

        super.onBackPressed();
    }

    private void setUpTabs() {


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_feed);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_friends);
        //set custom notification tab
        View view = inflater.inflate(R.layout.custom_tab, container, false);

        tabLayout.getTabAt(2).setCustomView(view);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_userprofile);
    }

    private void setUpViewPager() {
        Intent intent = getIntent();
        tab_position = intent.getIntExtra("tab_name", 0);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment());
        adapter.addFragment(new FriendRequesetFragment());
        adapter.addFragment(new NotificationFragment());
        adapter.addFragment(new ProfileFragment());
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_position);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
