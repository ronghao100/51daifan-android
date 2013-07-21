package com.daifan.activity;

import android.os.Bundle;

import com.daifan.R;
import com.daifan.Singleton;

public class PersonActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        setTitle(Singleton.getInstance().getCurrUser().getName());
    }


    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
