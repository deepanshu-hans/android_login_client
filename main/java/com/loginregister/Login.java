package com.loginregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button bLog;
    EditText etLogin;
    String url_req = "http://192.168.0.2/myfolder/request_otp.php";
    String otp = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        bLog = (Button) findViewById(R.id.bLogin);
        etLogin = (EditText) findViewById(R.id.etLogin);

        bLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(Login.this, "", "Please wait...", true);
                dialog.show();
                final String mobile = etLogin.getText().toString();
                if (mobile.length() == 10) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url_req, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String message = jsonObject.getString("message");
                                String code = jsonObject.getString("code");
                                if (code.equalsIgnoreCase("success")) {
                                    otp = jsonObject.getString("otp");
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else if (code.equalsIgnoreCase("failed")) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                                Intent ver = new Intent(Login.this, OTPVerify.class);
                                ver.putExtra("otp", otp);
                                ver.putExtra("mobile", mobile);
                                dialog.dismiss();
                                startActivity(ver);
                                endActivity();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Response Error !!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "No response !!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("mobile", mobile);
                            return params;
                        }
                    };
                    MySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter correct number", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void endActivity() {
        this.finish();
        dialog.dismiss();
    }
}
