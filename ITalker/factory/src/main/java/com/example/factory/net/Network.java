package com.example.factory.net;

import android.text.TextUtils;

import com.example.common.Common;
import com.example.factory.Factory;
import com.example.factory.persistance.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的封装
 * Created by Administrator on 2017/8/14.
 */

public class Network {
    private static Network instance;
    private Retrofit retrofit;
    private OkHttpClient client;

    static {
        instance = new Network();
    }

    public Network() {
    }

    public static OkHttpClient getClient(){
        if(instance.client != null)
            return instance.client;

        // 得到一个OK client
        OkHttpClient client = new OkHttpClient.Builder()
                // 给所有的请求添加一个拦截器
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // 拿到我们的请求
                        Request original = chain.request();
                        // 重新建一个builder
                        Request.Builder builder = original.newBuilder();
                        if(!TextUtils.isEmpty(Account.getToken())){
                            // 注入一个请求头
                            builder.addHeader("token",Account.getToken());
                        }
                        // 非必须 retrofit框架已经构建好了
                        //builder.addHeader("Content-Type","Application/json");
                        Request newRequest = builder.build();
                        // 返回一个 Response
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        // 存储起来
        instance.client = client;
        return instance.client;
    }

    public static Retrofit getRetrofit(){
        if(instance.retrofit != null)
            return instance.retrofit;

        // 得到一个OK client
        OkHttpClient client = getClient();

        Retrofit.Builder builder = new Retrofit.Builder();

        // 设置电脑连接

        instance.retrofit = builder.baseUrl(Common.Constant.API_URL)
                // 设置client
                .client(client)
                // 是指json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();

        return instance.retrofit;
    }

    /**
     * 返回一个请求代理
     * @return RemoteService
     */
    public static RemoteService remote(){
        return getRetrofit().create(RemoteService.class);
    }

}

