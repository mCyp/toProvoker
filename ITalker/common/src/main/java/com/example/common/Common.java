package com.example.common;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Common {
    /**
     * 记录一些不变的参数
     * 通常用于一些配置
     */
    public interface Constant{
        // 手机号的正则表达式
        String REGEX_MOBIE = "[1][3,4,5,7,8][0-9]{9}$";

        // 基础的网络请求地址
        //String API_URL = "http://10.17.36.82:8080/api/";
        //String API_URL = "http://192.168.23.1:8080/api/";
        String API_URL = "http://101.132.134.43:8688/Root/api/";

        // 最大的上传图片大小：860kb
        long MAX_UPLOAD_IMAGE_LEGGTH = 860 * 1024;
    }

}
