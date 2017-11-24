package com.example.amazinglu.my_dribbble;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.amazinglu.my_dribbble.login.DribbbleFunc;
import com.example.amazinglu.my_dribbble.login.auth.AuthActivity;
import com.example.amazinglu.my_dribbble.login.auth.AuthFunc;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.activity_login_btn) TextView loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        /**
         * check if the user has login
         * true => get the user information and go to main activity
         * false => go to oauth login
         * */
        try {
            DribbbleFunc.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * if we already have the token
         * check if the user info is up to date
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DribbbleFunc.checkUserInfo(LoginActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (DribbbleFunc.isLogin()) { // go to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else { // go to OAuth activity
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuthFunc.openAuthActivity(LoginActivity.this);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AuthFunc.REQ_CODE && resultCode == RESULT_OK) {
            final String authCode = data.getStringExtra(AuthActivity.KEY_CODE);
            /**
             * use a new thread to get the token
             * */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // get the token
                        String token = AuthFunc.fetchAccessToken(authCode);
                        // store the token and user information to share poreferences
                        DribbbleFunc.login(LoginActivity.this, token);

                        // login success => go to main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
