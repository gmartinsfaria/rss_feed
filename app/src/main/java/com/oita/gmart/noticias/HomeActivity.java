package com.oita.gmart.noticias;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    MaterialSearchView searchView;

    private static final String TAG = "HomeActivity";

    AlertDialog.Builder alertVistaOpcoes;

    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> titlesEncontrados;
    ArrayList<String> dates;
    ArrayList<String> contents;
    ArrayList<String> links;
    ArrayList<String> linksEncontrados;
    ArrayList<String> thumbnails;

    ArrayList<Noticia> noticias;
    ArrayList<Noticia> noticiasEncontradas;

    Intent meuIntentLocal;
    boolean[] checkedItems;
    boolean aProcurar = false;
    boolean[] opcaoMemorizada = {false};
    String[] memorizar = {"Memorizar a minha opção"};
    String[] appBrowser = {"RSS Feed", "Browser"};
    String[] listItems;
    String stringUrl;
    String titulo;

    int appBrowserNumerico = -1;
    static int numeroImagens = 0;

    int numeroUtilizacoes2 = 0; //vai definir se é a primeira vez que o utilizador abre a app ou não, caso seja, mostra de imediato a modal
    AlertDialog.Builder alert;

    public static final String PREFS_NAME = "meuPrefsFile2"; //preferência que vai guardar a variável contadora de utilizações

    //definir tudo o que ocorre no onCreate da app==================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //passa o URL do rss feed correspondente ao tema escolhido na activity anterior
        Bundle extras = getIntent().getExtras();
        stringUrl = extras.getString("key1");

        switch (String.valueOf(stringUrl)){

            case "https://www.noticiasaominuto.com/rss/ultima-hora":
                titulo = "Última Hora";
                break;
            case "https://www.noticiasaominuto.com/rss/politica":
                titulo = "Política";
                break;
            case "https://www.noticiasaominuto.com/rss/economia":
                titulo = "Economia";
                break;
            case "https://www.noticiasaominuto.com/rss/desporto":
                titulo = "Desporto";
                break;
            case "https://www.noticiasaominuto.com/rss/pais":
                titulo = "País";
                break;
            case "https://www.noticiasaominuto.com/rss/mundo":
                titulo = "Mundo";
                break;
            case "https://www.noticiasaominuto.com/rss/cultura":
                titulo = "Cultura";
                break;
            case "https://www.noticiasaominuto.com/rss/casa":
                titulo = "Casa";
                break;
        }

        titles = new ArrayList<>();
        dates = new ArrayList<>();
        contents = new ArrayList<>();
        links = new ArrayList<>();
        linksEncontrados = new ArrayList<>();
        titlesEncontrados = new ArrayList<>();
        thumbnails = new ArrayList<>();

        noticias = new ArrayList<>();
        noticiasEncontradas = new ArrayList<>();

        listItems = getResources().getStringArray(R.array.preference); //vai buscar o string-array com o name="preference" para criar o array que vai ser usado na lista das checkbox
        checkedItems = new boolean[listItems.length];


        new ProcessInBackground().execute();


        //define a toolbar, o titulo e a cor da mesma
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        //SEARCHVIEW
        //defina a searchview, para procurar notícias cujos títulos tenham um determinado conjunto de caracteres
        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                //se o searchView for fechado, vai retornar a lista com todas as notícias

                noticiasEncontradas.clear();
                linksEncontrados.clear();
                titlesEncontrados.clear();

                lvRss = (ListView)findViewById(R.id.lvRss);
                NoticiaListAdapter adapter = new NoticiaListAdapter(HomeActivity.this, R.layout.row, noticias);
                lvRss.setAdapter(adapter);


            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                noticiasEncontradas.clear();
                linksEncontrados.clear();
                titlesEncontrados.clear();

                if(newText != null && !newText.isEmpty()){
                    List<String> lstFound = new ArrayList<String>();


                    for (int i = 0; i < titles.size(); i++){

                        if(titles.get(i).toLowerCase().contains(newText.toLowerCase())){

                            linksEncontrados.add(String.valueOf(links.get(i)));
                            titlesEncontrados.add(String.valueOf(titles.get(i)));
                            Noticia elemento = new Noticia(titles.get(i), dates.get(i), contents.get(i), thumbnails.get(i));


                            noticiasEncontradas.add(elemento);

                        }
                    }


                    /*
                    for(String item:titles){
                        if(item.toLowerCase().contains(newText.toLowerCase())){
                            Noticia elemento = new Noticia("batatas", "batatas", "batatas");

                            noticiasEncontradas.add(elemento);
                        }
                    }
                    */

                    NoticiaListAdapter adapter = new NoticiaListAdapter(HomeActivity.this, R.layout.row, noticiasEncontradas);
                    lvRss.setAdapter(adapter);

                    adapter.notifyDataSetChanged();



                }else{

                    //se o texto de procura for null, retorna a lista de todas as notícias

                    noticiasEncontradas.clear();

                    NoticiaListAdapter adapter = new NoticiaListAdapter(HomeActivity.this, R.layout.row, noticias);
                    lvRss.setAdapter(adapter);

                }

                return true;
            }
        });
        searchView.setHint("Procurar");

        Log.d(TAG, "onCreate: Start");

        lvRss = (ListView)findViewById(R.id.lvRss);

        loadOpcoesChecked();

        if(numeroUtilizacoes2 <= 0){
            abrirModal();
        }

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // aqui definimos o q vamos fazer sempre q clicamos numa notícia (neste caso, vamos abrir o seu link)

                //verifica e está a existir uma procura ou não
                if(searchView.isSearchOpen()){
                    aProcurar = true;
                }

                if(!searchView.isSearchOpen()){
                    aProcurar = false;
                }

                if (appBrowserNumerico == -1){
                    abrirModal();
                }

                //abrir na própria app
                if(appBrowserNumerico == 0){

                    Intent meuIntent = new Intent(HomeActivity.this, VistaWeb.class);

                    if(!aProcurar){
                        meuIntent.putExtra("key1", String.valueOf(links.get(position)));
                        meuIntent.putExtra("key2", String.valueOf(titles.get(position)));


                    }

                    if(aProcurar){
                        meuIntent.putExtra("key1", String.valueOf(linksEncontrados.get(position)));
                        meuIntent.putExtra("key2", String.valueOf(titlesEncontrados.get(position)));

                    }

                    startActivity(meuIntent);
                }

                //abrir no browser
                if(appBrowserNumerico == 1){
                    if(!aProcurar){
                        Uri uri = Uri.parse(links.get(position)); //ligação ao link específico de cada notícia
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri); //aqui devfinimos que queremos ver a página web do link guardado em uri
                        startActivity(intent); //iniciamos a ação designada no intent
                    }


                    if(aProcurar){
                        Uri uri = Uri.parse(linksEncontrados.get(position)); //ligação ao link específico de cada notícia
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri); //aqui devfinimos que queremos ver a página web do link guardado em uri
                        startActivity(intent); //iniciamos a ação designada no intent
                    }
                }

            }
        });



    }

    //define qual o menu a apresentar===============================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    //define os comportamentos dos elementos da action bar==========================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_refresh:

                if(!isConnected(HomeActivity.this)){
                    //buildDialog(HomeActivity.this).show();

                    Toast.makeText(HomeActivity.this, "Sem ligação à Internet", Toast.LENGTH_SHORT).show();
                }else{
                    noticiasEncontradas.clear();
                    noticias.clear();
                    titles.clear();
                    dates.clear();
                    contents.clear();

                    new ProcessInBackground().execute();
                    return true;
                }
                break;
            case R.id.action_mais:
                abrirModal();
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    //tratar de toda a extração feita ao rss feed===================================================
    public InputStream getInputStream(URL url){
        try{

            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>{


        ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog.setMessage("A carregar informação...");
            progressDialog.show();
        }


        @Override
        protected Exception doInBackground(Integer... params) {



            try {

                URL url = new URL(stringUrl);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                //xpp.setInput(getInputStream(url), "UTF-8"); //para português usar: ISO-8859-1

                xpp.setInput(url.openConnection().getInputStream(), "UTF-8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT){

                    if (eventType == XmlPullParser.START_TAG){

                        if(xpp.getName().equalsIgnoreCase("item")){

                            insideItem = true;

                        }else if(xpp.getName().equalsIgnoreCase("title")){

                            if (insideItem){
                                Log.e("some_tag", "some message");
                                titles.add(xpp.nextText());
                            }
                        }else if(xpp.getName().equalsIgnoreCase("link")){

                            if (insideItem){

                                links.add(xpp.nextText());
                            }
                        }else if(xpp.getName().equalsIgnoreCase("pubDate")){

                            if (insideItem){

                                dates.add(xpp.nextText());
                            }
                        }else if(xpp.getName().equalsIgnoreCase("description")){

                            if (insideItem){

                                //retirar a parte da descrição que tem uma tag em formato de texto
                                String resultado = xpp.nextText().replaceAll("<img.+?>", "");

                                contents.add(resultado);
                            }
                        }else if(xpp.getName().equalsIgnoreCase("media:thumbnail")){

                            if (insideItem){

                                int larguraImagem = Integer.parseInt(xpp.getAttributeValue(1));
                                int alturaImagem = Integer.parseInt(xpp.getAttributeValue(2));


                                if (numeroImagens == 0){
                                    if((larguraImagem >= 184 && larguraImagem <= 613)){
                                        if((alturaImagem >= 88 && alturaImagem <= 565)){
                                            numeroImagens += 1;
                                            thumbnails.add(String.valueOf(xpp.getAttributeValue(0)));
                                        }
                                    }
                                }
                            }
                        } else if(xpp.getName().equalsIgnoreCase("enclosure")){

                            thumbnails.add(String.valueOf(xpp.getAttributeValue(0)));
                        }


                    }else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){

                        numeroImagens = 0;
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }

            }
            catch (MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                Log.e("some_tag", "some message", e);
                exception = e;
            }

            return exception;
        }


        @Override
        protected void onPostExecute(Exception s) {

            super.onPostExecute(s);

            for (int i = 0; i < titles.size(); i++){
                Log.e("voltas_noticiasa", "a carregar notícias");
                Noticia elemento = new Noticia(titles.get(i), dates.get(i), contents.get(i), thumbnails.get(i)); //o terceiro elemento são os contents

                noticias.add(elemento);
            }

            /*
            for (int i = 0; i < titles.size(); i++){

                Noticia elemento = new Noticia(titles.get(i), dates.get(i), contents.get(i));

                for (int a = 0; a < noticias.size(); a++){

                    if (elemento != noticias.get(a)){
                        noticias.add(elemento);
                    }
                }
            }
            */


            NoticiaListAdapter adapter = new NoticiaListAdapter(HomeActivity.this, R.layout.row, noticias);
            lvRss.setAdapter(adapter);

            /*
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.row, R.id.txtTitle, titles);

            lvRss.setAdapter(adapter);
            */

            progressDialog.dismiss();
        }
    }


    //verificar se no refresh das notícias há ligação à internet ou não=============================
    public boolean isConnected (Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())){
                return true;

            }else {

                return false;
            }

        }else {

            return false;
        }
    }


    public void abrirModal() {

        alertVistaOpcoes = new AlertDialog.Builder(this);
        alertVistaOpcoes.setTitle("Onde mostrar as tuas notícias?\nÍcon direito do menu para alterar.");
        //alertVistaOpcoes.setMessage("Escolhe onde preferes ver as tuas notícias. Tens sempre a opção de voltar a alterar, para isso, basta acederes ao ícone direito da barra de menu.");

        //não permite cancelar a modal, mesmo que se carregue no botão de voltar ou que se faça um click fora da modal
        alertVistaOpcoes.setCancelable(false);


        /*alertVistaOpcoes.setMultiChoiceItems(memorizar, opcaoMemorizada, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    opcaoMemorizada[position] = true;
                }else{
                    opcaoMemorizada[position] = false;
                }
            }
        });
        */

        alertVistaOpcoes.setSingleChoiceItems(appBrowser, appBrowserNumerico, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){

                    case 0:
                        appBrowserNumerico = 0;
                        break;

                    case 1:
                        appBrowserNumerico = 1;
                        break;

                }

            }
        });



        alertVistaOpcoes.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                salvarOpcoesChecked();
                loadOpcoesChecked();
            }
        });

        alertVistaOpcoes.show();
    }

    public void loadOpcoesChecked(){

        SharedPreferences settingsOpcaoMemorizada = getSharedPreferences(PREFS_NAME,0);
        appBrowserNumerico = settingsOpcaoMemorizada.getInt("opcaoMemorizada", appBrowserNumerico);

        //vai criar a storage do numero de utilizações
        SharedPreferences settingsNumeroUtilizacoes2 = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        numeroUtilizacoes2 = settingsNumeroUtilizacoes2.getInt("numeroUtilizacoes2", numeroUtilizacoes2);
    }

    public void salvarOpcoesChecked(){

        //opcao 0
        SharedPreferences settingsOpcaoMemorizada = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editorOpcaoMemorizada = settingsOpcaoMemorizada.edit();
        editorOpcaoMemorizada.putInt("opcaoMemorizada", appBrowserNumerico);
        editorOpcaoMemorizada.apply();

        //salvar numero de utilizações
        numeroUtilizacoes2 += 1;
        SharedPreferences settingsNumeroUtilizacoes2 = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNumeroUtilizacoes2 = settingsNumeroUtilizacoes2.edit();
        editorNumeroUtilizacoes2.putInt("numeroUtilizacoes2", numeroUtilizacoes2);
        editorNumeroUtilizacoes2.apply();

    }


}
