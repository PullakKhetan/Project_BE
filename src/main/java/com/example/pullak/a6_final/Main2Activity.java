package com.example.pullak.a6_final;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        CountDownTimer c=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                Intent i=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(i);
            }
        };
        c.start();
    }
}
