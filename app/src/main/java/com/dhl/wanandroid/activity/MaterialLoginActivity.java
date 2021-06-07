package com.dhl.wanandroid.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dhl.wanandroid.R;
import com.dhl.wanandroid.http.OkHttpManager;
import com.dhl.wanandroid.model.LoginBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

import static com.dhl.wanandroid.app.Constants.LOGIN_URL;


/**
 * 登录
 */
public class MaterialLoginActivity extends AppCompatActivity {

    private static final String TAG = "MaterialLoginActivity";

    @InjectView(R.id.login_bg)
    RelativeLayout login_bg ;

    @InjectView(R.id.tool_bar)
    Toolbar toolbar ;
    @InjectView(R.id.et_username)
    EditText userName;

    @InjectView(R.id.et_password)
    EditText passwordEt;

    @InjectView(R.id.fab)
    FloatingActionButton fab;
    /**
     * 登录信息
     */
    private LoginBean loginBean ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_login);
        ButterKnife.inject(this);
        toolbar.setTitle("登录");
       /* ImageBean imageBean = LitePal.findLast(ImageBean.class);
        if (imageBean != null) {
            String imagePath = imageBean.getImagePath();
            if (new File(imagePath).exists()) {
                Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile(imageBean.getImagePath()));
                login_bg.setBackground(drawable);
            }
        }*/
    }

    @OnClick({R.id.bt_go,R.id.fab})
    public  void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.bt_go:
                String name = userName.getText().toString();
                String password = passwordEt.getEditableText().toString();
                login(name,password);
                break;
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
        }

    }
    
    private void login(String userName,String password)
    {

        //ToastUtils.setMsgColor(getResources().getColor(R.color.blue));
        if(TextUtils.isEmpty(userName))
        {
            ToastUtils.showLong("请输入账号");
            return ;
        }

        if(TextUtils.isEmpty(password))
        {
            ToastUtils.showLong("密码");
            return ;
        }
        OkHttpManager.getInstance().login(LOGIN_URL,  userName, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                Log.e(TAG, "response::" + json);
                Gson gson = new Gson();
                JsonPrimitive jsonPrimitive =  gson.fromJson(json, JsonObject.class).getAsJsonPrimitive("errorCode");
                int errorCode = jsonPrimitive.getAsInt();

                if(errorCode == 0)
                {
                    JsonObject jsonObject = gson.fromJson(json,JsonObject.class);
                    JsonObject data =  jsonObject.getAsJsonObject("data");
                    loginBean = gson.fromJson(data.toString(), LoginBean.class);
                    loginBean.setErrorCode(0);
                }


                if (response.isSuccessful()) { //response 请求成功
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    if (cookies.size() > 0) {
                        String session = cookies.get(0);
                        String result = session.substring(0, session.indexOf(";"));
                        String JSESSIONID = result.substring(result.indexOf("=")+1);
                        Log.e(TAG, "session::" + session);
                        Log.e(TAG, "JSESSIONID::" + JSESSIONID);
                        SPUtils.getInstance().put("JSESSIONID",JSESSIONID);
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(loginBean != null && (loginBean.getErrorCode() == 0))
                        {

                            SPUtils.getInstance().put("userName",userName);
                            SPUtils.getInstance().put("password",password);
                            Intent intent = new Intent();
                            intent.putExtra("loginResult",loginBean);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else
                        {
                            //ToastUtils.setMsgColor(getResources().getColor(R.color.light_red,null));
                            ToastUtils.showLong(loginBean.getErrorMsg());
                        }
                    }
                });

            }

        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                );
            }

        }
        return super.dispatchTouchEvent(ev);
    }
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
