package com.example.sunflower.api;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    public static final String BASE_URL = "http://192.168.85.224:5000/";

    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        Log.d(TAG, "setAuthToken called, token: " + (token != null ? "YES" : "NO"));
        authToken = token;
        retrofit = null; // Reset để tạo client mới
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating new Retrofit client");

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // ✅ QUAN TRỌNG: Interceptor để thêm token vào header
            okhttp3.Interceptor authInterceptor = chain -> {
                okhttp3.Request original = chain.request();
                okhttp3.Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json");

                if (authToken != null && !authToken.isEmpty()) {
                    Log.d(TAG, "Adding Authorization header: Bearer " + authToken.substring(0, Math.min(20, authToken.length())) + "...");
                    requestBuilder.header("Authorization", "Bearer " + authToken);
                } else {
                    Log.e(TAG, "⚠️ No authToken! Request will be unauthorized.");
                }

                okhttp3.Request request = requestBuilder.build();
                return chain.proceed(request);
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)  // ✅ Thêm interceptor này
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}