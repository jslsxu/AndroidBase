package com.haocai.app.network.base.apis;

import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.haocai.app.BuildConfig;
import com.haocai.app.application.MyApplication;
import com.haocai.app.bean.LoginData;
import com.haocai.app.network.base.callback.DownloadCallback;
import com.haocai.app.network.base.callback.ResponseCallback;
import com.haocai.app.network.base.response.BaseResponse;
import com.robin.lazy.cache.CacheLoaderManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by jslsxu on 16/12/19.
 */

public class HttpApiBase implements CommonInterface {
    public static final int GET_METHOD = 1;
    public static final int POST_METHOD = 2;
    protected static final int LIMIT = 20;
    /**
     * 0为正式线上环境，1为开发环境
     */
    static int HOST = BuildConfig.API_ENV;
    private final static String[] IPS = {BuildConfig.API_HOST, BuildConfig.DEBUG_API_HOST};

    public static String getSecureBaseUrl() {
        return IPS[HOST];
    }

    public static void get(String url, Map<String, String> params, final ResponseCallback responseCallback) {
        execute(GET_METHOD, url, params, responseCallback);
    }

    public static void post(String url, Map<String, String> params, final ResponseCallback responseCallback) {
        execute(POST_METHOD, url, params, responseCallback);
    }

    public static void execute(int method, String url, Map<String, String> params, final ResponseCallback responseCallback) {
        String requestUrl = getSecureBaseUrl() + url;
        HashMap validateMap = addCommonParams(params);
        RequestCall requestCall;
        if (method == GET_METHOD) {
            requestCall = OkHttpUtils.get().url(requestUrl).params(validateMap).build();
        } else {
            requestCall = OkHttpUtils.post().url(requestUrl).params(validateMap).build();
        }
        requestCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (responseCallback != null) {
                    responseCallback.onFail(BaseResponse.NETWORK_ERROR, null, null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (TextUtils.isEmpty(response)) {
                    if (responseCallback != null) {
                        responseCallback.onFail(BaseResponse.NETWORK_ERROR, null, null);
                    }
                } else {
                    BaseResponse httpResponse = null;
                    try {
                        httpResponse = GsonManager.getGson().fromJson(response, responseCallback.getClazz());
                    } catch (JsonIOException | JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (httpResponse != null) {
                        if (responseCallback != null) {
                            if (httpResponse.success()) {
                                responseCallback.onSuccess(httpResponse);
                            } else {
                                responseCallback.onFail(httpResponse.getErrno(), httpResponse, null);
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFail(BaseResponse.NETWORK_ERROR, null, null);
                        }
                    }
                }
            }
        });
    }

    public static HashMap addCommonParams(Map<String, String> params) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("platform", "android");
        String city_id = CacheLoaderManager.getInstance().loadString("city_id");
        if (!TextUtils.isEmpty(city_id)) {
            paramsMap.put("city_id", city_id);
        }

        String loginString = CacheLoaderManager.getInstance().loadString("LoginInfo");
        LoginData loginData = null;
        try {
            loginData = GsonManager.getGson().fromJson(loginString, LoginData.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(loginData != null){
            String token = loginData.getData().getToken();
            if (!TextUtils.isEmpty(token)) {
                paramsMap.put("token", token);
            }
        }

        if (!TextUtils.isEmpty(MyApplication.device_id)) {
            paramsMap.put("device_id", MyApplication.device_id);
        }
        if (!TextUtils.isEmpty(MyApplication.os_version)) {
            paramsMap.put("os_version", MyApplication.os_version);
        }
        paramsMap.put("version", BuildConfig.VERSION_NAME);
        paramsMap.put("channel", BuildConfig.FLAVOR);
        if (params != null) {
            paramsMap.putAll(params);
        }

        return paramsMap;
    }

    public static void download(String url, String destinationDir, String fileName, final DownloadCallback callback) {
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(destinationDir, fileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (callback != null) {
                    callback.onFail(e);
                }
            }

            @Override
            public void onResponse(File response, int id) {
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (callback != null) {
                    callback.onProgress(progress, total);
                }
            }
        });
    }
}
