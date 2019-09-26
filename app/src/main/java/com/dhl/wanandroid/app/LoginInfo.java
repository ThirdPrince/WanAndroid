package com.dhl.wanandroid.app;


import com.dhl.wanandroid.model.LoginBean;

/**
 * 单例 存放 LoginBean
 */
public class LoginInfo {

    private static  LoginInfo loginInfo ;

    private LoginBean loginBean ;
    private LoginInfo ()
    {

    }

    public static LoginInfo  getInstance()
    {
        if(loginInfo == null)
        {
            synchronized (LoginInfo.class)
            {
                if(loginInfo == null)
                {
                    loginInfo = new LoginInfo();
                }
            }
        }

        return loginInfo ;
    }

    public  void setLoginInfo(LoginBean loginBean)
    {
        this.loginBean = loginBean ;

    }

    public LoginBean getLoginBean()
    {
        return  loginBean ;
    }


}
