package com.example.fzy.retrofitdemo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/10/12.
 * 构建retrofit，抛出去retrofit
 */

public class Home {

    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static BaseService getBaseService() {
        return retrofit.create(BaseService.class);
    }


}
