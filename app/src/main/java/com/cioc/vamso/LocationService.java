package com.cioc.vamso;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//import com.cioc.mygreendao.db.DaoSession;
//import com.cioc.mygreendao.db.GPSLocation;
//import com.cioc.mygreendao.db.GPSLocationDao;

import com.cioc.vamso.db.DaoSession;
import com.cioc.vamso.db.GPSLocation;
import com.cioc.vamso.db.GPSLocationDao;

import org.greenrobot.greendao.query.Query;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ashish on 2/9/2018.
 */

public class LocationService extends Service implements LocationListener {
    public static final String ACTION = "com.cioc.libreerp.LocationService";
    public static final long INTERVAL = 3000;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;
    TextView locationText1, longitude, latitude;
    String date_time;
    public static String loc;
    public static float distance;
    LocationManager locationManager;
    GPSLocationDao gpsLocationDao;
    Query<GPSLocation> gpsLocation;
    SessionManager sessionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        // cancel if service is  already existed
        sessionManager = new SessionManager(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the note DAO
        DaoSession daoSession = ((AppController) getApplication()).getDaoSession();
        gpsLocationDao = daoSession.getGPSLocationDao();

        // query all notes, sorted a-z by their text
        gpsLocation = gpsLocationDao.queryBuilder().orderAsc(GPSLocationDao.Properties.Id).build();
        isLocationEnabled();

        if(mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(),0,INTERVAL); // schedule task
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "loc Destroy", Toast.LENGTH_SHORT).show();//display toast when method called
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onLocationChanged(Location location) {
        longitude = new TextView(this);
        longitude.setText(location.getLongitude()+"");
        latitude = new TextView(this);
        latitude.setText(location.getLatitude()+"");
        Toast.makeText(LocationService.this, latitude.getText()+"-"+longitude.getText(), Toast.LENGTH_SHORT).show();
        Calendar c = Calendar.getInstance();
        int c_year = c.get(Calendar.YEAR);
        int c_month = c.get(Calendar.MONTH);
        int c_day = c.get(Calendar.DAY_OF_MONTH);
        int c_hr = c.get(Calendar.HOUR_OF_DAY);
        int c_min = c.get(Calendar.MINUTE);
        int c_sec = c.get(Calendar.SECOND);
//        int c_milli_sec = c.get(Calendar.MILLISECOND);
        date_time = c_year+"/"+(c_month+1)+"/"+c_day+" "+c_hr+":"+c_min+":"+c_sec;//+"."+c_milli_sec

        Location dis_loca = new Location("");
        dis_loca.setLatitude(location.getLatitude());
        dis_loca.setLongitude(location.getLongitude());
        distance = location.distanceTo(dis_loca);
        locationText1 = new TextView(this);
        locationText1.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            loc = locationText1.getText() + "\n"+ addresses.get(0).getAddressLine(0)+"\n" +
                    addresses.get(0).getAddressLine(1); //+addresses.get(0).getAddressLine(2);
            locationText1.setText(loc);
            addLocation();
        }catch(Exception e) {
            Log.e("Exception", "Locatin not Inserted"+e);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
//                    Toast.makeText(getApplicationContext(), "Location Notify", Toast.LENGTH_SHORT).show();
                    getLocation();

//                    Toast.makeText(LocationService.this, latitude.getText()+"-"+longitude.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 1, this);
//            Toast.makeText(getApplicationContext(), "Location Notify", Toast.LENGTH_SHORT).show();
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void addLocation() {
        if (sessionManager.getStatus()) {
            GPSLocation gps = new GPSLocation();
            gps.setLongitude_value(longitude.getText().toString());
            gps.setLatitude_value(latitude.getText().toString());
            gps.setDate_time(date_time);
            gpsLocationDao.insert(gps);

//        String Json_data = "{\"updated_location\":{\"id\":"+gps.getId()+",\"latitude\":"+gps.getLatitude_value()+",\"longitude\":" + gps.getLatitude_value() + ",\"datetime\":" + gps.getDate_time()+"\"}";
//
//        try {
//            JSONObject jsonObject = new JSONObject(Json_data);
//            JSONArray jsonArray = jsonObject.getJSONArray("updated_location");
//            for (int i=0; i<jsonArray.length(); i++) {
//                int id = jsonArray.getInt(i);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

            Log.e("DaoExample", "Inserted new gspLocation, ID: " + gps.getId() + " " + gps.getLatitude_value() + " " + gps.getLongitude_value() + " " + gps.getDate_time());
//        Toast.makeText(this, ""+gpsLocationDao.count(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        Log.e("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        boolean res = sessionManager.getStatus();
        if (res) {
            intent = new Intent("com.cioc.libreerp.backendservice");
            intent.putExtra("yourvalue", "torestore");
            sendBroadcast(intent);
            return START_STICKY;
        } else {
//            ComponentName receiver = new ComponentName(this, BackgroundBroadcastReceiver.class);
//            PackageManager pm = this.getPackageManager();
//            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            return START_STICKY_COMPATIBILITY;
        }
    }
}