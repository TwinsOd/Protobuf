package com.example.twins.testkeepsolid.data;

import com.example.twins.testkeepsolid.data.model.AuthAnswer;
import com.google.protobuf.Message;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("./")
    Call<AuthAnswer> getSession(@FieldMap Map<String, String> map);

    @GET("./")
    Call<Message> getItems(@Body byte[] b);
}
