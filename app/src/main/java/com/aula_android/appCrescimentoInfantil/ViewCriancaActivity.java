package com.aula_android.appCrescimentoInfantil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCriancaActivity extends AppCompatActivity {
    private long idLinha;
    private TextView lblNome;
    private TextView lblSexo;
    private TextView lblNascimento;
    private TextView lblVendas;
    /**/
    private Button btnAdicionar;

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
    }

    View.OnClickListener openAdicionarProgresso = new View.OnClickListener() {
        public void onClick(View v) {
            Intent addProgressoCrianca = new Intent(getApplicationContext(), AddNovoProgressoActivity.class);
            startActivity(addProgressoCrianca);
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        new CarregaClienteTask().execute(idLinha);
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
