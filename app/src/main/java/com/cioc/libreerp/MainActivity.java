package com.cioc.libreerp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;


public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button loginButton;
//    private String serverURL1;
    private Button changeBtn;
//    private HomeActivity homeAct;


//    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();

    SharedPreferences sharedPreferences;

    private File keyFile;
    private File settingsFile;
    public static String settingsFileName = ".libreerp.settings";
    public static String keyFileName = ".libreerp.key";

    Context context;
    private CookieStore httpCookieStore;
    private AsyncHttpClient client;
    static EditText erpPathInput;
//    static String keyText = "";

    private static String serverURL = "http://10.0.2.2:8080";//http://pradeepyadav.net

    protected void login(final String username , final String password){
//        if (chkKeys.isChecked()){
//            String key11 = editTextKey11.getText().toString();
//            String key12 = editTextKey12.getText().toString();
//            String key13 = editTextKey13.getText().toString();
//            String key14 = editTextKey14.getText().toString();
//            String key21 = editTextKey21.getText().toString();
//            String key22 = editTextKey22.getText().toString();
//            String key23 = editTextKey23.getText().toString();
//            String key24 = editTextKey24.getText().toString();
//
//            if (key11.equals(key21) && key12.equals(key22) && key13.equals(key23) && key14.equals(key24)){
//                keyText = String.format("%s%s%s%s", key11,key12, key13,key14);
//            }else{
//                String msg;
//                if (key11.isEmpty()){
//                    msg = "Please provide a login key!";
//                }else{
//                    msg = "Keys not matching!";
//                }
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }else {
//            keyText = "";
//        }

        client.get(serverURL+ "/login", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                System.out.println(statusCode);
                RequestParams params = new RequestParams();
                try{
                    String res = new String(response, "UTF-8");
//                    System.out.println(res);
                }catch (UnsupportedEncodingException e){

                }
                try {
                    int ind = -1;
                    for (int i = 0; i < headers.length; i++){
                        if(headers[i].getName().equals("Set-Cookie")){
                            ind= i;
                            break;
                        }
                    }
                    if(ind == -1){
                        return;
                    };

                    Header hdr = headers[ind];
                    String headerStr = hdr.getValue();

                    Pattern pattern = Pattern.compile("csrftoken=(.*?);");
                    Matcher matcher = pattern.matcher(headerStr);
                    String csrftoken = "";
                    while (matcher.find()) {
                        csrftoken = matcher.group(1);
                        Log.e("csrfmiddlewaretoken",""+csrftoken);
                    }
                    params.put("csrfmiddlewaretoken", csrftoken);
                }catch (ArrayIndexOutOfBoundsException e){

                }

                params.put("username", username);
                params.put("password", password);

                client.post(serverURL+ "/login", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // called when response HTTP status is "200 OK"
                        System.out.println("on failure");
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        System.out.println("on failure");
                        System.out.println(statusCode);
                        String msg;
                        if(statusCode == 401){
                            msg ="Wrong username or password";
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        else if(statusCode != 302){
                            msg = String.format("Error while logining : %s", statusCode);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onFinish() {
                        // saving the new cookies to the SD card , in future release this
                        // should be encrypted and then saved to key storage system of the OS
                        List<Cookie> lst = httpCookieStore.getCookies();
                        if(lst.isEmpty()){
                            Toast.makeText(MainActivity.this, String.format("Error , Empty cookie store"), Toast.LENGTH_SHORT).show();
                        }else{

                            if (lst.size() <2){
                                String msg = String.format("Error while logining, fetal error!");
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Cookie csrfCookie = lst.get(0);
                            Cookie sessionCookie = lst.get(1);

                            String csrf = csrfCookie.getValue();
                            String session = sessionCookie.getValue();
                            String keys = String.format("%s\n%s",csrf , session) ;

                            // writing the new keys
                            try {
                                FileOutputStream stream = new FileOutputStream(keyFile);
                                stream.write(keys.getBytes());
                                stream.close();
                            }catch (IOException e){

                            }
//                            getUser();
                        }
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println("failure");
                System.out.println(statusCode);

            }
        });
    }

//    protected void getUser(){
//        client.get(serverURL + "/api/HR/users/?mode=mySelf", new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                try {
//                    JSONObject usrObj = response.getJSONObject(0);
//                    String username = usrObj.getString("username");
//                    String firstName = usrObj.getString("first_name");
//                    Integer pk = usrObj.getInt("pk");
//                    String lastName = usrObj.getString("last_name");
//                    Integer settingsPk = usrObj.getInt("settings");
//                    JSONObject profileObj = usrObj.getJSONObject("profile");
//                    String DPLink = profileObj.getString("displayPicture");
//
//                    client.get(serverURL + "/api/PIM/settings/" + settingsPk.toString() + "/", new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            try {
//                                String mainThemeColor = response.getJSONObject("theme").getString("main");
//
//                            }catch (JSONException e){
//                                throw  new RuntimeException(e);
//                            }
//                        }
//                        @Override
//                        public void onFinish() {
//                            System.out.println("finished 001");
//
//                        }
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
//                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                            System.out.println("finished failed 001");
//                        }
//                    });
//
//
//                    user = new User(username ,pk);
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);
//
//
//                    client.get(DPLink, new FileAsyncHttpResponseHandler(context) {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, File file) {
//                            // Do something with the file `response`
//                            writeConfigFile(context);
//                            Bitmap pp = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            user.setProfilePicture(pp);
//                            user.saveUserToFile(context);
//                            Intent intent = new Intent(context, HomeActivity.class);
//                            startActivity(intent);
//                        }
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers,Throwable e, File file) {
//                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                            System.out.println("failure");
//                            System.out.println(statusCode);
//                        }
//                    });
//
//                    System.out.println(username);
//                }catch (JSONException e){
//                    throw  new RuntimeException(e);
//                }
//            }
//            @Override
//            public void onFinish() {
//                System.out.println("finished 001");
//
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                System.out.println("finished failed 001");
//            }
//        });
//    }

//    private void presentServerSettingsDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//        builder.setTitle("ERP Server");
//        erpPathInput = new EditText(MainActivity.this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        erpPathInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
//        erpPathInput.setText(serverURL);
//        builder.setView(erpPathInput);
//
//        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                serverURL = erpPathInput.getText().toString();
//                System.out.println(serverURL);
//            }
//        });
//        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
////        builder.setIcon(R.drawable.ic_action_gear);
//
//        builder.show();
//    }

    static public AsyncHttpClient getHTTPClient(Context context){
        // reading the existing keys
        File path = context.getFilesDir();
        File keyFile = new File(path, keyFileName);

        int length = (int) keyFile.length();

        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(keyFile);
            in.read(bytes);
            in.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }

        String contents = new String(bytes);
        String[] keysArr = contents.split("\n");
        final String csrftoken = keysArr[0];
        final String sessionid = keysArr[1];

        CookieStore httpCookieStoreSt = new PersistentCookieStore(context.getApplicationContext());
        httpCookieStoreSt.clear();
        AsyncHttpClient clientSt = new AsyncHttpClient();

        String slimedUrl = serverURL.replace("http://", "").replace("https://", "");

        BasicClientCookie newCsrftokenCookie = new BasicClientCookie("csrftoken", csrftoken);
        newCsrftokenCookie.setVersion(1);
        newCsrftokenCookie.setDomain(slimedUrl);
        newCsrftokenCookie.setPath("/");
        httpCookieStoreSt.addCookie(newCsrftokenCookie);
        BasicClientCookie newSessionidtokenCookie = new BasicClientCookie("sessionid", sessionid);
        newSessionidtokenCookie.setVersion(1);
        newSessionidtokenCookie.setDomain(slimedUrl);
        newSessionidtokenCookie.setPath("/");
        httpCookieStoreSt.addCookie(newSessionidtokenCookie);
        clientSt.addHeader("X-CSRFToken" , csrftoken);
        clientSt.setCookieStore(httpCookieStoreSt);

        return clientSt;
    };

    protected void writeConfigFile(Context context){

        JSONObject settJson = new JSONObject();
        try{
            settJson.put("domain" , serverURL );
//            settJson.put("keyText" , keyText );
        }catch (JSONException e){
        };

        try {
            FileOutputStream stream = new FileOutputStream(settingsFile);
            stream.write(settJson.toString().getBytes());
            stream.close();
        }catch (IOException e){
        }

    };

    public static JSONObject getSettingsJson(Context context){
        File path = context.getFilesDir();
        File settingsFile = new File(path, settingsFileName);

        int length = (int) settingsFile.length();
        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(settingsFile);
            in.read(bytes);
            in.close();
        }catch (IOException e){

        }
        String contents = new String(bytes);
        try{
            JSONObject settJson = new JSONObject(contents);
            return settJson;
        }catch (JSONException e){
            return new JSONObject();
        }
    }

    protected void loadConfigFile(Context context){
        int length = (int) settingsFile.length();
        byte[] bytes = new byte[length];

        try {
            FileInputStream in = new FileInputStream(settingsFile);
            in.read(bytes);
            in.close();
        }catch (FileNotFoundException e){
            writeConfigFile(context);
            loadConfigFile(context);
            return;
        }catch (IOException e){

        }
        String contents = new String(bytes);

        try{
            JSONObject settJson = new JSONObject(contents);
            serverURL = settJson.getString("domain");
//            keyText = settJson.getString("keyText");
        }catch (JSONException e){
            Toast.makeText(MainActivity.this, "Settings not found!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this.getApplicationContext();

        httpCookieStore = new PersistentCookieStore(context);
        httpCookieStore.clear();
        client = new AsyncHttpClient();
        client.setCookieStore(httpCookieStore);

        File path = context.getFilesDir();
        keyFile = new File(path, keyFileName);
        settingsFile = new File(path, settingsFileName); // a json file with the key value pair settings


        loadConfigFile(context);
//        if (serverURL.length()<1){
//            Toast.makeText(MainActivity.this, "No server details found!", Toast.LENGTH_SHORT).show();
////            presentServerSettingsDialog();
//        }

        // check if the file exist
//        if (keyFile.exists()){
//            if (!keyText.isEmpty()){
//                // ask for the key
//                Intent intent = new Intent(context, KeyLoginActivity.class);
//                startActivity(intent);
//            }else {
//                Intent intent = new Intent(context, HomeActivity.class);
//                startActivity(intent);
//            }
//        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.sign_in_button);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        login(username.getText().toString().trim(),password.getText().toString().trim());
            }
        });
        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);
// /api/v1/notifications/?csrf_token=4Iku9T1qI5rp4hXvuw3JjnQqt&session_id=LN8nyo986bo4ohtlPhtqvkWqU

        if (session_id != null && csrf_token != null){
            client.get(backend.BASE_URL + "/api/v1/notifications/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                    super.onSuccess(statusCode, headers, c);

                    try {
                        String firstName = c.getString("firstName");
                        String  middleName= c.getString("middleName");
                        String  lastName= c.getString("lastName");
                        Integer  id= c.getInt("id");

                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }catch (JSONException e) {

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e,  JSONObject c) {
                    super.onFailure(statusCode, headers, e, c);


                }

            });
        }
    }

    public void loginPage(){
        Toast.makeText(this, backend.BASE_URL, Toast.LENGTH_LONG).show();
        String userName = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        if (userName.isEmpty()){
            username.setError("Empty");
            username.requestFocus();
        } else {
            if (pass.isEmpty()){
                password.setError("Empty");
                password.requestFocus();
                } else {
                RequestParams params = new RequestParams();
                params.put("usr_email", userName);
                params.put("usr_password", pass);

                client.post(backend.BASE_URL + "/api/v1/login/", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        directToAccount(c);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e,  JSONObject c) {
                        super.onFailure(statusCode, headers, e, c);

                        if(statusCode == 401){
                            directToAccount(c);
                        }

                    }

                });
            }
        }
    }

    public void directToAccount(JSONObject c){
        try {
            String session_id = c.getString("session_id");
            String csrf_token = c.getString("csrf_token");


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("session_id", session_id);
            editor.putString("csrf_token", csrf_token);
            editor.commit();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

        }catch(JSONException e){

        }
    }
}
