/*  JD Edwards  2021 April 4    CSCE 492

    This file creates a browser window inside the app
    It loads a clicked article url using an instantiation of Chrome in the app
    This is so the user can view the article without having to switch to their browser application
    This class is instantiated in RSS_view

    Created using Anupam Chugh's tutorial from:
    https://www.journaldev.com/20126/android-rss-feed-app
 */
package com.example.template;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RSSActivityBrowser extends AppCompatActivity {

    // Create variables
    WebView webView;
    String url;

    // Start activity and set to activity_rss_browser_view
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_browser_view);
        Intent in = getIntent();
        url = in.getStringExtra("url");

        // Check if article URL exists
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "URL not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set the webview
        webView = findViewById(R.id.webView);
        initWebView();
        // Load the article's URL
        webView.loadUrl(url);
    }

    // Class for initializing the webView
    private void initWebView() {
        // Setup the browser, clear the cache, and enable JavaScript
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        // No horizontal scrolling since it would be distracting
        webView.setHorizontalScrollBarEnabled(false);

        // Set webView client
        webView.setWebViewClient(new WebViewClient() {
            // Set parameters for loading, overrides, finished loading, and errors for webView
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                invalidateOptionsMenu();
            }
        });
        // Clear cache and history to keep memory free
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
    }

    // Class for creating an instance of Chrome in the app
    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

}

