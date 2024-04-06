package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class SessionManager {

    private static String session_ = "";

    public static void setSession(String session) {
        session_ = session;
    }

    public static String getSession() {
        return session_;
    }

}
