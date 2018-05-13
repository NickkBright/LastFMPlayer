package com.nickkbright.lastfmplayer;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.nickkbright.lastfmplayer.activities.LoginActivity;
import com.nickkbright.lastfmplayer.fragments.PlayerFragment;
import com.nickkbright.lastfmplayer.fragments.ProfileFragment;


public class MainActivity extends AppCompatActivity {
    public static Context contextOfApplication;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();

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


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.drawer_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();

                    Fragment fragment = null;
                    int id = menuItem.getItemId();

                    if (id == R.id.nav_profile) {
                        fragment = new ProfileFragment();
                    } else if (id == R.id.nav_player) {
                        fragment = new PlayerFragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                    setTitle(menuItem.getTitle());

                    return true;
                }
            });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        mDrawerLayout.addDrawerListener(
            new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    // Respond when the drawer's position changes
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    // Respond when the drawer is opened
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Respond when the drawer is closed
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    // Respond when the drawer motion state changes
                }
            }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
