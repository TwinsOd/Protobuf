package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.twins.testkeepsolid.LoadingData;
import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.adapter.ChecklistAdapter;
import com.example.twins.testkeepsolid.data.model.TaskModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import proto.Message;
import proto.MessageCommon;
import proto.MessageTypeOuterClass;
import proto.MessageWorkgroup;

import static com.example.twins.testkeepsolid.Constant.KEY_SESSION_ID;

public class MainActivity extends AppCompatActivity implements LoadingData {
    private String TAG = "MainActivity";
    private SSLSocket socket;
    private ChecklistAdapter mChecklistAdapter;
    private List<TaskModel> mTaskList = new ArrayList<>();
    private byte[] arrayRequest;
    private ProgressBar progressBar;
    private Executor executorRequest;
    private Executor executorResponse;
    private Message.Request requestProtobufModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String sessionID = null;
        if (intent != null) {
            sessionID = intent.getStringExtra(KEY_SESSION_ID);
            Log.i(TAG, "sessionID = " + sessionID);
        }
        if (sessionID != null) {
            initRecycleView();
            requestProtobufModel = createObject(sessionID);
            arrayRequest = createArrayRequest(requestProtobufModel, 0);
        }
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        executorRequest = Executors.newSingleThreadExecutor();
        executorResponse = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            createSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Executor executorClose = Executors.newSingleThreadExecutor();
        executorClose.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initRecycleView() {
        mChecklistAdapter = new ChecklistAdapter(this, mTaskList, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.checklist_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mChecklistAdapter);
    }

    private void createSocket() throws IOException {
        final String ITEM_URL = "rpc.v1.keepsolid.com";
//        final String ITEM_URL = "198.7.62.140";
        final int PORT = 443;
//        final int PORT = 6668;

        executorResponse.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.i(TAG, "start ");

                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
                    };

                    // Install the all-trusting trust manager
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());

                    SocketFactory socketFactory = sc.getSocketFactory();
                    socket = (SSLSocket) socketFactory.createSocket(ITEM_URL, PORT);

                    printServerCertificate(socket);
                    printSocketInfo(socket);

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    outputStream.write(arrayRequest);
                    Log.i(TAG, "outputStream.write ");

                    int line = 0;
                    byte[] buffer = getBufferArrayDefault();
                    while (inputStream.read(buffer) != -1) {
                        line++;
                        if (line == 1) {
                            arrayRequest = createArrayRequest(requestProtobufModel, buffer);
                        } else if (line == 2) {
                            buffer = getBufferArrayBody(buffer);
                        } else if (line == 3) {
                            Message.Response response = Message.Response.parseFrom(buffer);
                            showResponse(response);
                            buffer = getBufferArrayDefault();
                            line = 0;
                        }
                    }
                    Log.i(TAG, "readBytes _ end ");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "IOException = " + e.getMessage());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showResponse(Message.Response response) {
        int countInfo = response.getWorkgroupsList().getWorkgroupInfoListList().size();
        if (countInfo >= 1) {
            for (MessageCommon.WorkGroupInfo model : response.getWorkgroupsList().getWorkgroupInfoListList()) {
                String json = model.getWorkgroupMetadata();
                Log.i(TAG, "getWorkgroupMetadata() = " + json);
                Gson gson = new Gson();
                TaskModel taskModel = gson.fromJson(json, TaskModel.class);
                taskModel.setType(model.getWorkgroupType());
                mTaskList.add(taskModel);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mChecklistAdapter != null)
                        mChecklistAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private byte[] getBufferArrayBody(byte[] bytes) {
        byte[] arraySizeBody = Arrays.copyOfRange(bytes, 0, 3);
        ByteBuffer buffer = ByteBuffer.wrap(arraySizeBody);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int bufferSize = buffer.getShort();
        Log.i(TAG, "bufferSize Body = " + bufferSize);
        return new byte[bufferSize];
    }

    private byte[] getBufferArrayDefault() {
        final int bufferSizeDefault = 8;
        return new byte[bufferSizeDefault];
    }

    private Message.Request createObject(String sessionID) {
        MessageWorkgroup.WorkGroupsListRequest worGroupList = MessageWorkgroup.WorkGroupsListRequest.newBuilder()
                .setSessionId(sessionID)
                .setFilter(MessageCommon.Filter.EXISTING_ONLY)
                .build();

        return Message.Request.newBuilder()
                .setMessageType(MessageTypeOuterClass.MessageType.RPC_WORKGROUPS_LIST)
                .setWorkgroupsList(worGroupList)
                .setServiceType(1)
                .setIsDebug(true)
                .build();
    }

    private byte[] createArrayRequest(Message.Request request, byte[] arraySequenceNumber) {
        byte[] arrayProto = request.toByteArray();
        int sizeProto = arrayProto.length;
        Log.i(TAG, "arrayProto.length = " + sizeProto);

        ByteBuffer buffer1 = ByteBuffer.allocate(4);
        buffer1.order(ByteOrder.LITTLE_ENDIAN);
        byte[] arrayHeaderPast1 = buffer1.putInt(sizeProto).array();

        ByteBuffer buffer2 = ByteBuffer.allocate(4);
        buffer2.order(ByteOrder.LITTLE_ENDIAN);
        byte[] arrayHeaderPast2 = buffer2.putInt(0).array();

        return arrayMerge(arraySequenceNumber, arrayHeaderPast1, arrayHeaderPast2, arrayProto);
    }

    private byte[] createArrayRequest(Message.Request request, int sequenceNumber) {
        byte[] arraySequenceNumber = ByteBuffer.allocate(8).putInt(sequenceNumber).array();
        return createArrayRequest(request, arraySequenceNumber);
    }

    private byte[] arrayMerge(byte[] arraySequenceNumber, byte[] arrayHeaderPast1, byte[] arrayHeaderPast2, byte[] arrayProto) {
        int sizeSequenceNumber = arraySequenceNumber.length;
        int sizeHeaderPast1 = arrayHeaderPast1.length;
        int sizeHeaderPast2 = arrayHeaderPast2.length;
        int sizeProto = arrayProto.length;

        byte[] newArray = new byte[sizeSequenceNumber + sizeHeaderPast1 + sizeHeaderPast2 + sizeProto];
        int position = 0;
        System.arraycopy(arraySequenceNumber, 0, newArray, position, sizeSequenceNumber);
        position = position + sizeSequenceNumber;
        System.arraycopy(arrayHeaderPast1, 0, newArray, position, sizeHeaderPast1);
        position = position + sizeHeaderPast1;
        System.arraycopy(arrayHeaderPast2, 0, newArray, position, sizeHeaderPast2);
        position = position + sizeHeaderPast2;
        System.arraycopy(arrayProto, 0, newArray, position, sizeProto);

        Log.i(TAG, "arrayRequest.length =  " + newArray.length);
        Log.i(TAG, "arrayRequest =  " + Arrays.toString(newArray));
        return newArray;
    }

    private void printServerCertificate(SSLSocket socket) {
        try {
            Certificate[] serverCerts =
                    socket.getSession().getPeerCertificates();
            for (int i = 0; i < serverCerts.length; i++) {
                Certificate myCert = serverCerts[i];
                Log.i(TAG, "====Certificate:" + (i + 1) + "====");
                Log.i(TAG, "-Public Key-\n" + myCert.getPublicKey());
                Log.i(TAG, "-Certificate Type-\n " + myCert.getType());

                System.out.println();
            }
        } catch (SSLPeerUnverifiedException e) {
            Log.i(TAG, "Could not verify peer");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void printSocketInfo(SSLSocket s) {
        Log.i(TAG, "Socket class: " + s.getClass());
        Log.i(TAG, "   Remote address = "
                + s.getInetAddress().toString());
        Log.i(TAG, "   Remote port = " + s.getPort());
        Log.i(TAG, "   Local socket address = "
                + s.getLocalSocketAddress().toString());
        Log.i(TAG, "   Local address = "
                + s.getLocalAddress().toString());
        Log.i(TAG, "   Local port = " + s.getLocalPort());
        Log.i(TAG, "   Need client authentication = "
                + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        Log.i(TAG, "   Cipher suite = " + ss.getCipherSuite());
        Log.i(TAG, "   Protocol = " + ss.getProtocol());
    }

    @Override
    public void setRequest() {
        Log.i(TAG, "setRequest ");
        progressBar.setVisibility(View.VISIBLE);
        try {
            executorRequest.execute(new SendRequestRunnable(socket.getOutputStream(), arrayRequest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SendRequestRunnable implements Runnable {
        private final OutputStream outputStream;
        private final byte[] arrayRequest;

        private SendRequestRunnable(OutputStream outputStream, byte[] arrayRequest) {
            this.outputStream = outputStream;
            this.arrayRequest = arrayRequest;
        }

        @Override
        public void run() {
            try {
                Log.i("SendRequestRunnable", " outputStream.write");
                outputStream.write(arrayRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
