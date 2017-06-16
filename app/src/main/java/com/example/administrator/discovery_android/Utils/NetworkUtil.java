package com.example.administrator.discovery_android.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 用于检测网络状态
 **/

public class NetworkUtil {
    public static boolean isNetworkAvailable(Context context) {
        // 网络连接类型
        int[] networkTypes = {
                ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI
        };
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}