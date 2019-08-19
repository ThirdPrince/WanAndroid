package com.dhl.wanandroid.util;

import com.dhl.wanandroid.app.Constants;


/**
 * 接口调用
 */
public class APIUtil {

    public static String getHomePageUrl(int page)
    {
        return Constants.HOME_PAGE + "/article/list/"+page+"/json" ;
    }

    public static String getWxArticle(int id ,int page)
    {
        return Constants.WX_ARTICLE + "/list/"+id+"/"+page+"/json";
    }

    /**
     * 获取收藏列表
     * @param page
     * @return
     */
    public static String  getCollectionList(int page)
    {
        return Constants.HOME_PAGE + "/lg/collect/list/"+page+"/json";
    }

    /**
     * 站内
     * @param id
     * @return
     */
    public static String  collectionArticle(String  id)
    {
        return Constants.HOME_PAGE + "/lg/collect/"+id+"/json";
    }

    /**
     * 站外 https://www.wanandroid.com/lg/collect/add/json
     */

    public static String  collectionArticleOut()
    {
        return Constants.HOME_PAGE + "/lg/collect/add/json";
    }

    /**
     * 取消收藏
     * @return
     */
    public static String  unCollectionArticle(String id)
    {
        return Constants.HOME_PAGE + "/lg/uncollect/"+id+"/json";
    }
}
