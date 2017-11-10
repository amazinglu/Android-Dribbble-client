package com.example.amazinglu.my_dribbble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.amazinglu.my_dribbble.shot_list.ShotListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance())
                    .commit();

        }
    }
}
