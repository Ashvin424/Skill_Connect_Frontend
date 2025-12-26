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
//    private static String BASE_URL = BuildConfig.BASE_URL; //AWS URL
    private static String BASE_URL = BuildConfig.BASE_URL_LOCAL; //Local url

    public static Retrofit getClient(Context context) {

        // ✅ Always use application context
        if (context == null) {
            context = MyApp.getInstance().getApplicationContext();
        } else {
            context = context.getApplicationContext();
        }

        if (retrofit == null) {
            TokenManager tokenManager = new TokenManager(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new AuthIntercepter(context, tokenManager));

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
}
