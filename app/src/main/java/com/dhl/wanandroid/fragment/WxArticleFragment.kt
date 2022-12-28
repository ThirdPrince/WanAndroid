package com.dhl.wanandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dhl.wanandroid.R
import com.dhl.wanandroid.app.Constants
import com.dhl.wanandroid.http.OkHttpManager
import com.dhl.wanandroid.model.WxArticleInfo
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.findAll
import org.litepal.LitePal.saveAll
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [WxArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WxArticleFragment : BaseFragment() {

    private var wxArticleInfoList: MutableList<WxArticleInfo> = mutableListOf()


    /**
     * tabLayout
     */
    private val tabLayout: TabLayout by lazy {
        view!!.findViewById(R.id.tab_layout)
    }

    /**
     * viewPager
     */
    private val viewPager: ViewPager by lazy {
        view!!.findViewById(R.id.content_vp)
    }
    private var wxArticleTabFragmentList: MutableList<WxArticleTabFragment> = mutableListOf()
    private var tabIndicator: MutableList<String> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wx_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
        toolbar.title = "公众号"
        wxArticleInfo
    }


    private val wxArticleInfo: Unit
        private get() {
            wxArticleTabFragmentList = ArrayList()
            tabIndicator = ArrayList()
            wxArticleInfoList = findAll(WxArticleInfo::class.java)
            if (wxArticleInfoList.size > 0) {
                setViewPageTab()
            }
            OkHttpManager.getInstance()[Constants.WX_ARTICLE_URL, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val jsonElement = JsonParser().parse(response.body!!.string())
                    val jsonObject = jsonElement.asJsonObject
                    val jsonArray = jsonObject.getAsJsonArray("data")
                    if (wxArticleInfoList.size == 0) {
                        wxArticleInfoList = Gson().fromJson(jsonArray.toString(), object : TypeToken<List<WxArticleInfo?>?>() {}.type)
                        activity!!.runOnUiThread { setViewPageTab() }
                    } else {
                        wxArticleInfoList = Gson().fromJson(jsonArray.toString(), object : TypeToken<List<WxArticleInfo?>?>() {}.type)
                        // Log.e(TAG,"list=="+knowledgeInfo.toString());
                        activity!!.runOnUiThread {
                            //tabLayout.removeAllTabs();
                            //refreshLayout.finishRefresh();
                            wxArticleTabFragmentList.clear()
                            for (wxArticleInfo in wxArticleInfoList) {
                                // tabLayout.addTab(tabLayout.newTab().setText(wxArticleInfo.getName()));
                                wxArticleTabFragmentList.add(WxArticleTabFragment.newInstance(wxArticleInfo.name, wxArticleInfo.id.toString() + ""))
                            }
                        }
                    }
                    deleteAll(WxArticleInfo::class.java)
                    saveAll(wxArticleInfoList)
                }
            }]
        }

    private fun setViewPageTab() {
        wxArticleTabFragmentList.clear()
        for (wxArticleInfo in wxArticleInfoList!!) {
            tabIndicator!!.add(wxArticleInfo.name)
            wxArticleTabFragmentList!!.add(WxArticleTabFragment.newInstance(wxArticleInfo.name, wxArticleInfo.id.toString() + ""))
        }
        viewPager!!.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(i: Int): Fragment {
                return wxArticleTabFragmentList!![i]
            }

            override fun getCount(): Int {
                return wxArticleTabFragmentList!!.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabIndicator!![position]
            }
        }
        viewPager!!.offscreenPageLimit = wxArticleTabFragmentList!!.size
        tabLayout!!.setupWithViewPager(viewPager)
    }



    companion object {
        private const val TAG = "WxArticleFragment"
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): WxArticleFragment {
            val fragment = WxArticleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}