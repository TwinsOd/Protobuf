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
        if (intent != null) {
            sessionID = intent.getStringExtra(KEY_SESSION_ID);//ebe3c2a2-2e17-4b5c-8469-1cdf25bc49e1
            Log.i(TAG, "intent != null _ sessionID = " + sessionID);
        }
//        if (sessionID == null) sessionID = "5c43704e-e578-4d72-bdf2-624cbeb8686c";
//        Log.i(TAG, "sessionID = " + sessionID);
        if (sessionID != null) {
            getData(sessionID);
        }
    }

    private void getData(String sessionID) {
        MessageWorkgroup.WorkGroupsListRequest worGroupList = MessageWorkgroup.WorkGroupsListRequest.newBuilder()
                .setSessionId(sessionID)
                .build();

        Message.Request request = Message.Request.newBuilder()
                .setMessageType(MessageTypeOuterClass.MessageType.RPC_WORKGROUPS_LIST)
                .setWorkgroupsList(worGroupList)
                .setServiceType(1)
                .setIsDebug(true)
                .build();

        ApiFactory.itemAdapter().getItems(request).enqueue(new Callback<Message.Response>() {
            @Override
            public void onResponse(Call<Message.Response> call, Response<Message.Response> response) {
                Log.i(TAG, "getMessageType = " + response.body().getMessageType());
                Log.i(TAG, "getErrorCode = " + response.body().getErrorCode());
                Log.i(TAG, "getRequestId = " + response.body().getRequestId());
                int countInfo = response.body().getWorkgroupsList().getWorkgroupInfoListCount();
                Log.i(TAG, "countInfo = " + countInfo);
                if (countInfo >= 1) {
                    Log.i(TAG, "getWorkgroupType() = " + response.body().getWorkgroupsList().getWorkgroupInfoList(0).getWorkgroupType());
                    Log.i(TAG, "Metadata() = " + response.body().getWorkgroupsList().getWorkgroupInfoList(0).getWorkgroupMetadata());
                }
                if (countInfo >= 2) {
                    Log.i(TAG, "getWorkgroupType() = " + response.body().getWorkgroupsList().getWorkgroupInfoList(1).getWorkgroupType());
                    Log.i(TAG, "Metadata() = " + response.body().getWorkgroupsList().getWorkgroupInfoList(1).getWorkgroupMetadata());
                }
            }
            @Override
            public void onFailure(Call<Message.Response> call, Throwable t) {

            }
        });
    }
}
