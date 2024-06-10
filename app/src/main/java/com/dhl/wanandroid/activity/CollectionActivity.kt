package com.dhl.wanandroid.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dhl.wanandroid.R
import com.dhl.wanandroid.activity.WebActivity.Companion.startActivity
import com.dhl.wanandroid.adapter.CollectionAdapter
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.CollectionBean
import com.dhl.wanandroid.util.APIUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 我的收藏UI
 *
 * @dhl
 */
class CollectionActivity : AppCompatActivity() {
    /**
     * smartRefresh
     */
    protected var refreshLayout: SmartRefreshLayout? = null
    protected var mClassicsHeader: ClassicsHeader? = null

    /**
     * rcy
     */
    protected var recyclerView: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var collectionAdapter: CollectionAdapter? = null
    private var collectionBeanList: MutableList<CollectionBean>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        initRefresh()
    }

    private fun initRefresh() {
        toolbar = findViewById(R.id.tool_bar)
        toolbar?.title = "收藏"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        recyclerView = findViewById(R.id.rcy_view)
        refreshLayout = findViewById(R.id.refreshLayout)
        mClassicsHeader = refreshLayout?.getRefreshHeader() as ClassicsHeader?
        //mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-deta));
        mClassicsHeader!!.setTimeFormat(SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA))
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        collectionBeanList = ArrayList()
        collectionAdapter =
            CollectionAdapter(this, R.layout.fragment_homepage_item, collectionBeanList)
        recyclerView?.setAdapter(collectionAdapter)
        refreshLayout?.autoRefresh()
        refreshLayout?.setOnRefreshListener(OnRefreshListener { getData(APIUtil.getCollectionList(0)) })
    }

    private fun getData(url: String) {
        OkHttpManager.getInstance().getCollectionList(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread { refreshLayout!!.finishRefresh() }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body!!.string()
                Log.e(TAG, "json::$json")
                val fromJson = Gson().fromJson(json, JsonObject::class.java)
                val errorCode = fromJson.getAsJsonPrimitive("errorCode")
                val jsonElement = JsonParser().parse(json)
                if (errorCode.asInt == 0) {
                    val jsonObject = jsonElement.asJsonObject.getAsJsonObject("data")
                    val jsonArray = jsonObject.getAsJsonArray("datas")
                    val gson = Gson()
                    val wxArticleBeans = gson.fromJson<List<CollectionBean>>(
                        jsonArray.toString(),
                        object : TypeToken<List<CollectionBean?>?>() {}.type
                    )
                    collectionBeanList!!.clear()
                    collectionBeanList!!.addAll(wxArticleBeans)
                    runOnUiThread { /* collectionAdapter = new CollectionAdapter(CollectionActivity.this,R.layout.fragment_homepage_item,wxArticleBeans);
                            recyclerView.setAdapter(collectionAdapter);*/
                        collectionAdapter!!.notifyDataSetChanged()
                        refreshLayout!!.finishRefresh()
                        if (wxArticleBeans.size < 20) {
                            refreshLayout!!.setEnableLoadMore(false)
                        }
                        setOnClickEvent()
                    }
                } else {
                    val errorMsg = fromJson.getAsJsonPrimitive("errorMsg")
                    runOnUiThread {
                        ToastUtils.showLong(errorMsg.asString)
                        refreshLayout!!.finishRefresh()
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun setOnClickEvent() {
        collectionAdapter!!.setOnItemClickListener(object :
            MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemClick(
                view: View,
                viewHolder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val collectionBean = collectionBeanList!![position]
                startActivity(this@CollectionActivity, collectionBean.title, collectionBean.link)
            }

            override fun onItemLongClick(
                view: View,
                viewHolder: RecyclerView.ViewHolder,
                i: Int
            ): Boolean {
                return false
            }
        })
        collectionAdapter!!.setOnCollectionListener { view, position ->
            val collectionBean = collectionBeanList!![position]
            OkHttpManager.getInstance().postUnCollection(
                APIUtil.unCollectionArticle(collectionBean.id.toString() + ""),
                collectionBean.originId.toString() + "",
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val rsp = response.body!!.string()
                        val jsonObject = Gson().fromJson(rsp, JsonObject::class.java)
                        val jsonPrimitive = jsonObject.getAsJsonPrimitive("errorCode")
                        if (jsonPrimitive.asInt == 0) {
                            runOnUiThread { ToastUtils.showLong("取消收藏成功") }
                            getData(APIUtil.getCollectionList(0))
                        }
                    }
                })
        }
    }

    companion object {
        private const val TAG = "CollectionActivity"
    }
}