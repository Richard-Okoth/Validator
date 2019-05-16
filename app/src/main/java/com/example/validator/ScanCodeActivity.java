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
        startActivity(new Intent(getApplicationContext(),VerifyActivity.class));
        onBackPressed();
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