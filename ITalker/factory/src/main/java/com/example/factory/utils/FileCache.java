package com.example.factory.utils;

import com.example.common.app.Application;
import com.example.factory.net.Network;
import com.example.utils.HashUtil;
import com.example.utils.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HeaderParser;

/**
 * 简单的一个文件缓存，实现文件的下载操作
 * 下载成功后回调相应的方法
 * Created by Administrator on 2017/9/18.
 */

public class FileCache<Holder> {
    // 下载的地址
    private File baseDir;
    // 下载的后缀名
    private String ext;
    private CacheListener<Holder> listener;
    // 目标
    private SoftReference<Holder> holderSoftReference;

    public FileCache(String baseDir,String ext,CacheListener<Holder> listener){
        this.baseDir = new File(Application.getCacheDirFile(),baseDir);
        this.ext = ext;
        this.listener = listener;
    }

    // 构建一个缓存文件，同一个网络对应一个本地文件
    private File buildCacheFile(String path){
        String key = HashUtil.getMD5String(path);
        return new File(baseDir,key + "." + ext);
    }

    // 下载方法
    public void downLoad(Holder holder,String path){
        // 如果路径是本地缓存路径 则不需要下载
        if(path.startsWith(Application.getCacheDirFile().getAbsolutePath())){
            listener.onDoneLoadSucceed(holder,new File(path));
            return;
        }

        // 构建缓存文件
        File fileCache = buildCacheFile(path);
        if(fileCache.exists() && fileCache.length() > 0){
            // 文件存在 无需重新下载
            listener.onDoneLoadSucceed(holder,fileCache);
            return;
        }

        // 把目标进行软引用
        holderSoftReference = new SoftReference<>(holder);
        OkHttpClient client = Network.getClient();
        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new NetCallback(holder,fileCache));
    }

    // 拿最后的目标 只能使用一次
    private Holder getLastHolderAndClear(){
        if(holderSoftReference == null){
            return null;
        }else {
            // 拿并清理
            Holder holder = holderSoftReference.get();
            holderSoftReference.clear();
            return holder;
        }
    }

    private class NetCallback implements Callback{
        private final SoftReference<Holder> holderSoftReference;
        private final File file;

        public NetCallback(Holder holder, File file) {
            this.holderSoftReference = new SoftReference<Holder>(holder);
            this.file = file;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Holder holder = holderSoftReference.get();
            if(holder != null && holder == getLastHolderAndClear()){
                FileCache.this.listener.onDoneFailed(holder);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream = response.body().byteStream();
            if(inputStream != null && StreamUtil.copy(inputStream,file)){
                Holder holder = holderSoftReference.get();
                // 仅仅最后一次才是有效的
                if(holder != null && holder == getLastHolderAndClear()){
                    FileCache.this.listener.onDoneLoadSucceed(holder,file);
                }
            }else {
                onFailure(call,null);
            }
        }
    }

    public interface CacheListener<Holder>{
        // 下载成功
        void onDoneLoadSucceed(Holder holder,File file);
        // 下载失败
        void onDoneFailed(Holder holder);
    }
}
