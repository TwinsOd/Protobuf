package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.data.ApiFactory;

import proto.Message;
import proto.MessageTypeOuterClass;
import proto.MessageWorkgroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (sessionID != null) {
            getData(sessionID);
        }
    }

    private void getData(String sessionID) {
        MessageWorkgroup.WorkGroupsListRequest worGroupkList = MessageWorkgroup.WorkGroupsListRequest.newBuilder()
                .setSessionId(sessionID)
                .build();

        Message.Request request = Message.Request.newBuilder()
                .setMessageType(MessageTypeOuterClass.MessageType.RPC_WORKGROUPS_LIST)
                .setWorkgroupsList(worGroupkList)
                .setServiceType(1)
                .setIsDebug(true)
                .build();

        byte[] bytes = request.toByteArray();

        ApiFactory.itemAdapter().getItems(bytes).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                response.
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
