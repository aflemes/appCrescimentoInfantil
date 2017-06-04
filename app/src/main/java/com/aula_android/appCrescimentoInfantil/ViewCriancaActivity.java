package com.aula_android.appCrescimentoInfantil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCriancaActivity extends AppCompatActivity {
    /**/
    private ListView progressoListView;
    public static final String LINHA_ID = "idLinha";
    public Long LINHA_ID_AUX;
    private long idLinha;
    private TextView lblNome;
    private TextView lblSexo;
    private TextView lblNascimento;
    private TextView lblVendas;
    /**/
    private CursorAdapter progressoAdapter; // Adaptador para a ListView
    /**/
    private Button btnAdicionar;
    private Button btnRemover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_crianca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Consulta Criança");

        lblNome = (TextView) findViewById(R.id.lblNome);
        lblSexo = (TextView) findViewById(R.id.lblSexo);
        lblNascimento = (TextView) findViewById(R.id.lblNascimento);

        Bundle extras = getIntent().getExtras();
        idLinha = extras.getLong(MainActivity.LINHA_ID);

        btnAdicionar = (Button) findViewById(R.id.btnAdicionar);
        btnAdicionar.setOnClickListener(openAdicionarProgresso);

        btnRemover = (Button) findViewById(R.id.btnRemover);
        btnRemover.setOnClickListener(deleteRecord);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"dtatualizacao","peso","altura"};
        int[] destino = new int[] { R.id.txtNome, R.id.spiSexo,R.id.txtDtNascimento};
        int flags = 0;

        progressoListView = (ListView) findViewById(R.id.listViewProgresso);

        progressoAdapter = new SimpleCursorAdapter(ViewCriancaActivity.this,R.layout.activity_view_crianca,null,origem,destino,flags);
        progressoListView.setAdapter(progressoAdapter);

        progressoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                for (int j = 0; j < adapterView.getChildCount(); j++)
                    adapterView.getChildAt(j).setBackgroundColor(Color.LTGRAY);

                // change the background color of the selected element
                view.setBackgroundColor(Color.GRAY);
                //
                LINHA_ID_AUX = id;
            }
        });
    }

    View.OnClickListener openAdicionarProgresso = new View.OnClickListener() {
        public void onClick(View v) {
            Intent addProgressoCrianca = new Intent(getApplicationContext(), AddNovoProgressoActivity.class);
            addProgressoCrianca.putExtra(LINHA_ID, idLinha);
            startActivity(addProgressoCrianca);
        }
    };

    View.OnClickListener deleteRecord = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewCriancaActivity.this);

            builder.setTitle(R.string.confirmaTitulo);
            builder.setMessage(R.string.confirmaMensagemSelecao);

            // provide an OK button that simply dismisses the dialog
            builder.setPositiveButton(R.string.botao_delete,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int button) {
                            final DBAdapter conexaoDB = new DBAdapter(ViewCriancaActivity.this);

                            AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>(){
                                @Override
                                protected Object doInBackground(Long... params){
                                    try{
                                        conexaoDB.open();
                                        conexaoDB.excluiDesenvolvimento(LINHA_ID_AUX);
                                        conexaoDB.close();
                                    }
                                    catch(SQLException e){
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result){
                                    finish();
                                }
                            };


                            deleteTask.execute(new Long[] { idLinha });
                        }
                    }); // finaliza o  método setPositiveButton

            builder.setNegativeButton(R.string.botao_cancel, null);
            builder.show();
        }
    };

    @Override
    protected void onResume(){
        super.onResume();

        new CarregaClienteTask().execute(idLinha);
        new getProgresso().execute();
    }
    // Executa a consulta em uma thead separada
    private class CarregaClienteTask extends AsyncTask<Long, Object, Cursor> {
        DBAdapter databaseConnector = new DBAdapter(ViewCriancaActivity.this);

        @Override
        protected Cursor doInBackground(Long... params){
            databaseConnector.open();
            return databaseConnector.getCrianca(params[0]);
        }
        // Usa o Cursor retornado do método doInBackground
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();

            int nomeIndex = result.getColumnIndex("nome");
            int sexoIndex = result.getColumnIndex("sexo");
            int nascimentoIndex = result.getColumnIndex("nascimento");

            lblNome.setText(result.getString(nomeIndex));
            lblSexo.setText(result.getString(sexoIndex));
            lblNascimento.setText(result.getString(nascimentoIndex));
            
            result.close();
            databaseConnector.close();
        }
    }

    private class getProgresso extends AsyncTask<Object, Object, Cursor> {
        DBAdapter conexaoDB = new DBAdapter(ViewCriancaActivity.this);
        @Override
        protected Cursor doInBackground(Object... params){
            conexaoDB.open(); //abre a base de dados
            return conexaoDB.getProgresso(idLinha);
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            progressoAdapter.changeCursor(result); //altera o cursor para um novo cursor
            conexaoDB.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consulta_crianca, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String dtNascto = lblNascimento.getText().toString();

        switch (item.getItemId()){
            case R.id.editItem:
                Intent addEditCrianca = new Intent(this, AddNovaCriancaActivity.class);

                addEditCrianca.putExtra(MainActivity.LINHA_ID, idLinha);
                addEditCrianca.putExtra("nome", lblNome.getText());
                addEditCrianca.putExtra("sexo", lblSexo.getText());
                addEditCrianca.putExtra("nascimento", dtNascto);

                startActivity(addEditCrianca);
                return true;
            case R.id.deleteItem:
                deleteCrianca();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCrianca(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewCriancaActivity.this);

        builder.setTitle(R.string.confirmaTitulo);
        builder.setMessage(R.string.confirmaMensagem);

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.botao_delete,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int button){
                        final DBAdapter conexaoDB = new DBAdapter(ViewCriancaActivity.this);

                        AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>(){
                            @Override
                            protected Object doInBackground(Long... params){
                                try{
                                    conexaoDB.open();
                                    conexaoDB.excluiCrianca(params[0]);
                                    conexaoDB.close();
                                }
                                catch(SQLException e){
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result){
                                finish();
                            }
                        };


                        deleteTask.execute(new Long[] { idLinha });
                    }
                }); // finaliza o  método setPositiveButton

        builder.setNegativeButton(R.string.botao_cancel, null);
        builder.show();
    }
}
