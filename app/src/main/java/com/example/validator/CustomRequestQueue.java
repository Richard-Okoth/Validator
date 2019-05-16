package com.example.validator;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class CustomRequestQueue
{
    private static CustomRequestQueue mInstance;
    private static Context mContext;
    private RequestQueue requestQueue;

    private CustomRequestQueue (Context context)
    {
        mContext=context;
        requestQueue=getRequestQueue();
    }

    public static synchronized CustomRequestQueue getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance = new CustomRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {

        if(requestQueue==null)
        {
            Cache cache= new DiskBasedCache(mContext.getCacheDir(),5*1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache,network);
            requestQueue.start();
        }
        return requestQueue;
    }
}
