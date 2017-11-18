package com.example.amazinglu.my_dribbble.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.amazinglu.my_dribbble.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);

        // set the tool bar
        setSupportActionBar(toolbar);
        if (isBackEnabled()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // title of the activity
        setTitle(getActivityTitle());

        // start the fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, newFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // the back button
        if (isBackEnabled() && item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    protected abstract Fragment newFragment();

    public boolean isBackEnabled() {
        return true;
    }

    @NonNull
    public String getActivityTitle() {
        return "";
    }
}
