package com.cioc.vamso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.cioc.libreerp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ProfileListActivity extends AppCompatActivity {

    TextView date, dataNotFound;
    ImageView previous, next;
    RecyclerView profileDetailsList;
    ProfileDetailsAdapter profileDetailsAdapter;
    int c_day, c_month, c_year;
    String strDate1, strDate, routePk;
    AsyncHttpClient client;
    Backend backend;
    List<Stop> stops;
    Button travelAllowance;
    boolean submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        getSupportActionBar().hide();

        date = findViewById(R.id.current_date);
        previous = findViewById(R.id.backward);
        next = findViewById(R.id.forward);
        profileDetailsList = findViewById(R.id.profilesDetails);
        dataNotFound = findViewById(R.id.dataNotFound);
        dataNotFound.setVisibility(View.GONE);

        backend = new Backend(this);
        client = backend.getHTTPClient();
        stops = new ArrayList<Stop>();

        Calendar c = Calendar.getInstance();
//        c_day = c.get(Calendar.DAY_OF_MONTH);
//        c_month = c.get(Calendar.MONTH);
//        c_year = c.get(Calendar.YEAR);

        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMM yyyy"); //yyyy-MM-dd
        strDate1 = mdformat.format(c.getTime());
        date.setText(strDate1);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        strDate = df.format(c.getTime());

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataNotFound.setVisibility(View.GONE);
                c.add(Calendar.DAY_OF_YEAR, -1);
                strDate = df.format(c.getTime());
                strDate1 = mdformat.format(c.getTime());
                date.setText(""+strDate1);
                getData();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataNotFound.setVisibility(View.GONE);
                c.add(Calendar.DAY_OF_YEAR, 1);
                strDate = df.format(c.getTime());
                strDate1 = mdformat.format(c.getTime());
                date.setText(strDate1);
                getData();
            }
        });

        getData();

        travelAllowance = findViewById(R.id.travel_allowance);
    }

    public void getData(){
        client.get(Backend.serverUrl+"/api/myWork/route/?format=json&scheduledOn="+ strDate, new JsonHttpResponseHandler() { //api/myWork/stop/?format=json
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                stops.clear();
//                for (int i=0; i<response.length(); i++){
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = response.getJSONObject(0);
                        JSONArray jsonArray = jsonObject.getJSONArray("stops");
                        for (int i=0; i<jsonArray.length(); i++) {
                            Stop stop = new Stop(jsonObject, jsonArray,i);
                            routePk = stop.getRoutePk();
                            submit = stop.isSumbit();
                            stops.add(stop);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                }
                if (stops.size() == 0){
                    profileDetailsList.setVisibility(View.GONE);
                    dataNotFound.setVisibility(View.VISIBLE);
                    dataNotFound.setText("Routes are not available.");
                } else {
                    dataNotFound.setVisibility(View.GONE);
                    profileDetailsList.setVisibility(View.VISIBLE);
                    profileDetailsList.setLayoutManager((new LinearLayoutManager(ProfileListActivity.this)));
                    profileDetailsAdapter = new ProfileDetailsAdapter(ProfileListActivity.this, stops);
                    profileDetailsList.setAdapter(profileDetailsAdapter);
                }
            }

            @Override
            public void onFinish() {
                System.out.println("finished 001");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("finished failed 001");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        travelAllowance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileListActivity.this, TravelAllowanceActivity.class)
                        .putExtra("date", strDate1)
                        .putExtra("pk",routePk)
                        .putExtra("submit", submit));
            }
        });
    }


    @Override
    protected void onDestroy() {
        Intent intent = new Intent("com.cioc.libreerp.backendservice");
        intent.putExtra("yourvalue", "torestore");
        sendBroadcast(intent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

}
