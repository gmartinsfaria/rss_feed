package com.oita.gmart.noticias;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class VistaWeb extends AppCompatActivity {

    String stringUrl;
    String stringTitulo;

    WebView wv;
    ProgressBar bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_web);

        //passa o URL/titulo da notícia escolhida na listView da activity HomeActivity
        Bundle extras = getIntent().getExtras();
        stringUrl = extras.getString("key1");
        stringTitulo = extras.getString("key2");

        //define a toolbar, o titulo e a cor da mesma
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_vista_web);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("RSS Feed");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        wv = (WebView)findViewById(R.id.wv);
        bar = (ProgressBar)findViewById(R.id.barra_progresso);

        wv.setWebViewClient(new myWebclient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setFocusable(true);
        wv.setFocusableInTouchMode(true);

        //fazer o set do rendar para "elevado"
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        //wv.getSettings().setAppCacheEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.loadUrl(stringUrl);

    }

    //define qual o menu a apresentar===============================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_web_menu,menu);
        return true;
    }

    //define os comportamentos dos elementos da action bar==========================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.refresh_vista_web:
                wv.loadUrl(stringUrl);
                break;


        }

        return super.onOptionsItemSelected(item);
    }



    public class myWebclient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode==KeyEvent.KEYCODE_BACK) && wv.canGoBack()){
            wv.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
