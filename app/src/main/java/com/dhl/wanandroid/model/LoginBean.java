package com.dhl.wanandroid.model;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

public class LoginBean extends LitePalSupport implements Serializable {


    /**
     * data : {"admin":false,"chapterTops":[],"collectIds":[7503,7505,3298,8831,8830],"email":"","icon":"","id":13053,"nickname":"18796017665","password":"","token":"","type":0,"username":"18796017665"}
     * errorCode : 0
     * errorMsg :
     */

    private int errorCode;
    private String errorMsg;

    /**
     * admin : false
     * chapterTops : []
     * collectIds : [7503,7505,3298,8831,8830]
     * email :
     * icon :
     * id : 13053
     * nickname : 18796017665
     * password :
     * token :
     * type : 0
     * username : 18796017665
     */

    private boolean admin;
    private String email;
    private String icon;
    private int id;
    private String nickname;
    private String password;
    private String token;
    private int type;
    private String username;
    private List<Object> chapterTops;
    private List<Integer> collectIds;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Object> getChapterTops() {
        return chapterTops;
    }

    public void setChapterTops(List<Object> chapterTops) {
        this.chapterTops = chapterTops;
    }

    public List<Integer> getCollectIds() {
        return collectIds;
    }

    public void setCollectIds(List<Integer> collectIds) {
        this.collectIds = collectIds;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
