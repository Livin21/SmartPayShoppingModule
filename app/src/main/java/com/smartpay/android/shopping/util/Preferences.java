package com.smartpay.android.shopping.util;


import android.content.Context;

public class Preferences {

    public static String getAuthToken(Context context){
        return context.getSharedPreferences("prefs",Context.MODE_PRIVATE).getString("API_AUTH_TOKEN","");
    }

    public static void saveAuthToken(Context context, String token){
        context.getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putString("API_AUTH_TOKEN",token).apply();
    }

    public static String getPrivateKey(Context context){
        return context.getSharedPreferences("prefs",Context.MODE_PRIVATE).getString("WALLET_PRIVATE_KEY","");
    }

    public static void savePrivateKey(Context context, String key) {
        context.getSharedPreferences("prefs",Context.MODE_PRIVATE).edit().putString("WALLET_PRIVATE_KEY",key).apply();
    }
}
