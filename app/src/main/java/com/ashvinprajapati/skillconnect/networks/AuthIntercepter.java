package com.ashvinprajapati.skillconnect.networks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ashvinprajapati.skillconnect.activities.LoginActivity;
import com.ashvinprajapati.skillconnect.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthIntercepter implements Interceptor {
    private Context context;
    private TokenManager tokenManager;

    public AuthIntercepter(Context context, TokenManager tokenManager) {
        this.context = context;
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String token = tokenManager.getToken();
        Log.d("INTERCEPTOR_TOKEN", "Token is: " + token);;

        Request.Builder builder = originalRequest.newBuilder();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(builder.build());

        if (response.code() == 403 || response.code() == 401) {
            tokenManager.clearToken();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        if (!response.isSuccessful()) {
            if (response.code() == 413) {
                Toast.makeText(context, "Image file too large. Please select smaller images.", Toast.LENGTH_SHORT).show();
            }
        }
        return response;
    }
}
