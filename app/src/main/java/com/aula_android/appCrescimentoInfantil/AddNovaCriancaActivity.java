package com.aula_android.appCrescimentoInfantil;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNovaCriancaActivity extends AppCompatActivity {
    private long idLinha;
    private EditText txtNome;
    private Spinner spiSexo;
    private EditText txtDtNascimento;

    /**/
    private DatePicker   datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private Button btnSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nova_crianca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras() == null) {
            getSupportActionBar().setTitle("Adicionar um novo registro");
        }
        else
            getSupportActionBar().setTitle("Modificar um registro");

        txtNome = (EditText) findViewById(R.id.txtNome);
        spiSexo = (Spinner) findViewById(R.id.spiSexo);
        txtDtNascimento = (EditText) findViewById(R.id.txtDtNascimento);

        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(salvarCriancaButtonClicked);

        initDateDialog();
    }

    private void initDateDialog(){
        dateView = (TextView) findViewById(R.id.txtDtNascimento);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
    }

    View.OnClickListener salvarCriancaButtonClicked = new View.OnClickListener(){
        public void onClick(View v){
            if (txtNome.getText().length() != 0){
                AsyncTask<Object, Object, Object> salvaClienteTask = new AsyncTask<Object, Object, Object>(){
                    @Override
                    protected Object doInBackground(Object... params){
                        salvaCliente(); // Salva o livro na base de dados
                        return null;
                    } // end method doInBackground

                    @Override
                    protected void onPostExecute(Object result){
                        finish(); // Fecha a atividade
                    }
                };

                // Salva o livro no BD usando uma thread separada
                salvaClienteTask.execute();
            } // end if
            else {
                // Cria uma caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNovaCriancaActivity.this);
                builder.setTitle(R.string.tituloErro);
                builder.setMessage(R.string.mensagemErro);
                builder.setPositiveButton(R.string.botaoErro, null);
                builder.show();
            }
        }
    };

    // Salva o livro na base de dados
    private void salvaCliente(){
        DBAdapter databaseConnector = new DBAdapter(this);
        try{
            databaseConnector.open();
            if (getIntent().getExtras() == null){
                databaseConnector.insereCrianca(
                        txtNome.getText().toString(),
                        spiSexo.getSelectedItem().toString(),
                        txtDtNascimento.getText().toString());
            }
            else{
                databaseConnector.alteraCrianca(idLinha,
                        txtNome.getText().toString(),
                        spiSexo.getSelectedItem().toString(),
                        txtDtNascimento.getText().toString());
            }
            databaseConnector.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
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
}
