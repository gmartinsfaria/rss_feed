package com.oita.gmart.noticias;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.View;

public class MainActivity extends AppCompatActivity {

    //tempo em que o ecrã inicial está ativo (1000 = 1segundo)
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verifica, caso não exista ligação à internet, mostra o alert dialog a informar, caso exista, vai fazer a contagem do tempo, até passar para a outra activity
        if(!isConnected(MainActivity.this)){
            buildDialog(MainActivity.this).show();
        }else{


            new Handler().postDelayed(() -> {
                Intent jornaisIntent = new Intent(MainActivity.this, JornaisActivity.class);
                startActivity(jornaisIntent);
                finish();
            }, SPLASH_TIME_OUT);

        }


    }

    //verificar se existe ligação à Internet quando se inicia a aplicação============================
    public boolean isConnected (Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());

        }else {

            return false;
        }
    }

    //alert dialog que informa que o utilizador não tem ligação à Internet e por isso não pode utilizar a aplicação
    public AlertDialog.Builder buildDialog(Context c){

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Sem ligação à Internet");
        builder.setMessage("Precisa de ter dados móveis ou wifi para utilizar esta aplicação. Clique OK para sair");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;

    }

}
