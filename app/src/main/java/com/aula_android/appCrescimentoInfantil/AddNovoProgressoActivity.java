package com.aula_android.appCrescimentoInfantil;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by skyli on 30/05/2017.
 */

public class AddNovoProgressoActivity extends AppCompatActivity {
    /**/
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private Button btnSalvar;
    /*campos em tela*/
    private EditText txtDtAtualizacao;
    private EditText txtPeso;
    private EditText txtAltura;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_progresso_crianca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Adicionar progresso da criança");

        /*init fields*/
        txtDtAtualizacao = (EditText) findViewById(R.id.txtDtAtualizacao);
        txtPeso = (EditText) findViewById(R.id.txtPeso);
        txtAltura = (EditText) findViewById(R.id.txtAltura);

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(salvarProgressoButtonClicked);

        initDateDialog();
    }

    private void initDateDialog(){
        dateView = (TextView) findViewById(R.id.txtDtAtualizacao);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int mYear, int mMonth, int mDay) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");

        dateView.setText(new StringBuilder()
                .append(mDay).append("-")
                .append(mMonth + 1).append("-")
                .append(mYear).append(" "));
    }

    View.OnClickListener salvarProgressoButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            if (txtDtAtualizacao.getText().length() != 0){
                AsyncTask<Object, Object, Object> salvaProgressoTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaProgresso(); // Salva o livro na base de dados
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result){
                        finish(); // Fecha a atividade
                    }
                };

                // Salva o livro no BD usando uma thread separada
                salvaProgressoTask.execute();
            } // end if
            else {
                // Cria uma caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNovoProgressoActivity.this);
                builder.setTitle(R.string.tituloErro);
                builder.setMessage(R.string.mensagemErro);
                builder.setPositiveButton(R.string.botaoErro, null);
                builder.show();
            }
        }
    };

    // Salva o progress na base de dados
    private void salvaProgresso(){
        DBAdapter databaseConnector = new DBAdapter(this);
        Bundle extras = getIntent().getExtras();
        long idLinha = extras.getLong(MainActivity.LINHA_ID);

        float pesoTemp = Float.parseFloat(txtPeso.getText().toString());
        float alturaTemp = Float.parseFloat(txtAltura.getText().toString());

        try{
            databaseConnector.open();
            databaseConnector.insereDesenvolvimento(
                    idLinha,
                    pesoTemp,
                    alturaTemp,
                    txtDtAtualizacao.getText().toString()
            );

            databaseConnector.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}

