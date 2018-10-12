package com.cioc.vamso;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cioc.libreerp.R;

public class SplashActivity extends AppCompatActivity {

    private Handler hd = new Handler();
    SessionManager sessionManager;
    public static boolean res;
    String csrfId, sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        sessionManager = new SessionManager(this);
        getSupportActionBar().hide();
        stopService(new Intent(this, BackgroundService.class));
        stopService(new Intent(this, LocationService.class));

        res = sessionManager.getStatus();
        csrfId = sessionManager.getCsrfId();
        sessionId = sessionManager.getSessionId();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (csrfId.equals("") && sessionId.equals("")) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        },3000);
    }
}
