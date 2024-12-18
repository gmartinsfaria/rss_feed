package com.oita.gmart.noticias;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Arrays;

public class JornaisActivity extends AppCompatActivity {

    TextView vazio;
    MaterialSearchView searchView_temas;

    private static final String TAG = "JornaisActivity";

    ListView lvTemas;

    AlertDialog.Builder alertTemas;

    ArrayList<String> escolhas;
    ArrayList<String> escolhasUrl;
    ArrayList<String> escolhasEncontradas;
    ArrayList<String> escolhasUrlEncontradas;
    ArrayList<String> rss;

    String[] listItems;
    boolean[] checkedItems;
    boolean jaEscolhido = false;
    boolean aProcurar = false;

    int numeroUtilizacoes = 0;

    public static final String PREFS_NAME = "meuPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jornais);

        escolhas = new ArrayList<>();
        escolhasUrl = new ArrayList<>();
        escolhasEncontradas = new ArrayList<>();
        escolhasUrlEncontradas = new ArrayList<>();

        vazio = (TextView)findViewById(R.id.vazio);
        listItems = getResources().getStringArray(R.array.preference);
        checkedItems = new boolean[listItems.length];
        rss = new ArrayList<>(Arrays.asList(
                /*Última Hora*/"https://www.noticiasaominuto.com/rss/ultima-hora",
                /*Política*/"https://www.noticiasaominuto.com/rss/politica",
                /*Economia*/"https://www.noticiasaominuto.com/rss/economia",
                /*Desporto*/"https://www.noticiasaominuto.com/rss/desporto",
                /*País*/"https://www.noticiasaominuto.com/rss/pais",
                /*Mundo*/"https://www.noticiasaominuto.com/rss/mundo",
                /*Cultura*/"https://www.noticiasaominuto.com/rss/cultura",
                /*Casa*/"https://www.noticiasaominuto.com/rss/casa"));

        //estes feeds comentados sao os feeds do Jornal de negócios
        //"http://feeds.jn.pt/JN-Ultimas", "http://feeds.jn.pt/JN-Nacional", "http://feeds.jn.pt/JN-Pais", "http://feeds.jn.pt/JN-Justica" , "http://feeds.jn.pt/JN-Mundo", "http://feeds.jn.pt/JN-Desporto", "http://feeds.jn.pt/JN-Gente", "http://feeds.jn.pt/JN-Economia"
        lvTemas = (ListView)findViewById(R.id.lvTemas);

        loadOpcoesChecked();

        //verifica se é a primeira vez q se abre a app para mostrar ou não automaticamente a modal
        for (int i = 0; i<checkedItems.length; i++){

            if(checkedItems[i] == true){
                jaEscolhido = true;
                break;
            }
        }

        if (numeroUtilizacoes <= 0 || !jaEscolhido){
            abrirModal();
            vazio.setVisibility(View.VISIBLE);
        }else{

            vazio.setVisibility(View.INVISIBLE);
        }


        //define a toolbar, o titulo e a cor da mesma
        Toolbar toolbar_temas = (Toolbar)findViewById(R.id.toolbar_temas);
        setSupportActionBar(toolbar_temas);
        getSupportActionBar().setTitle("RSS Feed - Temas");
        toolbar_temas.setTitleTextColor(Color.parseColor("#FFFFFF"));


        //SEARCHVIEW================================================================================
        //defina a searchview, para procurar notícias cujos títulos tenham um determinado conjunto de caracteres
        searchView_temas = (MaterialSearchView)findViewById(R.id.search_view_temas);
        searchView_temas.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                escolhasEncontradas.clear();
                escolhasUrlEncontradas.clear();

                //se clicar em fechar, vai aparecer a lista total dos temas escolhidos
                lvTemas = (ListView)findViewById(R.id.lvTemas);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(JornaisActivity.this, R.layout.row_jornais, R.id.jornais, escolhas);
                lvTemas.setAdapter(adapter);

            }
        });
        searchView_temas.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                escolhasEncontradas.clear();
                escolhasUrlEncontradas.clear();

                if (newText != null && !newText.isEmpty()){

                    for (int i = 0; i < escolhas.size(); i++){

                        if(escolhas.get(i).toLowerCase().startsWith(newText.toLowerCase())){
                            escolhasEncontradas.add(String.valueOf(escolhas.get(i)));
                            escolhasUrlEncontradas.add(String.valueOf(rss.get(i)));
                        }

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(JornaisActivity.this, R.layout.row_jornais, R.id.jornais, escolhasEncontradas);
                    lvTemas.setAdapter(adapter);

                }else{
                    //se o texto procurado for nulo, retorna novamente a lista total
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(JornaisActivity.this, R.layout.row_jornais, R.id.jornais, escolhas);
                    lvTemas.setAdapter(adapter);
                }

                return true;
            }
        });
        searchView_temas.setHint("Procurar");


        //fazer com que ao clicar em cada item da lista, o utilizador seja direcionado para uma nova
        //activity com a lista das notícias correspondentes ao tema escolhido
        lvTemas = (ListView)findViewById(R.id.lvTemas);
        lvTemas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent meuIntent = new Intent(JornaisActivity.this, HomeActivity.class);

                //verifica e está a existir uma procura ou não
                if(searchView_temas.isSearchOpen()){
                    aProcurar = true;
                }

                if(!searchView_temas.isSearchOpen()){
                    aProcurar = false;
                }

                if(!aProcurar){
                    meuIntent.putExtra("key1", String.valueOf(escolhasUrl.get(position)));

                }

                if(aProcurar){
                    meuIntent.putExtra("key1", String.valueOf(escolhasUrlEncontradas.get(position)));

                }

                startActivity(meuIntent);
            }
        });

    }

    //define qual o menu a apresentar===============================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.jornais_menu,menu);
        MenuItem item_temas = menu.findItem(R.id.temas_procurar);
        searchView_temas.setMenuItem(item_temas);
        return true;
    }

    //define os comportamentos dos elementos da action bar==========================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.temas_more:
                abrirModal();

                break;

            case R.id.temas_procurar:
                aProcurar = true;
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //abrir modal para escolher temas de notícias===================================================
    public void abrirModal() {

        alertTemas = new AlertDialog.Builder(this);
        alertTemas.setTitle("Escolhe os teus jornais");
        // alert.setMessage("Message");

        //não permite cancelar a modal, mesmo que se carregue no botão de voltar ou que se faça um click fora da modal
        alertTemas.setCancelable(false);

        //checkboxes automáticas
        alertTemas.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    checkedItems[position] = true;
                }else{
                    checkedItems[position] = false;
                }
            }
        });

        alertTemas.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                salvarOpcoesChecked();
                loadOpcoesChecked();

                jaEscolhido = false;

                for (int i = 0; i<checkedItems.length; i++){

                    if(checkedItems[i] == true){
                        jaEscolhido = true;
                        break;
                    }
                }

                if(!jaEscolhido){
                    vazio.setVisibility(View.VISIBLE);
                }

                if(jaEscolhido){
                    vazio.setVisibility(View.INVISIBLE);
                }
            }
        });

        alertTemas.setNegativeButton("Limpar tudo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }

                salvarOpcoesChecked();
                loadOpcoesChecked();

                vazio.setVisibility(View.VISIBLE);
            }
        });

        alertTemas.show();
    }


    //load dos jornais escolhidos===================================================================
    public void loadOpcoesChecked(){

        escolhas.clear();
        escolhasUrl.clear();

        //vai criar a storage do numero de utilizações
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        numeroUtilizacoes = settings.getInt("numeroUtilizacoes", numeroUtilizacoes);


        //opcao0
        SharedPreferences settingsUltimas = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[0] = settingsUltimas.getBoolean("opcaoUltimas", checkedItems[0]);

        //opcao1
        SharedPreferences settingsNacional = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[1] = settingsNacional.getBoolean("opcaoNacional", checkedItems[1]);

        //opcao2
        SharedPreferences settingsLocal = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[2] = settingsLocal.getBoolean("opcaoLocal", checkedItems[2]);

        //opcao3
        SharedPreferences settingsJustica = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[3] = settingsJustica.getBoolean("opcaoJustica", checkedItems[3]);

        //opcao4
        SharedPreferences settingsMundo = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[4] = settingsMundo.getBoolean("opcaoMundo", checkedItems[4]);

        //opcao5
        SharedPreferences settingsDesporto = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[5] = settingsDesporto.getBoolean("opcaoDesporto", checkedItems[5]);

        //opcao6
        SharedPreferences settingsPessoas = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[6] = settingsPessoas.getBoolean("opcaoPessoas", checkedItems[6]);

        //opcao7
        SharedPreferences settingsEconomia = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        checkedItems[7] = settingsEconomia.getBoolean("opcaoEconomia", checkedItems[7]);

        for (int i = 0; i < checkedItems.length; i++){

            if(checkedItems[i]){
                escolhas.add(String.valueOf(listItems[i]));
                escolhasUrl.add(String.valueOf(rss.get(i)));
            }
        }

        lvTemas = (ListView)findViewById(R.id.lvTemas);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JornaisActivity.this, R.layout.row_jornais, R.id.jornais, escolhas);
        lvTemas.setAdapter(adapter);

    }

    //salvar os jornais escolhidos==================================================================
    public void salvarOpcoesChecked(){

        //salvar numero de utilizações
        numeroUtilizacoes += 1;
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("numeroUtilizacoes", numeroUtilizacoes);
        editor.apply();

        //opcao 0
        SharedPreferences settingsUltimas = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUltimas = settingsUltimas.edit();
        editorUltimas.putBoolean("opcaoUltimas", checkedItems[0]);
        editorUltimas.apply();

        //opcao1
        SharedPreferences settingsNacional = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNacional = settingsNacional.edit();
        editorNacional.putBoolean("opcaoNacional", checkedItems[1]);
        editorNacional.apply();

        //opcao2
        SharedPreferences settingsLocal = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorLocal = settingsLocal.edit();
        editorLocal.putBoolean("opcaoLocal", checkedItems[2]);
        editorLocal.apply();

        //opcao3
        SharedPreferences settingsJustica = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorJustica = settingsJustica.edit();
        editorJustica.putBoolean("opcaoJustica", checkedItems[3]);
        editorJustica.apply();

        //opcao4
        SharedPreferences settingsMundo = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorMundo = settingsMundo.edit();
        editorMundo.putBoolean("opcaoMundo", checkedItems[4]);
        editorMundo.apply();

        //opcao5
        SharedPreferences settingsDesporto = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorDesporto = settingsDesporto.edit();
        editorDesporto.putBoolean("opcaoDesporto", checkedItems[5]);
        editorDesporto.apply();

        //opcao6
        SharedPreferences settingsPessoas = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorPessoas = settingsPessoas.edit();
        editorPessoas.putBoolean("opcaoPessoas", checkedItems[6]);
        editorPessoas.apply();

        //opcao7
        SharedPreferences settingsEconomia = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorEconomia = settingsEconomia.edit();
        editorEconomia.putBoolean("opcaoEconomia", checkedItems[7]);
        editorEconomia.apply();
    }

}


