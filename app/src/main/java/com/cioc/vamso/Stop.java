package com.cioc.vamso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 11/05/18.
 */

public class Stop {

    String stopPk, routePk, name, email, scheduleOn, timing, mobile, street, city, state, pincode, lat, lan, country, address, feedback;
    boolean sumbit, completed;
    JSONObject jsonObject;
    JSONArray jsonArray;
    int num;

    public Stop() {
    }

    public Stop(String stopPk, String routePk, String name, String email, String scheduleOn, String timing, String mobile, String street, String city, String state, String pincode, String lat, String lan,
                String country, String address, String feedback, boolean submit, boolean completed) {
        this.stopPk = stopPk;
        this.routePk = routePk;
        this.name = name;
        this.email = email;
        this.scheduleOn = scheduleOn;
        this.timing = timing;
        this.mobile = mobile;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.lat = lat;
        this.lan = lan;
        this.country = country;
        this.address = address;
        this.feedback = feedback;
        this.sumbit = submit;
        this.completed = completed;
    }

    public Stop(JSONObject jsonObject, JSONArray jsonArray, int num) {
        this.jsonObject = jsonObject;
        this.jsonArray = jsonArray;
        this.num = num;

        try {
//            JSONArray jsonArray = jsonObject.getJSONArray("stops");
//            for (int i=0; i<jsonObject.length(); i++) {

            this.routePk = jsonObject.getString("pk");
            this.sumbit = jsonObject.getBoolean("submit");
            JSONObject json = jsonArray.getJSONObject(num);

            this.timing = json.getString("plannedSlot");
            this.stopPk = json.getString("pk");
            this.feedback = json.getString("feedbackTxt");
            this.completed = json.getBoolean("completed");
            JSONObject objIndividual = json.getJSONObject("individual");
//            this.pk = objIndividual.getString("pk");
            this.name = objIndividual.getString("name");
            this.email = objIndividual.getString("email");
            this.mobile = objIndividual.getString("mobile");
            JSONObject objAdd = objIndividual.getJSONObject("adress");
            this.street = objAdd.getString("street");
            this.city = objAdd.getString("city");
            this.state = objAdd.getString("state");
            this.pincode = objAdd.getString("pincode");
            this.lat = objAdd.getString("lat");
            this.lan = objAdd.getString("lan");
            this.country = objAdd.getString("country");

//                JSONObject jObj = jsonObject.getJSONObject("route");
//                this.routePk = jObj.getString("pk");
//                this.scheduleOn = jObj.getString("scheduledOn");
//                this.feedback = jObj.getString("feedback");
            } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

//    public Stop(JSONObject jsonObject){
//        this.jsonObject = jsonObject;
//    }

    public String getStopPk() {
        return stopPk;
    }

    public void setStopPk(String pk) {
        this.stopPk = pk;
    }

    public String getRoutePk() {
        return routePk;
    }

    public void setRoutePk(String routePk) {
        this.routePk = routePk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getScheduleOn() {
        return scheduleOn;
    }

    public void setScheduleOn(String scheduleOn) {
        this.scheduleOn = scheduleOn;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isSumbit() {
        return sumbit;
    }

    public void setSumbit(boolean sumbit) {
        this.sumbit = sumbit;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
