package com.nickkbright.lastfmplayer;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.nickkbright.lastfmplayer.activities.LoginActivity;
import com.nickkbright.lastfmplayer.adapters.TabsAdapter;


public class MainActivity extends AppCompatActivity {
    public static Context contextOfApplication;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        Lfm.wakeUpSession(new Lfm.LfmCallback<Lfm.LoginState>() {
            @Override
            public void onResult(Lfm.LoginState result) {
                switch (result) {
                    case LoggedOut:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case LoggedIn:
                        break;
                }
            }

            @Override
            public void onError(LfmError error) {

            }
        });

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        contextOfApplication = getApplicationContext();

        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
