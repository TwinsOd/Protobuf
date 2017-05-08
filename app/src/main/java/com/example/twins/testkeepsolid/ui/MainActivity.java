package com.example.twins.testkeepsolid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.twins.testkeepsolid.R;

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
import java.util.Arrays;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import proto.Message;
import proto.MessageCommon;
import proto.MessageTypeOuterClass;
import proto.MessageWorkgroup;

import static com.example.twins.testkeepsolid.Constant.KEY_SESSION_ID;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private SSLSocket socket;

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

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
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

                    while (runSocket) {
                        outputStream.write(arrayRequest);
                        Log.i(TAG, "outputStream.write ");

                        byte[] buffer = readBytes(inputStream);
                        // [1, 0, 0, 0, 0, 0, 0, 0, 46, 0, 0, 0, 0, 0, 0, 0, 8, 64, -126, 4, 3, 8, -112, 3, -128, -128, 1, -112, 3, -118, -128, 1, 12, 66, 97, 100, 32, 114, 101, 113, 117, 101, 115, 116, 46, -110, -128, 1, 13, 54, 69, 69, 54, 58, 52, 54, 55, 55, 51, 51, 53, 55]
                        Log.i(TAG, "buffer =  " + Arrays.toString(buffer));
                        Log.i(TAG, "buffer.length =  " + buffer.length); //  4826

                        if (buffer.length > 16) {
                            byte[] arrayProtobuf = Arrays.copyOfRange(buffer, 16, buffer.length);
                            Log.i(TAG, Arrays.toString(arrayProtobuf));
                            Message.Response response = Message.Response.parseFrom(arrayProtobuf);
                            printResponse(response);
                            arrayRequest = createArrayRequest(request, Arrays.copyOfRange(buffer, 0, 8));
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

    private void printResponse(Message.Response response) {
        Log.i(TAG, "Message.Response.parseFrom");

        Log.i(TAG, "getMessageType = " + response.getMessageType()); // getMessageType = RPC_WORKGROUPS_LIST
        Log.i(TAG, "getErrorCode = " + response.getErrorCode()); //200
        if (response.getErrorMessageList() != null && response.getErrorMessageList().size() > 0)
        Log.i(TAG, "MessageList().get(0) = " + response.getErrorMessageList().get(0)); // OK
        Log.i(TAG, "getRequestId = " + response.getRequestId());//8B1:3BDC612A - different
        int countInfo = response.getWorkgroupsList().getWorkgroupInfoListList().size();
        Log.i(TAG, "countInfo = " + countInfo);//8
        Log.i(TAG, "getErrorCode = " + response.getWorkgroupsList().getErrorCode());//200
        Log.i(TAG, "getLastEventId = " + response.getWorkgroupsList().getLastEventId());//1418401
        if (countInfo >= 1) {
            Log.i(TAG, "getWorkgroupType() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(0).getWorkgroupType()); //1001
            Log.i(TAG, "getWorkgroupMetadata() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(0).getWorkgroupMetadata());
        }
        if (countInfo >= 2) {
            Log.i(TAG, "getWorkgroupType() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(1).getWorkgroupType());
            Log.i(TAG, "getWorkgroupMetadata() = " + response.getWorkgroupsList().getWorkgroupInfoListList().get(1).getWorkgroupMetadata());
        }
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        Log.i(TAG, "readBytes ");
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 10240;
        byte[] buffer = new byte[bufferSize];
        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            Log.i(TAG, "readBytes _ len =" + len);
            byteBuffer.write(buffer, 0, len);
            if (len > 8) break;
        }
        //I/Choreographer: Skipped 41 frames!  The application may be doing too much work on its main thread.
        Log.i(TAG, "readBytes _ end ");
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
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
        Log.i(TAG, "arrayProto.length = " + sizeProto);//51

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

        Log.i(TAG, "arrayRequest.length =  " + newArray.length);//67
        Log.i(TAG, "arrayRequest =  " + Arrays.toString(newArray));
        //[0, 0, 0, 0, 0, 0, 0, 0, 51, 0, 0, 0, 0, 0, 0, 0, 8, 64, -126, 4, 38, 10, 36, 57, 100, 99, 52, 50, 54, 99, 97, 45, 50, 48, 97, 57, 45, 52, 52, 50, 100, 45, 56, 99, 57, 101, 45, 48, 50, 56, 102, 52, 54, 54, 50, 49, 53, 53, 99, -128, -128, 2, 1, -120, -128, 2, 1]
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
