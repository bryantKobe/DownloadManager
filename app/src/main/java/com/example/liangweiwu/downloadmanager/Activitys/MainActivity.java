package com.example.liangweiwu.downloadmanager.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.liangweiwu.downloadmanager.Services.FloatingService;
import com.example.liangweiwu.downloadmanager.R;
import com.example.liangweiwu.downloadmanager.Utils.GameInformationUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mFloatingBtn = (Button) findViewById(R.id.floating_btn);
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FloatingService.class);
                startService(intent);
                finish();
            }
        });
        onLaunch();
    }
    private void onLaunch(){
        GameInformationUtils.getInstance(this);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        GameInformationUtils.getInstance().onDestory();
    }
}
