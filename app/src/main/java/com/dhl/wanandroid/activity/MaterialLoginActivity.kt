package com.dhl.wanandroid.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.LoginBean
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * 登录
 */
class MaterialLoginActivity : BasicActivity() {
    private var userName: EditText? = null
    var passwordEt: EditText? = null
    var fab: FloatingActionButton? = null

    /**
     * 登录信息
     */
    private var loginBean: LoginBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_login)
        toolbar.title = "登录"
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.bt_go -> {
                val name = userName!!.text.toString()
                val password = passwordEt!!.editableText.toString()
                login(name, password)
            }

            R.id.fab -> {
                window.exitTransition = null
                window.enterTransition = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        fab,
                        fab!!.transitionName
                    )
                    startActivity(Intent(this, RegisterActivity::class.java), options.toBundle())
                } else {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            }
        }
    }

    private fun login(userName: String, password: String) {

        //ToastUtils.setMsgColor(getResources().getColor(R.color.blue));
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showLong("请输入账号")
            return
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showLong("密码")
            return
        }
        OkHttpManager.getInstance().login("LOGIN_URL", userName, password, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body!!.string()
                Log.e(TAG, "response::$json")
                val gson = Gson()
                val jsonPrimitive =
                    gson.fromJson(json, JsonObject::class.java).getAsJsonPrimitive("errorCode")
                val errorCode = jsonPrimitive.asInt
                if (errorCode == 0) {
                    val jsonObject = gson.fromJson(json, JsonObject::class.java)
                    val data = jsonObject.getAsJsonObject("data")
                    loginBean = gson.fromJson(data.toString(), LoginBean::class.java)
                    loginBean?.errorCode = 0
                }
                if (response.isSuccessful) { //response 请求成功
                    val headers = response.headers
                    val cookies = headers.values("Set-Cookie")
                    if (cookies.size > 0) {
                        val session = cookies[0]
                        val result = session.substring(0, session.indexOf(";"))
                        val JSESSIONID = result.substring(result.indexOf("=") + 1)
                        Log.e(TAG, "session::$session")
                        Log.e(TAG, "JSESSIONID::$JSESSIONID")
                        SPUtils.getInstance().put("JSESSIONID", JSESSIONID)
                    }
                }
                runOnUiThread {
                    if (loginBean != null && loginBean!!.errorCode == 0) {
                        SPUtils.getInstance().put("userName", userName)
                        SPUtils.getInstance().put("password", password)
                        val intent = Intent()
                        intent.putExtra("loginResult", loginBean)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        //ToastUtils.setMsgColor(getResources().getColor(R.color.light_red,null));
                        ToastUtils.showLong(loginBean!!.errorMsg)
                    }
                }
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    v!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    companion object {
        private const val TAG = "MaterialLoginActivity"
    }
}