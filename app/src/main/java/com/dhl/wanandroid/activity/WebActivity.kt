package com.dhl.wanandroid.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.dhl.wanandroid.R
import com.just.agentweb.AgentWeb

/**
 * H5 页面
 * @author dhl
 */
class WebActivity : BasicActivity() {
    private var mAgentWeb: AgentWeb? = null

    private var title: String? = null
    private var url: String? = null

    /**
     * 返回Lay
     */
    private val toolbar: Toolbar by lazy {
        findViewById(R.id.tool_bar)
    }

    private val linearLayout: LinearLayout by lazy {
        findViewById(R.id.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        getIntentData()
        setWebView()
    }


    private fun getIntentData() {

        if (intent != null) {
            title = intent.getStringExtra("title")
            url = intent.getStringExtra("webUrl")
            toolbar!!.title = Html.fromHtml(title)
        }
        setSupportActionBar(toolbar)
        toolbar.run {
            setNavigationIcon(R.mipmap.back)
            setNavigationOnClickListener {
                finish()
            }
        }


    }


    private fun setWebView() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(linearLayout, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (mAgentWeb != null && mAgentWeb!!.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val TAG = "WebActivity"

        @JvmStatic
        fun startActivity(activity: Activity, title: String?, url: String?) {
            val intent = Intent(activity, WebActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("webUrl", url)
            activity.startActivity(intent)
        }
    }
}