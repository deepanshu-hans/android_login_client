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

public class OTPVerify extends AppCompatActivity {

    EditText etOTP;
    Button bVerify;
    String otp, mobile, url_verify = "http://192.168.0.2/myfolder/verify_otp.php";
    Bundle extras;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpverify);

        etOTP = (EditText) findViewById(R.id.editTextOtp);
        bVerify = (Button) findViewById(R.id.buttonConfirm);
        extras = getIntent().getExtras();

        if (extras != null) {
            otp = extras.getString("otp");
            mobile = extras.getString("mobile");
        }

        bVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(OTPVerify.this, "", "Please wait...", true);
                dialog.show();
                String verify = etOTP.getText().toString();
                if (verify.equalsIgnoreCase("") || !(verify.length() == 6)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid items !!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    if (otp.equalsIgnoreCase(verify)) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_verify, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    if (code.equalsIgnoreCase("existing")) {
                                        Intent profile = new Intent(OTPVerify.this, Profile.class);
                                        profile.putExtra("mobile", mobile);
                                        dialog.dismiss();
                                        startActivity(profile);
                                        endActivity();
                                    } else if (code.equalsIgnoreCase("new")) {
                                        Intent register = new Intent(OTPVerify.this, Register.class);
                                        register.putExtra("mobile", mobile);
                                        dialog.dismiss();
                                        startActivity(register);
                                        endActivity();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Response Error!!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "No response !!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("otpverify", otp);
                                params.put("mobile", mobile);
                                return params;
                            }
                        };
                        MySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    } else {
                        Toast.makeText(getApplicationContext(), "OTP doesn't match", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void endActivity() {
        this.finish();
        dialog.dismiss();
    }
}