package com.example.fzy.retrofitdemo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/10/12.
 */

public interface BaseService {
    //get请求
    @GET
    Call<ResponseBody> baseGetRequest(@Url String user);
    @GET
    Call<ResponseBody> baseGetImage(@Url String user);

    @Streaming
    @GET
    Call<ResponseBody> baseUploadFile(@Url String fileUrl);
}
