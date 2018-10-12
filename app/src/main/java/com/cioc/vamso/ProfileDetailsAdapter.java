package com.cioc.vamso;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.libreerp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static android.text.InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;

/**
 * Created by admin on 10/05/18.
 */

public class ProfileDetailsAdapter extends RecyclerView.Adapter<ProfileDetailsAdapter.MyHolder> {

    public static int loc[] = {R.drawable.ic_person_pin_circle,R.drawable.ic_person_pin_circle,R.drawable.ic_person_pin_circle,R.drawable.ic_person_pin_circle,
            R.drawable.ic_person_pin_circle,R.drawable.ic_person_pin_circle};
    String call[] = {"8746331302", "7746334512", "7846334578", "8946331312", "7854331378", "7416331336", "8746331389", "7845337845"};
    String names[] = {"Samuel D. Pollock ","Sanket", "Prasanjit Nadi","Praddep Yadav","Rita Stith", "Ronald Allen","Rita Stith", "Prasanjit Nadi"};
    String times[] = {"11:00 AM", "12:00 AM", "04:00 PM", "09:00 PM", "10:00 AM", "01:00 PM", "03:00 PM", "09:00 AM"};

    List<Stop> stops;
    Context context;
    Backend backend;
    AsyncHttpClient httpClient;

    public ProfileDetailsAdapter(Context context, List<Stop> stops){
        this.context = context;
        this.stops = stops;
    }

    @NonNull
    @Override
    public ProfileDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        backend = new Backend(context);
        httpClient = backend.getHTTPClient();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.profile_details_layout, parent, false);
        MyHolder myHolder = new MyHolder(v);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileDetailsAdapter.MyHolder holder, int position) {

        Stop stop = stops.get(position);

        holder.profileName.setText(stop.getName());

        String time = stop.getTiming();
        holder.timing.setText(time+":00 AM");
//
//        holder.profileName.setText(names[position]);
//        holder.timing.setText(times[position]);


        holder.personLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = stop.getStreet()+", "+stop.getCity()+", "+stop.getState()+", "+stop.getPincode()+", "+stop.getCountry();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+loc);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        holder.callPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + stop.getMobile()));
                context.startActivity(i);
            }
        });

        if (stop.getFeedback() != "null"){
            holder.profileCardView.setCardBackgroundColor(Color.rgb(0, 200, 0));
            holder.profileName.setTextColor(Color.WHITE);
            holder.timing.setTextColor(Color.WHITE);
            holder.timingImg.setImageResource(R.drawable.ic_access_time_white);
            holder.personLocation.setImageResource(R.drawable.ic_person_pin_circle_white);
            holder.callPerson.setImageResource(R.drawable.ic_phone_white);
            holder.popupMenu.setImageResource(R.drawable.ic_popup_menu_white);
        }

        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
//                if (stop.getFeedback() == "null") {
                    popupMenu.inflate(R.menu.mark_list);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.complete1:
                                    EditText editText = new EditText(context);
                                    if (stop.getFeedback() == "null")
                                        editText.setText("");
                                    else
                                        editText.setText(stop.getFeedback());
                                    editText.setHeight(250);
                                    editText.hasWindowFocus();
                                    editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS | TYPE_TEXT_FLAG_MULTI_LINE);
                                    editText.setGravity(Gravity.TOP);

                                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                                    adb.setTitle("Write a comment!").setView(editText)
                                            .setCancelable(false)
                                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    RequestParams params = new RequestParams();
                                                    params.put("feedbackTxt", editText.getText().toString().trim());
                                                    params.put("completed", true);
//                                                    params.put("status", "submitted");

                                                    httpClient.patch(Backend.serverUrl + "/api/myWork/stop/" + stop.getStopPk()+"/", params, new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            holder.res = true;
                                                            holder.comment = editText.getText().toString().trim();
                                                            stop.setFeedback(holder.comment);
                                                            holder.profileCardView.setCardBackgroundColor(Color.rgb(0, 200,0 ));
                                                            holder.profileName.setTextColor(Color.WHITE);
                                                            holder.timing.setTextColor(Color.WHITE);
                                                            holder.timingImg.setImageResource(R.drawable.ic_access_time_white);
                                                            holder.personLocation.setImageResource(R.drawable.ic_person_pin_circle_white);
                                                            holder.callPerson.setImageResource(R.drawable.ic_phone_white);
                                                            holder.popupMenu.setImageResource(R.drawable.ic_popup_menu_white);

                                                            Toast.makeText(context, "complete", Toast.LENGTH_SHORT).show();
                                                            Log.e("ProfileDetailsAdapter", "  onFailure");
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            Log.e("ProfileDetailsAdapter", "  onFailure");
                                                        }
                                                    });

                                                }
                                            }).setNegativeButton("Cancel", null)
                                            .create().show();
                                    break;
                            }
                            return false;
                        }
                    });
//                    popupMenu.show();
//                } else {
//                    popupMenu.inflate(R.menu.mark_list2);
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.complete2:
//                                    EditText editText = new EditText(context);
//                                    editText.setText(stop.getFeedback());
//                                    editText.setHeight(250);
//                                    editText.hasWindowFocus();
//                                    editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS | TYPE_TEXT_FLAG_MULTI_LINE);
//                                    editText.setGravity(Gravity.TOP);
//
//                                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
//                                    adb.setTitle("Write a comment!").setView(editText)
//                                            .setCancelable(false)
//                                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    RequestParams params = new RequestParams();
//                                                    params.put("feedbackTxt", editText.getText().toString().trim());
////                                                    params.put("status", "submitted");
//
//                                                    httpClient.patch(Backend.serverUrl + "/api/myWork/stop/" + stop.getStopPk()+"/", params, new AsyncHttpResponseHandler() {
//                                                        @Override
//                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
////                                                            holder.res = true;
//                                                            holder.comment = editText.getText().toString().trim();
////                                                            stop.setFeedback(holder.comment);
//                                                            holder.profileCardView.setCardBackgroundColor(Color.rgb(0, 0, 100));
//                                                            holder.profileName.setTextColor(Color.WHITE);
//                                                            holder.timing.setTextColor(Color.WHITE);
//                                                            holder.timingImg.setImageResource(R.drawable.ic_access_time_white);
//                                                            holder.personLocation.setImageResource(R.drawable.ic_person_pin_circle_white);
//                                                            holder.callPerson.setImageResource(R.drawable.ic_phone_white);
//                                                            holder.popupMenu.setImageResource(R.drawable.ic_popup_menu_white);
//
//                                                            Toast.makeText(context, "complete", Toast.LENGTH_SHORT).show();
//                                                            Log.e("ProfileDetailsAdapter", "  onFailure");
//                                                        }
//
//                                                        @Override
//                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                                                            Log.e("ProfileDetailsAdapter", "  onFailure");
//                                                        }
//                                                    });
//                                                }
//                                            }).setNegativeButton("Cancel", null)
//                                            .create().show();
//
//                                    break;
//
//                            }
//                            return false;
//                        }
//                    });
//
//                }
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        boolean res = false;
        String comment = "";
        TextView profileName, timing;
        ImageView timingImg, personLocation, callPerson, popupMenu;
        CardView profileCardView;

        public MyHolder(View itemView) {
            super(itemView);
            profileCardView = itemView.findViewById(R.id.profilesDetails_cardView);
            profileName =  itemView.findViewById(R.id.profile_name);
            timing =  itemView.findViewById(R.id.timing);
            timingImg =  itemView.findViewById(R.id.time_img);
            personLocation =  itemView.findViewById(R.id.profile_location);
            callPerson =  itemView.findViewById(R.id.person_call);
            popupMenu =  itemView.findViewById(R.id.popup_menu);
        }
    }



}
