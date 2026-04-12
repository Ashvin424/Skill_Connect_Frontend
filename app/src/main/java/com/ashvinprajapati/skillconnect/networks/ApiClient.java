package com.ashvinprajapati.skillconnect.networks;

import android.content.Context;

import com.ashvinprajapati.skillconnect.BuildConfig;
import com.ashvinprajapati.skillconnect.utils.MyApp;
import com.ashvinprajapati.skillconnect.utils.TokenManager;
import com.ashvinprajapati.skillconnect.utils.MyApp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    public static Retrofit getClient(Context context) {

        if (context == null) {
            context = MyApp.getInstance().getApplicationContext();
        } else {
            context = context.getApplicationContext();
        }

        // Always use production URL — switch to BASE_URL_LOCAL only during development
        String baseUrl = BuildConfig.BASE_URL_LOCAL;

        // Rebuild if URL has changed or not yet built
        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            TokenManager tokenManager = new TokenManager(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Only log in debug builds
            logging.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new AuthIntercepter(context, tokenManager))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            currentBaseUrl = baseUrl;
        }

        return retrofit;
    }

    // Call this to force rebuild — useful after logout
    public static void reset() {
        retrofit = null;
        currentBaseUrl = null;
    }
}