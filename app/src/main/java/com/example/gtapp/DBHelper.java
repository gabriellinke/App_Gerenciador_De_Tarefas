package com.example.gtapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/** CLASSE QUE CRIA A BASE DO BANCO DE DADOS **/

public class DBHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "tarefas.db";
    private static final int VERSAO_BANCO  = 1;

    public DBHelper(@Nullable Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlConfiguracoes = "CREATE TABLE IF NOT EXISTS configuracoes (" +
                "_id INTEGER PRIMARY KEY," +
                "tarefa VARCHAR(255)," +
                "data VARCHAR(255)" +
                ");";

        db.execSQL(sqlConfiguracoes);

        this.onUpgrade(db, 1, VERSAO_BANCO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*switch (oldVersion)
        {
            case 1:
                faz att
            case 2:
                ...
            case 3:
                ...
        }*/

        String sqlTarefa = "CREATE TABLE IF NOT EXISTS tarefas (" +
                "_id INTEGER PRIMARY KEY," +
                "tarefa VARCHAR(255)," +
                "data VARCHAR(255)" +
                ");";

        db.execSQL(sqlTarefa);
    }
}
