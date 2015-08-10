/**
 * ALIQRCodeActivity.java
 *
 * Created by xuanzhui on 2015/8/6.
 * Copyright (c) 2015 BeeCloud. All rights reserved.
 */
package cn.beecloud.sdk_demo;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 用于展示Ali内嵌二维码
 */
public class ALIQRCodeActivity extends Activity {
    private WebView aliQRCode;
    private String aliQRURL;
    private String aliQRHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliqrcode);


        aliQRCode = (WebView) findViewById(R.id.aliQRCode);
        WebSettings webSettings = aliQRCode.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        //设置默认加载的可视范围是大视野范围
        webSettings.setLoadWithOverviewMode(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);


        aliQRCode.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Here put your code
                //Log.w("My Webview", url);

                // return true; //Indicates WebView to NOT load the url;
                return false; //Allow WebView to load url
            }

            //必须重写
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }
        });

        aliQRHtml = getIntent().getStringExtra("aliQRHtml");
        aliQRURL = getIntent().getStringExtra("aliQRURL");

        //可以
        //aliQRCode.loadUrl(aliQRURL);

        //或者
        aliQRCode.loadData(aliQRHtml, "text/html", "utf-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aliqrcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}