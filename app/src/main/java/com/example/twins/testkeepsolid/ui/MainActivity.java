package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.twins.testkeepsolid.R;
import com.example.twins.testkeepsolid.adapter.ChecklistAdapter;
import com.example.twins.testkeepsolid.data.model.TaskModel;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
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

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private SSLSocket socket;
    private ChecklistAdapter mChecklistAdapter;
    private List<TaskModel> mTaskList = new ArrayList<>();

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
            try {
                initRecycleView();
                getData(sessionID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initRecycleView() {
        mChecklistAdapter = new ChecklistAdapter(this, mTaskList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.checklist_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mChecklistAdapter);
    }

    private void getData(final String sessionID) throws IOException {
        final String ITEM_URL = "rpc.v1.keepsolid.com";
//        final String ITEM_URL = "198.7.62.140";
        final int PORT = 443;
//        final int PORT = 6668;


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Message.Request request = createObject(sessionID);
                byte[] arrayRequest = createArrayRequest(request, 0);
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

//                    printServerCertificate(socket);
//                    printSocketInfo(socket);

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    boolean runSocket = true;
                    int i = 0;
                    while (runSocket) {
                        outputStream.write(arrayRequest);
                        Log.i(TAG, "outputStream.write ");

                        byte[] buffer = readBytes(inputStream);
                        Log.i(TAG, "buffer =  " + Arrays.toString(buffer));
                        Log.i(TAG, "buffer.length =  " + buffer.length); //  4826

                        if (buffer.length > 16) {
                            byte[] arrayProtobuf = Arrays.copyOfRange(buffer, 16, buffer.length);
                            Log.i(TAG, Arrays.toString(arrayProtobuf));
                            Message.Response response = Message.Response.parseFrom(arrayProtobuf);
                            showResponse(response);
                            i++;
                            arrayRequest = createArrayRequest(request, Arrays.copyOfRange(buffer, 0, 8));
                            if (i > 5) runSocket = false;
                        } else {
                            Log.i(TAG, "bad response");
                            runSocket = false;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "IOException = " + e.getMessage());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                    mChecklistAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        Log.i(TAG, "readBytes ");
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int line = 0;
        byte[] buffer = getBufferArrayDefault();
        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            Log.i(TAG, "readBytes _ len =" + len);
            byteBuffer.write(buffer, 0, len);
            if (line == 1) buffer = getBufferArrayBody(buffer);
            if (line == 2) break;
            line++;
        }
        Log.i(TAG, "readBytes _ end ");
        return byteBuffer.toByteArray();
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
    protected void onPause() {
        super.onPause();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
