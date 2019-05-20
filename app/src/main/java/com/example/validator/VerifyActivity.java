package com.example.validator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyActivity extends AppCompatActivity
{
    private Button scan,issue;
    public  TextView billref;
    private String data1 =null;
    private  TextView slider_qty;
    private  TextView n_slider_qty;
    private  TextView slider_amount;
    private  TextView n_slider_amount;
    private  TextView amount_total;
    private RequestQueue requestQueue ;
    private  String REQUEST_TAG="VerifyActivity";
    private  final int slider=1350;
    private  final int nslider=300;
    private int UPDATE_TAG=0;

    public void searchBillRef(final String text)
    {
        String url = String.format("https://www.wildwaterskenya.com/android_validate.php"+"?bill=%1$s",text);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject object= new JSONObject(response);
                    String success= object.getString("success");
                    if(success.equals("1"))
                    {
                        UPDATE_TAG=1;
                        issue.setTextColor(Color.WHITE);
                       issue.setEnabled(true);
                       int res_slider= object.getInt("slider");
                       int res_non_slider=object.getInt("nonslider");
                       setValues(text,res_slider,res_non_slider);
                     }
                    else
                    {
                        setValues("0000000",0,0  );
                        MarginLayoutParams lp= (MarginLayoutParams) amount_total.getLayoutParams();
                        lp.setMarginEnd(65);
                        amount_total.setLayoutParams(lp);
                        Toast.makeText(getApplicationContext(),"The Card has been used.",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        })

        ;
        stringRequest.setTag(REQUEST_TAG);
        requestQueue = CustomRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue!=null)
        {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        scan= findViewById(R.id.buttonScan);
        issue=findViewById(R.id.buttonIssue);
        billref=findViewById(R.id.bills);
        slider_qty = findViewById(R.id.slider_qty);
        n_slider_qty=findViewById(R.id.n_slider_qty);
        slider_amount=findViewById(R.id.slider_amount);
        n_slider_amount=findViewById(R.id.n_slider_amount);
        amount_total=findViewById(R.id.amount_total);
        issue.setTextColor(Color.GRAY);


        scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                {
                   statCamera();
                }
                else
                {
                    requestPermission();
                }
            }
        });

        issue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateRecord(data1);
            }
        });
    }

    private void updateRecord(final String bil)
    {
        String url = "https://www.wildwaterskenya.com/android_update.php";
        if (UPDATE_TAG == 1)
        {
            StringRequest updateRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    try {
                        JSONObject object = new JSONObject(response);
                        String success = object.getString("success");
                        if (success.equals("1")) {
                            Toast.makeText(getApplicationContext(), "The billref was validated successfully.", Toast.LENGTH_LONG).show();
                            UPDATE_TAG=0;
                            issue.setEnabled(false);
                            issue.setTextColor(Color.GRAY);
                        }
                        else
                            {
                            Toast.makeText(getApplicationContext(), "An error was encountered during validation.", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(getApplicationContext(), "An error was encountered during validation.", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String,String>params = new HashMap<>();
                    params.put("bill", bil);
                    return params;
                }
            };
            updateRequest.setTag(REQUEST_TAG);
            requestQueue= CustomRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
            requestQueue.add(updateRequest);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Requested operation could not be completed.", Toast.LENGTH_LONG).show();
        }
    }
    private void statCamera()
    {
        Intent intent1 = new Intent(VerifyActivity.this, ScanCodeActivity.class);
        startActivityForResult(intent1, 2);
    }

    private void requestPermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))
        {

            ActivityCompat.requestPermissions(VerifyActivity.this, new String[]{Manifest.permission.CAMERA},1);

        }
        else
        {
            Toast.makeText(getApplicationContext(),"The application require camera access.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1&&(grantResults.length>0))
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                statCamera();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"The application require camera access.",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==2)
        {
            data1 = data.getStringExtra("ref");
            searchBillRef(data.getStringExtra("ref"));
        }
        else
        {
            setValues("0000000",0,0  );
            Toast.makeText(getApplicationContext(),"Nothing was captured.",Toast.LENGTH_LONG).show();
        }
    }

    private void setValues(String text, int sliders, int nonslider)
    {

        billref.setText(text);
        slider_qty.setText(String.valueOf(sliders));
        n_slider_qty.setText(String.valueOf(nonslider));
        int s_amount=slider*sliders;
        int ns_amount=nslider*nonslider;
        slider_amount.setText(String.valueOf(s_amount));
        n_slider_amount.setText(String.valueOf(ns_amount));
        amount_total.setText(String.valueOf(s_amount+ns_amount));

        if(!amount_total.getText().equals("0"))
        {
            MarginLayoutParams lp= (MarginLayoutParams) amount_total.getLayoutParams();
            lp.setMarginEnd(35);
            amount_total.setLayoutParams(lp);
        }
    }
}
