package com.example.amazinglu.my_dribbble.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.amazinglu.my_dribbble.base.DribbbleException;
import com.example.amazinglu.my_dribbble.model.Like;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private static final String SHOTS_END_POINT = API_URL + "shots";

    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};

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

    public static void logout(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);
        accessToken = null;
        user = null;
    }

    public static User getCurrentUser() {
        return user;
    }

    /**
     * add the token together with the url
     * */
    private static Response makeGetRequest(String url) throws IOException {
        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
        Request request = builder.build();
        return client.newCall(request).execute();
    }

    private static <T> T parseResponse(Response response, TypeToken<T> typeToken) throws IOException {
        String responseString = response.body().string();
        /**
         * responseString is a JSON base string
         * when we set the typeToken to Shot class
         * JSON will automatically 根据变量名 set value
         * */
        return ModelUtils.toObject(responseString, typeToken);
    }

    /**
     * load and store the token from share preferences
     * */
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

    public static void removeAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH,
                Context.MODE_PRIVATE);
        sp.edit().remove(KEY_ACCESS_TOKEN).apply();
    }

    /**
     * load and store the user information from share preferences
     */
    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    /**
     * get user infor from Dribbble API
     * */
    public static User getUser() throws IOException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    /**
     * get shots from Dribbble API
     * */
    public static List<Shot> getShots(int page) throws IOException {
        String url = SHOTS_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static List<Like> getLikes(int page) throws IOException {
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }

    public static List<Shot> getLikedShots(int page) throws IOException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }

}
