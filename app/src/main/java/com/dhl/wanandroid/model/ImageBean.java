package com.dhl.wanandroid.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

public class ImageBean extends LitePalSupport implements Serializable {

    /**
     * startdate : 20190702
     * fullstartdate : 201907021600
     * enddate : 20190703
     * url : /th?id=OHR.Transfagarasan_ZH-CN5760731327_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp
     * urlbase : /th?id=OHR.Transfagarasan_ZH-CN5760731327
     * copyright : 特兰西瓦尼亚的川斯发格拉山公路，罗马尼亚 (© Calin Stan/Shutterstock)
     * copyrightlink : https://www.bing.com/search?q=%E5%B7%9D%E6%96%AF%E5%8F%91%E6%A0%BC%E6%8B%89%E5%B1%B1%E5%85%AC%E8%B7%AF&form=hpcapt&mkt=zh-cn
     * title :
     * quiz : /search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20190702_Transfagarasan%22&FORM=HPQUIZ
     * wp : true
     * hsh : bb378965e1840e0c9b9748501ea06cc8
     * drk : 1
     * top : 1
     * bot : 1
     * hs : []
     */

    private String startdate;
    private String fullstartdate;
    private String enddate;
    private String url;
    private String urlbase;
    private String copyright;
    private String copyrightlink;
    private String title;
    private String quiz;
    private boolean wp;
    private String hsh;

    private String imagePath ;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private int drk;
    private int top;
    private int bot;
    @Column(ignore = true)
    private List<?> hs;

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getFullstartdate() {
        return fullstartdate;
    }

    public void setFullstartdate(String fullstartdate) {
        this.fullstartdate = fullstartdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlbase() {
        return urlbase;
    }

    public void setUrlbase(String urlbase) {
        this.urlbase = urlbase;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyrightlink() {
        return copyrightlink;
    }

    public void setCopyrightlink(String copyrightlink) {
        this.copyrightlink = copyrightlink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public boolean isWp() {
        return wp;
    }

    public void setWp(boolean wp) {
        this.wp = wp;
    }

    public String getHsh() {
        return hsh;
    }

    public void setHsh(String hsh) {
        this.hsh = hsh;
    }

    public int getDrk() {
        return drk;
    }

    public void setDrk(int drk) {
        this.drk = drk;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBot() {
        return bot;
    }

    public void setBot(int bot) {
        this.bot = bot;
    }

    public List<?> getHs() {
        return hs;
    }

    public void setHs(List<?> hs) {
        this.hs = hs;
    }

}
