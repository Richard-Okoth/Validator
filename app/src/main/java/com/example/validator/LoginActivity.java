package com.example.validator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity
{

    static Button login;
    private EditText username,password;
    public static final String REQUEST_TAG = "LoginActivity";
    public RequestQueue mQueue;
    private ProgressBar loading;
    private static Handler handler;
    private static final int HANDLER_DATA=1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        preferences=getSharedPreferences("user_data",MODE_PRIVATE);



        handler= new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what==HANDLER_DATA)
                {
                    username.requestFocus();
                    clear();
                }
            }
        };
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        loading=findViewById(R.id.loading);

        if(preferences.contains("username")&&preferences.contains("password"));
        {
            startActivity(new Intent(this,VerifyActivity.class));
        }
        login=findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Check network connectivity.
                if(NetworkChecker.isNetAvailable(getApplicationContext()))
                {
                    //call validate method to validate user input.
                    validate();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Check your network connection.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

   private void validate()
    {
        //checking if both username and password are filled
        if(username.getText().toString().equals("")&&password.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter your username and password",Toast.LENGTH_SHORT).show();
        }
        //checking if password field is empty.
        else if(password.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter your password",Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        //checking if username field is empty.
        else if(username.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter your username",Toast.LENGTH_SHORT).show();
            username.requestFocus();
        }
        else
        {
            //log in method authenticates user inputs using Volley library.
            loading.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            login(username.getText().toString(),password.getText().toString());
        }
    }

    private void login(final String username, final String password)
    {
        String url= "https://www.wildwaterskenya.com/android_auth.php";
        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject object = new JSONObject(response);
                    String success = object.getString("success");
                    //JSONArray jsonArray = object.getJSONArray("login");


                    if(success.equals("1"))
                    {
                        //keep the login details in the shared preference file for session management
                        editor=preferences.edit();
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(),VerifyActivity.class));
                        handler.sendEmptyMessageDelayed(HANDLER_DATA,3000);
                        //clear();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error loging in.",Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e)
                {
                    loading.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Error "+e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loading.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Error "+error,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String,String> params= new HashMap<>();
                params.put("user_name",username);
                params.put("pass_word",password);
                return params;
            }
        };
        stringRequest.setTag(REQUEST_TAG);
        mQueue= CustomRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        mQueue.add(stringRequest);
    }

    private void clear()
    {
        username.setText("");
        password.setText("");
    }

    @Override
    protected void onStop()
    {
        loading.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);
        super.onStop();
        if(mQueue!=null)
        {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }
}