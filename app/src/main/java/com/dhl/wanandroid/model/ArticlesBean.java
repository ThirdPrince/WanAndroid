package com.dhl.wanandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class ArticlesBean extends LitePalSupport {

    /**
     * apkLink :
     * author : 小编
     * chapterId : 272
     * chapterName : 常用网站
     * collect : false
     * courseId : 13
     * desc :
     * envelopePic :
     * fresh : false
     * id : 1848
     * link : https://developers.google.cn/
     * niceDate : 2018-01-07
     * origin :
     * prefix :
     * projectLink :
     * publishTime : 1515322795000
     * superChapterId : 0
     * superChapterName :
     * tags : []
     * title : Google开发者
     * type : 0
     * userId : -1
     * visible : 0
     * zan : 0
     */

    @Expose
    private String apkLink;
    @Expose
    private String author;
    @Expose
    private int chapterId;
    @Expose
    private String chapterName;
    @Expose
    private boolean collect;
    @Expose
    private int courseId;
    @Expose
    private String desc;
    @Expose
    private String envelopePic;
    @Expose
    private boolean fresh;

    private long id;
    @Expose@SerializedName("id")
    private int artId ;
    @Expose
    private String link;
    @Expose
    private String niceDate;
    @Expose
    private String origin;
    @Expose
    private String prefix;
    @Expose
    private String projectLink;
    @Expose
    private long publishTime;
    @Expose
    private int superChapterId;
    @Expose
    private String superChapterName;
    @Expose
    private String title;
    @Expose
    private int type;
    @Expose
    private int userId;
    @Expose
    private int visible;
    @Expose
    private int zan;
    @Expose
    @Column(ignore = true)
    private List<?> tags;

    private NavInfo navInfo ;

    public int getArtId() {
        return artId;
    }

    public void setArtId(int artId) {
        this.artId = artId;
    }

    public NavInfo getNavInfo() {
        return navInfo;
    }

    public void setNavInfo(NavInfo navInfo) {
        this.navInfo = navInfo;
    }

    public String getApkLink() {
        return apkLink;
    }

    public void setApkLink(String apkLink) {
        this.apkLink = apkLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEnvelopePic() {
        return envelopePic;
    }

    public void setEnvelopePic(String envelopePic) {
        this.envelopePic = envelopePic;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNiceDate() {
        return niceDate;
    }

    public void setNiceDate(String niceDate) {
        this.niceDate = niceDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getProjectLink() {
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getSuperChapterId() {
        return superChapterId;
    }

    public void setSuperChapterId(int superChapterId) {
        this.superChapterId = superChapterId;
    }

    public String getSuperChapterName() {
        return superChapterName;
    }

    public void setSuperChapterName(String superChapterName) {
        this.superChapterName = superChapterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }

    public List<?> getTags() {
        return tags;
    }

    public void setTags(List<?> tags) {
        this.tags = tags;
    }


}
