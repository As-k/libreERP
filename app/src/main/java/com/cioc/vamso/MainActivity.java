package com.cioc.vamso;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.libreerp.R;
import com.cioc.vamso.db.DaoMaster;
import com.cioc.vamso.db.DaoSession;
import com.cioc.vamso.db.GPSLocation;
import com.cioc.vamso.db.GPSLocationDao;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.greenrobot.greendao.query.Query;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    TextView userName, emailId, mobileNo;
    ImageView profilePic;
    Button logoutButton, updateButton, routeLists;
    SessionManager sessionManager;
    Backend backend;
    AsyncHttpClient httpclient;
    File file1;
    static int pk;
    public static Switch sw;
    boolean serviceRuning;

    GPSLocationDao gpsLocationDao;
    Query<GPSLocation> query;
    List<GPSLocation> gpsLocation;
    DaoSession daoSession;
    int lst_pk, i;
    long id;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);

        getSupportActionBar().hide();

        backend = new Backend(this);
        httpclient = backend.getHTTPClient();

        userName = findViewById(R.id.user_name);
        emailId = findViewById(R.id.emailId);
        mobileNo = findViewById(R.id.mobileNo);
        sw = findViewById(R.id.sw);
        routeLists = findViewById(R.id.route_lists);

        profilePic = findViewById(R.id.profile_image);

        daoSession = ((AppController) getApplication()).getDaoSession();
        gpsLocationDao = daoSession.getGPSLocationDao();

        serviceRuning = sessionManager.getStatus();

        sw.setChecked(serviceRuning);
//        if (serviveRuning) {
//            startService(new Intent(MainActivity.this, LocationService.class));
//        } else {
//            stopService(new Intent(MainActivity.this, LocationService.class));
//        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setStatus(isChecked);
//                    startService(new Intent(MainActivity.this, BackgroundService.class));
                    startService(new Intent(MainActivity.this, LocationService.class));
                } else {
                    sessionManager.setStatus(isChecked);
//                    stopService(new Intent(MainActivity.this, BackgroundService.class));
                    stopService(new Intent(MainActivity.this, LocationService.class));
                }
            }
        });

//        update();

        httpclient.get(Backend.serverUrl + "/api/HR/users/?mode=mySelf&format=json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("MainActivity","onSuccess");
                try {
                    JSONObject usrObj = response.getJSONObject(0);
                    pk = usrObj.getInt("pk");
                    String username = usrObj.getString("username");
                    String firstName = usrObj.getString("first_name");
                    String lastName = usrObj.getString("last_name");
                    String email = usrObj.getString("email");
                    JSONObject profileObj = usrObj.getJSONObject("profile");

                    String dpLink = profileObj.getString("displayPicture");
                    String mobile = profileObj.getString("mobile");

                    userName.setText(firstName+" "+lastName);
                    emailId.setText(email);
                    if (!mobile.equals("null"))
                        mobileNo.setText(mobile);

//                    String[] image = dpLink.split("/"); //Backend.serverUrl+"/media/HR/images/DP/"
//                    String dp = image[7];
//                    Log.e("image "+dpLink,""+dp);
//                    for (int item=0; item<image.length; item++) {
//                        System.out.println("item = " + item);
//                        Log.e("image"+item,""+image[item]);
//                    }

//                    String path = LoginActivity.file.getAbsolutePath()+"/image";
//                    file1 = LoginActivity.file.getAbsoluteFile();

//                    if (!isExternalStorageWritable()) {
                    httpclient.get(dpLink, new FileAsyncHttpResponseHandler(MainActivity.this) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            // Do something with the file `response`
//                            writeConfigFile(context);

//                            FileOutputStream outputStream;
//                            try {
//                                file1 = new File(Environment.getExternalStorageDirectory()+"/CIOC"+ "/" + dp);
//                                if (file1.exists())
//                                    file1.delete();
////                            file1.createNewFile();
//                                outputStream = new FileOutputStream(file1);
//                                outputStream.write(dp.getBytes());
////                            outputStream.flush();
//                                outputStream.close();
////                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                            } catch (Exception e) {
//                                e.printStackTrace();
////                        }
////
//
////                                bitmap = BitmapFactory.decodeFile(LoginActivity.file + "/" + dp);
////                                if (bitmap != null) {
////                                    profilePic.setImageBitmap(bitmap);
////                                }
//                            }
//                            Log.e("image",""+file1.getAbsolutePath());
                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
                            profilePic.setImageBitmap(pp);


//                            user.setProfilePicture(pp);
//                            user.saveUserToFile(context);
//                            Intent intent = new Intent(context, HomeActivity.class);
//                            startActivity(intent);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.e("failure-image",""+file.getAbsolutePath());
                            System.out.println("failure");
                            System.out.println(statusCode);
                        }
                    });


                } catch (JSONException e){
                    e.printStackTrace();
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("MainActivity","onFailure");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("MainActivity","onFinish");
            }
        });


        routeLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileListActivity.class));
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
            }
        },2000);

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout ?")
                        .setMessage("Are you sure you want to logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.clearAll();
//                                gpsLocationDao.deleteInTx(gpsLocation);
//                                DaoMaster mDaoMaster = AppController.;
                                DaoMaster.dropAllTables(daoSession.getDatabase(), true);
                                DaoMaster.createAllTables(daoSession.getDatabase(), true);
                                File dir = new File(Environment.getExternalStorageDirectory()+"/"+R.string.app_name1);
                                Log.e("MainActivity",""+Environment.getExternalStorageDirectory()+"/"+R.string.app_name1);
                                if (dir.exists())
                                if (dir.isDirectory()) {
                                    String[] children = dir.list();
                                    for (int i = 0; i < children.length; i++)
                                    {
                                        new File(dir, children[i]).delete();
                                    }
                                    dir.delete();
                                }
                                stopService(new Intent(MainActivity.this, BackgroundService.class));
                                stopService(new Intent(MainActivity.this, LocationService.class));
                                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

//        updateButton = findViewById(R.id.update_button);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//            }
//        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Register for the particular broadcast based on ACTION string
//        IntentFilter filter = new IntentFilter(LocationService.ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
//        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Unregister the listener when the application is paused
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
//        // or `unregisterReceiver(testReceiver)` for a normal broadcast
//    }
//
    // Define the callback for what to do when data is received
//    private BroadcastReceiver testReceiver = new BackgroundBroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
//            if (resultCode == RESULT_OK) {
//                String resultValue = intent.getStringExtra("resultValue");
//                Toast.makeText(MainActivity.this, resultValue, Toast.LENGTH_SHORT).show();
//                intent = new Intent("com.cioc.libreerp.backendservice");
//                intent.putExtra("yourvalue", "torestore");
//                sendBroadcast(intent);
//            }
//        }
//    };

    public void update(){

        query = gpsLocationDao.queryBuilder().orderAsc(GPSLocationDao.Properties.Id).build();

        JSONArray jsonArray = new JSONArray();

        lst_pk = sessionManager.getLastUpdatedPk();
        gpsLocation = query.list();
        int j = 0;
        if (lst_pk < gpsLocation.size()) {
            for (j = lst_pk; j < gpsLocation.size(); j++) {
                id = gpsLocation.get(j).getId();
                String latitude = gpsLocation.get(j).getLatitude_value();
                String longitude = gpsLocation.get(j).getLongitude_value();
                String datetime = gpsLocation.get(j).getDate_time();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pk", id);
                    jsonObject.put("user", pk);
                    jsonObject.put("dated", datetime);
                    jsonObject.put("x", latitude);
                    jsonObject.put("y", longitude);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("MainActivity " + id, latitude + " " + longitude + " " + datetime);
            }
            i = j;

            RequestParams params = new RequestParams();
            params.put("jsonArray", jsonArray);


            httpclient.post(Backend.serverUrl + "/api/myWork/locationData/", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    System.out.print("success");
                    Log.e("updated","onSuccess");
                    sessionManager.setLastUpdatedPk(Integer.parseInt(String.valueOf(gpsLocation.get(i-1).getId())));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.print("un-success");
                    Log.e("updated","onFailure");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    System.out.print("finish");
                    Log.e("updated","onFinish");
                }
            });
        } else {
            Log.e("fhgncv","bdfght");
            Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
        }


    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void changePassword(View v){
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }


    @Override
    public void onDestroy() {
//            Log.w("onDestroy", " Destroyed Notification Service");
        super.onDestroy();
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
    }

}
