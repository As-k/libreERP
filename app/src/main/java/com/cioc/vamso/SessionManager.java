package com.cioc.vamso;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 30/04/18.
 */

public class SessionManager {
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private String csrfId = "csrftoken";
    private String sessionId = "sessionid";
    private String STATUS = "status";
    private String pk = "last_updated_pk";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("registered_status", Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public String getCsrfId() {
        return sp.getString(csrfId, "");
    }

    public void setCsrfId(String csrf) {
        spe.putString(csrfId, csrf);
        spe.apply();
    }

    public String getSessionId() {
        return sp.getString(sessionId, "");
    }

    public void setSessionId(String session) {
        spe.putString(sessionId, session);
        spe.apply();
    }

    public boolean getStatus() {
        return sp.getBoolean(STATUS, false);
    }

    public void setStatus(boolean status) {
        spe.putBoolean(STATUS, status);
        spe.commit();
    }

    public int getLastUpdatedPk() {
        return sp.getInt(pk,0);
    }

    public void setLastUpdatedPk(int last_pk) {
        spe.putInt(pk, last_pk);
        spe.apply();
    }

    public void clearAll(){
        spe = sp.edit();
        spe.clear();
        spe.commit();
    }
}
