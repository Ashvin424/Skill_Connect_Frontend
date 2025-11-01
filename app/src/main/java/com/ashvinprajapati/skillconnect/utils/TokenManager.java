package com.ashvinprajapati.skillconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {
    private static final String PREF_NAME = "MY_APP_PREF";
    private static final String KEY_TOKEN = "TOKEN";
    private SharedPreferences prefs;

    public TokenManager(Context context) {
        if (context == null) {
            context = MyApp.getInstance().getApplicationContext();
        }
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        if (token != null && !token.isEmpty()) {
            prefs.edit().putString(KEY_TOKEN, token).apply();
            Log.d("TOKEN_SAVED", "Token: " + token);
        }
    }

    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d("TOKEN_RETRIEVE", "Saved token now: " + token);
        return token;
    }

    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
