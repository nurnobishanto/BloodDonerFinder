package com.zedapp.reddrop_blooddonorfinder;

import android.content.Intent;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
                finish();
            }

        });
        thread.start();
    }

    public void doWork()
    {


        try {


            Thread.sleep(3500);


        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }



    }
    public void startApp(){
        Intent intent;
        intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}