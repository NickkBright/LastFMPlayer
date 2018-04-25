package com.nickkbright.lastfmplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.ag.lfm.Session;
import com.nickkbright.lastfmplayer.MainActivity;
import com.nickkbright.lastfmplayer.R;


public class LoginActivity extends Activity {

    private EditText mUsername,mPassword;
    private Button mSignIn;
    private ProgressBar mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoading = (ProgressBar) findViewById(R.id.loading);


        mSignIn = (Button) findViewById(R.id.signInBtn);
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUsername.getText() !=null && mPassword.getText()!=null){
                    mLoading.setVisibility(View.VISIBLE);
                    //User sign in.
                    Lfm.login(mUsername.getText().toString(), mPassword.getText().toString(), new Lfm.LfmCallback<Session>() {
                        @Override
                        public void onResult(Session result) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(LfmError error) {
                            Log.e("Error", "onError: "+error.toString() );
                            Toast.makeText(getApplicationContext(),error.errorMessage, Toast.LENGTH_SHORT).show();
                            mLoading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }


}