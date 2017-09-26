package com.example.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.factory.Factory;
import com.example.utils.HashUtil;

import java.io.File;
import java.util.Date;


/**
 * 上传工具类，用于上传任意文件到阿里OSS存储
 * Created by Administrator on 2017/8/8.
 */

public class UploadHelper {
    // 终结点 与存储区域有关系
    public static final String ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    // 上传的仓库名
    private static final String BUCKET_NAME = "toprovker";

    private static final String TAG=UploadHelper.class.getSimpleName();


    private static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAI0g8P3d6oLvVn", "1IauJGIbOxNTfZ3Q5Tc7EmwgDKM2wh");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法
     * @param objkey 上传文件唯一的key
     * @param path 上传文件的路径
     */
    private static String upload(String objkey,String path){
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objkey, path);

        try{
            // 初始化上传的client
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult result = client.putObject(request);
            // 得到一个外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME,objkey);
            // 格式打印输出
            Log.d(TAG,String.format("PublicObjectURL:%s",url));
            return  url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传本地图片
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path){
        String key = getImageObjkey(path);
        return upload(key,path);
    }

    /**
     * 上传头像
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path){
        String key = getPortraitObjkey(path);
        return upload(key,path);
    }

    /**
     * 上传录音
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadRecordAudio(String path){
        String key = getAudioObjkey(path);
        return upload(key,path);
    }

    /**
     * 分月存储，避免一个文件夹太多文件
     * @return
     */
    private static String getDateString(){
        return DateFormat.format("yyyyMM",new Date()).toString();
    }

    // image/201708/dsdadad54564adsad.jpg
    public static String getImageObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg",dateString,fileMd5);
    }

    // portrait/201708/dsdadad54564adsad.jpg
    public static String getPortraitObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg",dateString,fileMd5);
    }

    // audio/201708/dsdadad54564adsad.mps
    public static String getAudioObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3",dateString,fileMd5);
    }


}
