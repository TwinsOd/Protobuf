package com.example.twins.testkeepsolid.data;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;


public class ApiFactory {
//        private static final String AUTH_URL = "https://dev-auth.simplexsolutionsinc.com";// 302
    private static final String AUTH_URL = "https://auth.simplexsolutionsinc.com";// 302
    //    private static final String ITEM_URL = "https://198.7.62.140:6668";
    private static final String ITEM_URL = "https://rpc.v1.keepsolid.com:443";
    //or Address: rpc.v1.keepsolid.com, port: 443


    public static ApiService authAdapter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(ApiService.class);
    }

    public static ApiService itemAdapter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ITEM_URL)
                .addConverterFactory(ProtoConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(ApiService.class);
    }
}