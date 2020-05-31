package com.example.gtapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class CursoresActivity extends AppCompatActivity
{

    private Cursor mCursor;
    private int id;
    private String data;
    private String tarefa;
    private String dataNova;
    private String tarefaNova;

    @Override
    protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();  //RECUPERA DADOS DA ACTIVITY ANTERIOR

        assert dados != null;
        id = dados.getInt("id");
        tarefa = dados.getString("tarefa");
        data = dados.getString("data");
        tarefaNova = dados.getString("tarefaNova");
        dataNova = dados.getString("dataNova");

        switch (id) //REALIZA ALGUMA AÇÃO BASEADA NO ID RECEBIDO
        {
            case 1:
                criarRegistros();
                break;
            case 2:
                atualizarRegistro();
                break;
            case 3:
                deletarRegistros();
                break;
            case 4:
                removerRegistro();
                break;
        }

    }

    public void criarRegistros() {

        DBHelper db = new DBHelper(getBaseContext());
        SQLiteDatabase banco = db.getWritableDatabase();    //CRIA UM BANCO

        ContentValues ctv = new ContentValues();

        ctv.put("tarefa", tarefa);     //ARMAZENA VALORES EM UM CONTENTVALUES
        ctv.put("data", data);

        banco.insert("tarefas", null, ctv); //BOTA OS DADOS NA TABELA tarefas
        finish();
    }


    public void atualizarRegistro()
    {
        DBHelper db = new DBHelper(getBaseContext());       //CRIA UM BANCO
        SQLiteDatabase banco = db.getWritableDatabase();

        ContentValues ctv = new ContentValues();
        ctv.put("tarefa", tarefaNova);
        ctv.put("data", dataNova);

        //ATUALIZA O A TAREFA ANTIGA COM A NOVA TAREFA E A NOVA DATA
        String [] args = {tarefa};
        banco.update("tarefas", ctv, "tarefa=?", args);

        finish(); //FINALIZA A ACTIVITY
    }

    public void removerRegistro()
    {
        DBHelper db = new DBHelper(getBaseContext());       //CRIA UM BANCO
        SQLiteDatabase banco = db.getWritableDatabase();

        //DELETA A TAREFA QUE FOI PASSADA
        String [] args = {tarefa};
        banco.delete("tarefas", "tarefa=?", args);

        finish(); //FINALIZA A ACTIVITY
    }

    private void deletarRegistros()
    {
        DBHelper db = new DBHelper(getBaseContext());       //CRIA UM BANCO
        SQLiteDatabase banco = db.getWritableDatabase();

        mCursor = banco.rawQuery("SELECT _id, tarefa, data FROM tarefas", null);
        ContentValues ctv;

        //DELETA OS REGISTROS
        if(mCursor.moveToFirst())
        {
            do{

                banco.delete("tarefas", "_id = "+ mCursor.getString(0), null);

                Log.d("Cursor: ", mCursor.getString(2));

            } while(mCursor.moveToNext());
        }
        else
            Log.d("Cursor: ", "SEM REGISTROS");


        if(!mCursor.isClosed())
            mCursor.close();
        finish(); //FINALIZA A ACTIVITY
    }
}
