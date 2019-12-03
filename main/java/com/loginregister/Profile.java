package com.loginregister;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    ImageView img;
    TextView tv;
    String url_pic_fetch = "", mobile, url_fetch_acc = "http://192.168.0.2/myfolder/fetch_account.php";
    ProgressDialog dialog;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        img = (ImageView) findViewById(R.id.profilepic);
        tv = (TextView) findViewById(R.id.tvDetails);
        extras = getIntent().getExtras();

        if (extras != null) {
            mobile = extras.getString("mobile");
            url_pic_fetch = "http://192.168.0.2/myfolder/profile/" + mobile + ".jpg";
        }

        dialog = ProgressDialog.show(this, "Fetching Details", "Please wait...", true);
        dialog.show();

        ImageRequest imageRequest = new ImageRequest(url_pic_fetch, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.i("tag", url_pic_fetch);
                img.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getmInstance(getApplicationContext()).addToRequestQueue(imageRequest);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_fetch_acc, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("name");
                    String phone = jsonObject.getString("phone");
                    String dob = jsonObject.getString("dob");
                    String email = jsonObject.getString("email");
                    String address = jsonObject.getString("address");
                    String date = jsonObject.getString("datestamp");
                    String device = jsonObject.getString("device");
                    tv.setText(name + " : " + phone + " : " + dob + " : " + email + " : " + address + " : " +
                            device + " : " + date);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error in response !!", Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                return params;
            }
        };
        MySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
