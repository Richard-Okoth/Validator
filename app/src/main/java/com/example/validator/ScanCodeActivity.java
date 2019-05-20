package com.example.validator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        scannerView= new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result)
    {
        if(result!=null)
        {
            Intent intent = new Intent();
            intent.putExtra("ref", result.toString());
            setResult(2, intent);
            finish();
        }
        else
        {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed()
    {
       // super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("ref","0");
        setResult(1,intent);
        finish();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
