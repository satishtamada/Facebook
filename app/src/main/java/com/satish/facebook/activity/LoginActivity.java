package com.satish.facebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.satish.facebook.R;
import com.satish.facebook.app.AppConfig;
import com.satish.facebook.app.AppController;
import com.satish.facebook.helper.SQLiteHandler;
import com.satish.facebook.helper.SessionManager;
import com.satish.facebook.utils.AccountUtil;
import com.satish.facebook.utils.ParseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLinkRegister, btnLogin;
    private EditText txtInputEmail, txtInputPassword;
    private SessionManager session;
    private ProgressBar progressBar;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user is already logged in or not
        session = new SessionManager(getApplicationContext());
        if (!AccountUtil.verifyGoogleAccounts(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_no_google_account), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (AccountUtil.hasAccount(getApplicationContext())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.login_screen);
        txtInputEmail = (EditText) findViewById(R.id.txt_email);
        txtInputPassword = (EditText) findViewById(R.id.txt_password);
        btnLinkRegister = (Button) findViewById(R.id.btn_link_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btn_login);

        db = new SQLiteHandler(getApplicationContext());


        //user login into facebook
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtInputEmail.getText().toString();
                String password = txtInputPassword.getText().toString();

                if (email.trim().length() > 0 && password.length() > 0) {
                    Log.d(TAG, email + "," + password);
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        //switch to register screen
        btnLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        progressBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");
                    if (error) {


                        // Fetching user details from sqlite

                        HashMap<String, String> userData = db.getUserDetails();
                        //checking user data is not available in sqllite
                        if (userData.size() == 0) {
                            JSONObject user = jObj.getJSONObject("profile");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String uid = user.getString("apikey");
                            String created_at = user.getString("created_at");
                            String id = user.getString("id");
                            String profileImageUrl = user.getString("profile_image");
                            db.addUser(id, name, email, uid, created_at, profileImageUrl);

                            AccountUtil.createAccount(getApplicationContext(), uid);

                            //subscribe to parse with email
                            ParseUtils.subscribeWithEmail(email);
                        }
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        JSONObject errorObj = jObj.getJSONObject("error");
                        String errorMsg = errorObj.getString("message");
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        txtInputPassword.setText("");
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
