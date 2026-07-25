package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 */
public class SessionManager {

    private static String session_ = "";

    public static void setSession(String session) {
        setSession(session, true);
    }

    public static void setSession(String session, boolean saved) {
        session_ = session;
        if (saved) {
            SPUtils.putData(Common.login_key, session_);
        }
    }

    public static String getSession() {
        return session_;
    }

}
