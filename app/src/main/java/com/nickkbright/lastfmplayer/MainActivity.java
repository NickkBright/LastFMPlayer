package com.nickkbright.lastfmplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.nickkbright.lastfmplayer.activities.LoginActivity;


public class MainActivity extends AppCompatActivity {
    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Lfm.wakeUpSession(new Lfm.LfmCallback<Lfm.LoginState>() {
            @Override
            public void onResult(Lfm.LoginState result) {
                switch (result) {
                    case LoggedOut:
                        //if user logged out go to LoginActivity
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

        mLogout = (Button)findViewById(R.id.logout);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lfm.logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
