package com.cioc.vamso;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by admin on 30/04/18.
 */

class Backend {
//    static String serverUrl = "http://192.168.1.113:8000/";
//    static String serverUrl = "http://192.168.1.114:8000/";
    static String serverUrl = "http://vamso.cioc.in/";
    public Context context;

    SessionManager sessionManager;

    public Backend(Context context){
        this.context = context;
    }

    public AsyncHttpClient getHTTPClient(){
        sessionManager = new SessionManager(this.context);
        final String csrftoken = sessionManager.getCsrfId();
        final String sessionid = sessionManager.getSessionId();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("X-CSRFToken" , csrftoken);
        client.addHeader("COOKIE" , String.format("csrftoken=%s; sessionid=%s" ,csrftoken,  sessionid));
        return client;
    };

}
