package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.twins.testkeepsolid.R;

import static com.example.twins.testkeepsolid.Constant.KEY_SESSION_ID;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String sessionID = null;
        if (intent != null)
            sessionID = intent.getStringExtra(KEY_SESSION_ID);//ebe3c2a2-2e17-4b5c-8469-1cdf25bc49e1
        Log.i(TAG, "sessionID = " + sessionID);

    }
}
