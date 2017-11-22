package com.example.amazinglu.my_dribbble.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.amazinglu.my_dribbble.model.User;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.amazinglu.my_dribbble.utils.ModelUtils.save;

/**
 * Created by AmazingLu on 11/22/17.
 */

public class DribbbleFunc {

    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";

    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String USER_END_POINT = API_URL + "user";

    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};

    private static String accessToken;
    private static User user;
    private static OkHttpClient client = new OkHttpClient();

    public static void init(@NonNull Context context) {
        accessToken = loadAcesstoken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static boolean isLogin() {
        return accessToken != null;
    }

    public static void login(@NonNull Context context, @NonNull String token) throws IOException {
        // store the token
        DribbbleFunc.accessToken = token;
        storeAccessToken(context, token);
        // get and store the user info
        DribbbleFunc.user = getUser();
        storeUser(context, user);
    }

    public static User getUser() throws IOException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    private static Response makeGetRequest(String url) throws IOException {
        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
        Request request = builder.build();
        return client.newCall(request).execute();
    }

    private static <T> T parseResponse(Response response, TypeToken<T> typeToken) throws IOException {
        String responseString = response.body().string();
        return ModelUtils.toObject(responseString, typeToken);
    }

    // load the token from share preferences
    public static String loadAcesstoken(@NonNull Context context) {
        // Context.MODE_PRIVATE: the created file can onlt be use in this application
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static void storeAccessToken(@NonNull Context context, @Nullable String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH,
                Context.MODE_PRIVATE);
        // use apply 让硬盘储存异步进行
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    // load the user information from share preferences
    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

}
