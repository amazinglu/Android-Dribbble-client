package com.example.amazinglu.my_dribbble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.amazinglu.my_dribbble.shot_list.ShotListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * savedInstanceState:
         * a Bundle object containing the activity's previously saved state.
         * If the activity has never existed before, the value of the Bundle object is null
         * */
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance())
                    .commit();
        }
    }
}
