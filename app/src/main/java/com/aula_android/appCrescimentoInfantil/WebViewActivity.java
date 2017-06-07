package com.aula_android.appCrescimentoInfantil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by allan.lemes on 06/06/2017.
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ArrayList<ArrayList<String>> registros;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gráfico do Crescimento");

        Bundle extras = getIntent().getExtras();
        registros =(ArrayList<ArrayList<String>>) extras.getSerializable(ViewCriancaActivity.REGISTROS);


        /*webView = (WebView) findViewById(R.id.webViewCrescimento);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://chart.googleapis.com/chart?cht=lc&chco=FF6342,ADDE63,63C6DE&chs=250x100&chd=t:6.92,8.98,10.5,11.6%7C7.5,10.12,12.3,15.25%7C8.76,11.25,13.02,14.33&chl=6%20meses%7C1%20ano%7C1a%206m%7C2%20anos&chdl=Peso%20Min.%7CPeso%20M%C3%A1x.%7C%20Peso");
        */

        ArrayList<String> tempNodo;
        for (int i = 0; i < registros.size();i ++){
            tempNodo = registros.get(i);

        }
    }

}