package com.smith.ireland8.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    private WebView wv;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wv = new WebView(this);

        setContentView(wv);

        //hide action bar
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");

        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(url);

    }
}
