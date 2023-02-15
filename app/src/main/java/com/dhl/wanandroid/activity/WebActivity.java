package com.dhl.wanandroid.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhl.wanandroid.R;
import com.just.agentweb.AgentWeb;

/**
 * H5 页面
 * @author dhl
 */
public class WebActivity extends AppCompatActivity {


    private static final String TAG = "WebActivity";
    private AgentWeb mAgentWeb;
    private LinearLayout linearLayout;

    /**
     * 返回Lay
     */

    private RelativeLayout back_lay;
    private Toolbar toolbar;

    private TextView toolbar_title;

    private String title;

    private String url;

    private ImageView iv_back;

    public static void startActivity(Activity activity, String title, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("webUrl", url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        getIntentData();
        setWebView();
    }

    private void initView() {
        back_lay = findViewById(R.id.back_lay);
        linearLayout = findViewById(R.id.container);
        toolbar = findViewById(R.id.tool_bar);
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAgentWeb.back()) {
                    finish();
                }
            }
        });
        back_lay.setVisibility(View.VISIBLE);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            url = intent.getStringExtra("webUrl");
            toolbar_title.setText(Html.fromHtml(title));

        }

    }

    private void setWebView() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(linearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
