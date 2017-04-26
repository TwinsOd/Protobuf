package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.twins.testkeepsolid.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.cert.Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import proto.Message;
import proto.MessageTypeOuterClass;
import proto.MessageWorkgroup;

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
            sessionID = intent.getStringExtra(KEY_SESSION_ID);
            Log.i(TAG, "sessionID = " + sessionID);
        }

        if (sessionID != null) {
            try {
                getData(sessionID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getData(final String sessionID) throws IOException {
//        final String ITEM_URL = "rpc.v1.keepsolid.com";
        final String ITEM_URL = "198.7.62.140";
//        final int PORT = 443;
        final int PORT = 6668;


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Message.Request request = createObject(sessionID);
                final byte[] arrayRequest = createArrayRequest(request);

                try {
                    Log.i(TAG, "start ");

                    SocketFactory socketFactory = SSLSocketFactory.getDefault();
                    SSLSocket socket = (SSLSocket) socketFactory.createSocket(ITEM_URL, PORT);

                    printServerCertificate(socket);
                    printSocketInfo(socket);

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    outputStream.write(arrayRequest);

                    Message.Response response = Message.Response.parseFrom(inputStream);

                    Log.i(TAG, "getMessageType = " + response.getMessageType());
                    Log.i(TAG, "getErrorCode = " + response.getErrorCode());
                    Log.i(TAG, "getRequestId = " + response.getRequestId());
                    int countInfo = response.getWorkgroupsList().getWorkgroupInfoListList().size();
                    Log.i(TAG, "countInfo = " + countInfo);
                    if (countInfo >= 1) {
                        Log.i(TAG, "getWorkgroupType() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(0).getWorkgroupType());
                        Log.i(TAG, "getWorkgroupMetadata() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(0).getWorkgroupMetadata());
                    }
                    if (countInfo >= 2) {
                        Log.i(TAG, "getWorkgroupType() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(1).getWorkgroupType());
                        Log.i(TAG, "getWorkgroupMetadata() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(1).getWorkgroupMetadata());
                    }

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "IOException = " + e.getMessage());
                }
            }
        }).start();
    }

    private Message.Request createObject(String sessionID) {
        MessageWorkgroup.WorkGroupsListRequest worGroupList = MessageWorkgroup.WorkGroupsListRequest.newBuilder()
                .setSessionId(sessionID)
                .build();

        return Message.Request.newBuilder()
                .setMessageType(MessageTypeOuterClass.MessageType.RPC_WORKGROUPS_LIST)
                .setWorkgroupsList(worGroupList)
                .setServiceType(1)
                .setIsDebug(true)
                .build();
    }

    private byte[] createArrayRequest(Message.Request request) {
        byte[] arrayProto = request.toByteArray();
        int sizeProto = arrayProto.length;
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] arrayHeaderPast1 = buffer.putInt(0).array();
        byte[] arrayHeaderPast2 = buffer.putInt(sizeProto).array();
        int sequenceNumber = 0;
        byte[] arraySequenceNumber = ByteBuffer.allocate(8).putInt(sequenceNumber).array();
        return arrayMerge(arraySequenceNumber, arrayHeaderPast1, arrayHeaderPast2, arrayProto);
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
        Log.i(TAG, "   Cipher suite = " + ss.getCipherSuite());//TLS_DHE_RSA_WITH_AES_128_CBC_SHA
        Log.i(TAG, "   Protocol = " + ss.getProtocol());//TLSv1
    }


}
