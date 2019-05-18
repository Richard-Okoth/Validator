package com.example.validator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifyActivity extends AppCompatActivity
{
    private Button scan,issue;
    public  TextView billref;
    private  TextView slider_qty;
    private  TextView n_slider_qty;
    private  TextView slider_amount;
    private  TextView n_slider_amount;
    private  TextView amount_total;
    private RequestQueue requestQueue ;
    private  String REQUEST_TAG="VerifyActivity";
    private  final int slider=1350;
    private  final int nslider=300;

    public void searchBillRef(final String text)
    {
        String url = "https://www.wildwaterskenya.com/android_validate.php";
        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
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
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String>params = new HashMap<>();
                params.put("bill",text);
                return params;
            }
        };
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

        scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(VerifyActivity.this,ScanCodeActivity.class);
                startActivityForResult(intent1,2);
            }
        });

        issue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2)
        {
            searchBillRef(data.getStringExtra("ref"));
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
            lp.setMarginEnd(40);
            amount_total.setLayoutParams(lp);
        }
    }
}
