package com.azy.locktools.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by
 */
public interface ApiService {

    @POST("/StudentApp/doorLock/openDoor/GetStudentDoor")
    Call<ResponseBody> GetStudentDoor();

    @POST("/StudentApp/Login/Login/StudentLogin")
    @FormUrlEncoded
    Call<ResponseBody> login(@FieldMap Map<String, String> params);
}
