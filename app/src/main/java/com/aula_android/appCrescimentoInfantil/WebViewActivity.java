package com.aula_android.appCrescimentoInfantil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by allan.lemes on 06/06/2017.
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ArrayList<ArrayList<String>> registros;
    private String opc_grafico;
    /* VARIAVEIS GLOBAIS DO GRAFICO*/
    private String googleURL    = "https://chart.googleapis.com/chart?";
    private String tipoGrafico  = "cht=lc"; //linhas
    private String coresGrafico = "&chco=FF6342,ADDE63,63C6DE";
    private String dimenGrafico = "&chs=360x250";
    private String dadosGrafico = "&chd=t:";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gráfico do Crescimento");

        Bundle extras = getIntent().getExtras();
        registros   =(ArrayList<ArrayList<String>>) extras.getSerializable(ViewCriancaActivity.REGISTROS);
        opc_grafico = extras.getString(ViewCriancaActivity.OPC_GRAFICO);

        webView = (WebView) findViewById(R.id.webViewCrescimento);
        webView.getSettings().setJavaScriptEnabled(true);

        String linhaInteira = "";

        if (opc_grafico.equals("Idade")){
            linhaInteira = gerar_grafico_idade();
        }
        else
            if (opc_grafico.equals("Altura")){
                linhaInteira = gerar_grafico_altura();
            }
        webView.loadUrl(linhaInteira);
    }

    private String gerar_grafico_idade(){
        String titEixoX     = "&chl=";
        String legGrafico   = "&chdl=Peso";

        ArrayList<String> tempNodo;

        for (int i = 0; i < registros.size();i ++){
            tempNodo     = registros.get(i);

            if ((i + 1) == registros.size()) {
                dadosGrafico += tempNodo.get(1);
                titEixoX     += tempNodo.get(0).toString();
            }
            else {
                dadosGrafico += tempNodo.get(1) + ",";
                titEixoX     += tempNodo.get(0).toString() + "|";
            }
        }
        return googleURL + tipoGrafico + coresGrafico + dimenGrafico + dadosGrafico + titEixoX + legGrafico;
    }

    private String gerar_grafico_altura(){
        String titEixoX     = "&chl=";
        String legGrafico   = "&chdl=Peso";

        ArrayList<String> tempNodo;

        for (int i = 0; i < registros.size();i ++){
            tempNodo     = registros.get(i);

            if ((i + 1) == registros.size()) {
                dadosGrafico += tempNodo.get(1);
                titEixoX     += tempNodo.get(2).toString();
            }
            else {
                dadosGrafico += tempNodo.get(1) + ",";
                titEixoX     += tempNodo.get(2).toString() + "|";
            }
        }

        return googleURL + tipoGrafico + coresGrafico + dimenGrafico + dadosGrafico + titEixoX + legGrafico;
    }

}