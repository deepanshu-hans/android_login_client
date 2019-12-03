package com.loginregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etDOB, etPassword, etDevice, etFullAdd;
    Button bRegister, bUpload;
    private String url = "http://192.168.0.2/myfolder/register.php";
    private Bitmap bitmap;
    ImageView imageView;
    String mobileNo;
    Bundle extras;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etDevice = (EditText) findViewById(R.id.etDevice);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etDOB = (EditText) findViewById(R.id.etdob);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFullAdd = (EditText) findViewById(R.id.etAddress);
        etEmail = (EditText) findViewById(R.id.etEmail);
        bRegister = (Button) findViewById(R.id.bReg);
        imageView = (ImageView) findViewById(R.id.imageView);
        bUpload = (Button) findViewById(R.id.bUpload);
        extras = getIntent().getExtras();

        if (extras != null) {
            mobileNo = extras.getString("mobile");
            etPhone.setText(mobileNo);
        }

        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(Register.this, "", "Please wait...", true);
                dialog.show();
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String phone = etPhone.getText().toString();
                final String dob = etDOB.getText().toString();
                final String device = etDevice.getText().toString();
                final String fulladd = etFullAdd.getText().toString();
                final String password = etPassword.getText().toString();

                if (name.equalsIgnoreCase("") || password.equalsIgnoreCase("") || dob.equalsIgnoreCase("") || email.equalsIgnoreCase("") || phone.equalsIgnoreCase("") || device.equalsIgnoreCase("") || fulladd.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please enter all details !!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    if (phone.length() == 10 && email.contains(".com") && email.contains("@")) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String code = jsonObject.getString("code");
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    if (code.equalsIgnoreCase("reg_success")) {
                                        Intent login = new Intent(Register.this, Login.class);
                                        dialog.dismiss();
                                        startActivity(login);
                                        endActivity();
                                    } else if (code.equalsIgnoreCase("reg_failed")) {
                                        dialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Error in response !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No response !!", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                String image = getStringImage(bitmap);
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("name", name);
                                params.put("profilepic", image);
                                params.put("email", email);
                                params.put("phone", phone);
                                params.put("dob", dob);
                                params.put("device", device);
                                params.put("address", fulladd);
                                params.put("password", password);
                                return params;
                            }
                        };
                        MySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Please enter correct details !!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            imageView.buildDrawingCache(true);
            bmp = imageView.getDrawingCache(true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void endActivity() {
        this.finish();
    }
}
