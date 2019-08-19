package com.dhl.wanandroid.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class KnowledgeInfochild extends LitePalSupport implements Serializable {


    /**
     * children : [{"children":[],"courseId":13,"cid":60,"name":"Android Studio相关","order":1000,"parentChapterId":150,"userControlSetTop":false,"visible":1},{"children":[],"courseId":13,"cid":169,"name":"gradle","order":1001,"parentChapterId":150,"userControlSetTop":false,"visible":1},{"children":[],"courseId":13,"cid":269,"name":"官方发布","order":1002,"parentChapterId":150,"userControlSetTop":false,"visible":1}]
     * courseId : 13
     * cid : 150
     * name : 开发环境
     * order : 1
     * parentChapterId : 0
     * userControlSetTop : false
     * visible : 1
     */
    @Expose
    private int courseId;
    private long id ;
    @Expose
    @SerializedName("id")
    private int cId;
    @Expose
    private String name;
    @Expose
    private int order;
    @Expose
    private int parentChapterId;
    @Expose
    private boolean userControlSetTop;
    private int visible;

    private KnowledgeInfo knowledgeInfo ;

    public KnowledgeInfo getKnowledgeInfo() {
        return knowledgeInfo;
    }

    public void setKnowledgeInfo(KnowledgeInfo knowledgeInfo) {
        this.knowledgeInfo = knowledgeInfo;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCid() {
        return cId;
    }

    public void setCid(int cid) {
        this.cId = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getParentChapterId() {
        return parentChapterId;
    }

    public void setParentChapterId(int parentChapterId) {
        this.parentChapterId = parentChapterId;
    }

    public boolean isUserControlSetTop() {
        return userControlSetTop;
    }

    public void setUserControlSetTop(boolean userControlSetTop) {
        this.userControlSetTop = userControlSetTop;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }




}
