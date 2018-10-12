package com.cioc.vamso;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cioc.libreerp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ChangePasswordActivity extends AppCompatActivity {

    AutoCompleteTextView oldPass, newPass, confirmPass;
    TextView changePassTxt;
    Button savePassword;

    Backend backend;
    AsyncHttpClient httpclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();

        oldPass = findViewById(R.id.oldPassword);
        newPass = findViewById(R.id.newPassword);
        confirmPass = findViewById(R.id.confirmPassword);
        changePassTxt = findViewById(R.id.changePasswordTxt);
        changePassTxt.setVisibility(View.GONE);
        savePassword = findViewById(R.id.saveButton);

        backend = new Backend(this);
        httpclient = backend.getHTTPClient();

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = oldPass.getText().toString().trim();
                String newP = newPass.getText().toString().trim();
                String con = confirmPass.getText().toString().trim();

                if (newP.equals(con)){

                    RequestParams params = new RequestParams();
                    params.put("oldPassword",old);
                    params.put("password",newP);

                    httpclient.patch(Backend.serverUrl + "/api/HR/users/"+MainActivity.pk+"/", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            changePassTxt.setVisibility(View.VISIBLE);
                            changePassTxt.setText("Password successfully changed.");

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            changePassTxt.setVisibility(View.VISIBLE);
                            changePassTxt.setText("Sorry!! Change password failed.");
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Confirm password wrong.", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}
