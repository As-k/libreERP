package com.cioc.vamso;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cioc.libreerp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TravelAllowanceActivity extends AppCompatActivity {

    LinearLayout travelAllowanceForm, filledForm;
    Button save, submit;
    Spinner transportDropdown;
    TextView routeDate, totalKMTxt, transportTxt, allowanceTxt, lodgingTxt, foodTxt, statusTxt;
    EditText totalKMEdit, allowanceEdit, lodgingEdit, foodEdit;
    CheckBox lodgingBill, foodBill;

    String routePk, totalKM, transport, travelAllowance, lodging, food;
    boolean res, isSave;

    Backend backend;
    AsyncHttpClient httpclient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_allowance);

        getSupportActionBar().hide();

        String date = getIntent().getExtras().getString("date");
        routePk = getIntent().getExtras().getString("pk");
        res = getIntent().getExtras().getBoolean("submit");

        backend = new Backend(this);
        httpclient = backend.getHTTPClient();

        save = findViewById(R.id.save_button);
        submit = findViewById(R.id.submit_button);
        submit.setVisibility(View.GONE);

        routeDate = findViewById(R.id.route_date_tv);
        routeDate.setText(date);

        totalKMTxt = findViewById(R.id.total_km_tv);
        transportTxt = findViewById(R.id.transport_tv);
        allowanceTxt = findViewById(R.id.travel_allowance_tv);
        lodgingTxt = findViewById(R.id.lodging_tv);
        foodTxt = findViewById(R.id.food_tv);
        statusTxt = findViewById(R.id.status_tv);

        transportDropdown = findViewById(R.id.transport_spinner);
        totalKMEdit = findViewById(R.id.total_km_edit);
        allowanceEdit = findViewById(R.id.ta_allowance_edit);
        lodgingEdit = findViewById(R.id.lodging_edit);
        foodEdit = findViewById(R.id.foodCostEdit);

        lodgingBill = findViewById(R.id.lodging_bills);
        foodBill = findViewById(R.id.food_bills);

        travelAllowanceForm = findViewById(R.id.travel_allowance_form);
        filledForm = findViewById(R.id.llFilledForm);
        filledForm.setVisibility(View.GONE);

        final String[] items = new String[]{"bike", "bus", "car" , "flight"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        transportDropdown.setAdapter(adapter);

        if (res){
            travelAllowanceForm.setVisibility(View.GONE);
            filledForm.setVisibility(View.VISIBLE);
            httpclient.get(Backend.serverUrl + "/api/myWork/route/" + routePk +"/?format=json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                    super.onSuccess(statusCode, headers, object);
                    try {
//                        JSONObject object = response.getJSONObject("");
                        totalKMTxt.setText(object.getString("totalKM"));
                        transportTxt.setText(object.getString("modeOfTransport"));
                        allowanceTxt.setText(object.getString("travelCost"));
                        lodgingTxt.setText(object.getString("lodgingCost"));
                        foodTxt.setText(object.getString("foodCost"));
                        statusTxt.setText(object.getString("status"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            httpclient.get(Backend.serverUrl + "/api/myWork/route/" + routePk +"/?format=json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                    super.onSuccess(statusCode, headers, object);
                    try {
//                        JSONObject object = response.getJSONObject("");
                        totalKMEdit.setText(object.getString("totalKM"));
                        String transport = object.getString("modeOfTransport");
                        for (int i=0; i<items.length; i++){
                            if (items[i].equals(transport)){
                                transportDropdown.setSelection(i);
                            }
                        }
//                        transportTxt.setText(object.getString("modeOfTransport"));
                        allowanceEdit.setText(object.getString("travelCost"));
                        lodgingEdit.setText(object.getString("lodgingCost"));
                        foodEdit.setText(object.getString("foodCost"));
                        lodgingBill.setChecked(object.getBoolean("lodgingCostWithBill"));
                        foodBill.setChecked(object.getBoolean("foodCostWithBill"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });

        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isSave = true;
                totalKM = totalKMEdit.getText().toString().trim();
                travelAllowance = allowanceEdit.getText().toString().trim();
                lodging = lodgingEdit.getText().toString().trim();
                food = foodEdit.getText().toString().trim();

                transport = transportDropdown.getSelectedItem().toString();

                boolean lodgingWithBill = lodgingBill.isChecked();
                boolean foodWithBill = foodBill.isChecked();

                RequestParams params = new RequestParams();
                params.put("totalKM", totalKM);
                params.put("modeOfTransport", transport);
                params.put("travelCost", travelAllowance);
                params.put("lodgingCost", lodging);
                params.put("foodCost", food);
                params.put("status","created");
                params.put("lodgingCostWithBill", lodgingWithBill);
                params.put("foodCostWithBill", foodWithBill);

                httpclient.patch(Backend.serverUrl + "/api/myWork/route/" + routePk + "/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.e("TravelAllowance","onSuccess");
                        submitAll();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("TravelAllowance","onFailure");
                    }
                });
            }
        });

    }

    public void submitAll(){
        save.setVisibility(View.GONE);
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(TravelAllowanceActivity.this);
                adb.setTitle("Confirm").setMessage("Are you sure you want to submit ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RequestParams params = new RequestParams();
                                params.put("submit", !res);
                                params.put("status", "submitted");

                                httpclient.patch(Backend.serverUrl + "/api/myWork/route/" + routePk+"/", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        travelAllowanceForm.setVisibility(View.GONE);
                                        filledForm.setVisibility(View.VISIBLE);

                                        totalKMTxt.setText(totalKM);
                                        transportTxt.setText(transport);
                                        allowanceTxt.setText(travelAllowance);
                                        lodgingTxt.setText(lodging);
                                        foodTxt.setText(food);
                                        statusTxt.setText("submitted");
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                    }
                                });
                            }
                        }).setNegativeButton("No", null).create().show();
            }
        });
    }


}
