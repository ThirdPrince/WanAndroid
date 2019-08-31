package com.dhl.wanandroid.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.dhl.wanandroid.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 登录
 */
public class MaterialLoginActivity extends AppCompatActivity {

    @InjectView(R.id.et_username)
    EditText userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_login);
        ButterKnife.inject(this);
    }
}
