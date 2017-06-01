package com.aula_android.appCrescimentoInfantil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    /*crianca*/
    public static final String KEY_NOME = "nome";
    public static final String KEY_SEXO = "sexo";
    public static final String KEY_NASCIMENTO = "nascimento";
    /*desenvolvimento*/
    public static final String KEY_ALTURA = "altura";
    public static final String KEY_PESO = "peso";
    public static final String KEY_ATUALIZACAO = "dtatualizacao";
    /**/
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "databse";
    private static final String DATABASE_TABLE_CRIANCA = "crianca";
    private static final String DATABASE_TABLE_DESENV  = "desenvolvimento_crianca";
    private static final int DATABASE_VERSION = 3;

    private static final String CRIA_TABELA_CRIANCA = "create table crianca " +
            "(_id integer primary key autoincrement, " +
            " nome text not null," +
            " sexo text not null," +
            " nascimento text not null);" ;

    private static final String CRIA_TABELA_DESENVOLVIMENTO = "create table desenvolvimento_crianca " +
            "(_id integer," +
            " dtatualizacao text not null, " +
            " peso real not null," +
            " altura real not null," +
            " primary key(_id,dtatualizacao));";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context); //classe interna que herda de SQLiteOpenHelper
    }

    //classe interna que manipula o banco
    //SQLiteOpenHelper é uma classe abstrata.
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                db.execSQL(CRIA_TABELA_CRIANCA);
                db.execSQL(CRIA_TABELA_DESENVOLVIMENTO);
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
            Log.w(TAG, "Atualizando a base de dados a partir da versao " + oldVersion
                    + " para " + newVersion + ",isso irá destruir todos os dados antigos");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CRIANCA);
            onCreate(db);
        }
    }

    // *******************************************************************************
    //--- abre a base de dados ---
    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //--- fecha a base de dados ---
    public void close(){
        DBHelper.close();
    }

    //---insere um Livro na base da dados ---
    public long insereCrianca(String nome, String sexo, String dtnascimento){
        ContentValues dados = new ContentValues();
        dados.put(KEY_NOME, nome);
        dados.put(KEY_SEXO, sexo);
        dados.put(KEY_NASCIMENTO, dtnascimento);
        return db.insert(DATABASE_TABLE_CRIANCA, null, dados);
    }

    public long insereDesenvolvimento(long pid, float altura, float peso, String dtAtualizacao){
        ContentValues dados = new ContentValues();
        dados.put(KEY_ROWID, pid);
        dados.put(KEY_PESO, altura);
        dados.put(KEY_ALTURA, peso);
        dados.put(KEY_ATUALIZACAO, dtAtualizacao);
        return db.insert(DATABASE_TABLE_DESENV, null, dados);
    }

    //--- exclui o desenvolvimento ---
    public boolean excluiDesenvolvimento(long idLinha){
        return db.delete(DATABASE_TABLE_DESENV, KEY_ROWID + "=" + idLinha, null) > 0;
    }
    //--- exclui uma crianca ---
    public boolean excluiCrianca(long idLinha){
        return db.delete(DATABASE_TABLE_CRIANCA, KEY_ROWID + "=" + idLinha, null) > 0;
    }
    public boolean excluiTodasCriancas(ArrayList<Integer> idLinha){

        for (int i=0;i<idLinha.size();i++){
            db.delete(DATABASE_TABLE_CRIANCA, KEY_ROWID + "=" + idLinha.get(i), null);
        }
        return true;
    }

    //--- devolve o progresso---
    public Cursor getProgresso(long idLinha){
        String colunas[] ={KEY_ROWID,KEY_ATUALIZACAO,KEY_PESO,KEY_ALTURA};
        String whereClause = "_id = " + String.valueOf(idLinha);

        return db.query(DATABASE_TABLE_DESENV,colunas, whereClause, null, null, null, null);
    }

    //--- devolve todos os clientes---
    public Cursor getTodosCriancasByName(String nomeAux){
        String colunas[] ={KEY_ROWID,KEY_NOME,KEY_SEXO,KEY_NASCIMENTO};
        String whereClause = "nome like '%" + nomeAux + "%'";

        return db.query(DATABASE_TABLE_CRIANCA,colunas, whereClause, null, null, null, null);
    }

    public Cursor getTodasCriancas(){
        String colunas[] ={KEY_ROWID,KEY_NOME,KEY_SEXO,KEY_NASCIMENTO};
        return db.query(DATABASE_TABLE_CRIANCA,colunas, null, null, null, null, null);
    }

    //--- recupera uma linha (livro) ---
    public Cursor getCrianca(long idLinha) throws SQLException{

        String colunas[] ={KEY_ROWID,KEY_NOME,KEY_SEXO,KEY_NASCIMENTO};
        String linhaAcessada = KEY_ROWID + "=" + idLinha;
        Cursor mCursor = db.query(DATABASE_TABLE_CRIANCA, colunas,linhaAcessada,null,null,null,null,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean alteraCrianca(long idLinha, String nome, String sexo,String dtnascimento){
        ContentValues dados = new ContentValues();
        String linhaAcessada = KEY_ROWID + "=" + idLinha;

        dados.put(KEY_NOME, nome);
        dados.put(KEY_SEXO, sexo);
        dados.put(KEY_NASCIMENTO, dtnascimento);

        return db.update(DATABASE_TABLE_CRIANCA, dados, linhaAcessada, null)>0;
    }
}