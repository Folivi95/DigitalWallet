package com.joshua.digitalwallet;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by JOSHUA on 26/10/2016.
 */

public class WfManager {
    //check whether wifi hotspot is on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Throwable ignored) {
            //empty method
        }
        return false;
    }

    //toggle wifi on or off
    public static boolean configApState(Context context) {
        try {
            //if WiFi is on, turn it off
            WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            wifimanager.setWifiEnabled(true );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }//end method configApState
}
