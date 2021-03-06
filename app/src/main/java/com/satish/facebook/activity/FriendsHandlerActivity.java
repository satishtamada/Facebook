package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.satish.facebook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 13/9/15.
 */
public class FriendsHandlerActivity extends AppCompatActivity {
    private static String TAG = FriendsHandlerActivity.class.getSimpleName();
    ViewPager viewPager;
    TabLayout tabLayout;
    int tab_position;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        setUpViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(getResources().getColor(R.color.tab_unselected), getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        Intent intent = getIntent();
        tab_position = intent.getIntExtra("tab_name", 0);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsListFragment(), "FRIENDS");
        adapter.addFragment(new SuggestionsFragment(), "SUGGESTIONS");
        adapter.addFragment(new FriendRequesetFragment(), "REQUESTS");
        viewPager.setAdapter(adapter);

        Log.e(TAG, "tabPosition: " + tab_position);

        viewPager.setCurrentItem(tab_position);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
