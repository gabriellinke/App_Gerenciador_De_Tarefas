package com.example.gtapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        definirAlarme();

        FloatingActionButton insertButton = (FloatingActionButton) findViewById(R.id.insert_button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), InsertActivity.class));

            }
        });

        //AO DAR UM CLIQUE LONGO EM UM ITEM DA LISTA, ABRE UMA JANELA COM A OPÇÃO DE EDITAR OU FINALIZAR A TAREFA
        final ListView lv = (ListView) findViewById(R.id.ltvTarefas);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                TextView txvTarefa = (TextView) arg1.findViewById(R.id.txvTarefa);
                String tarefa = txvTarefa.getText().toString();
                TextView txvData = (TextView) arg1.findViewById(R.id.txvData);
                String data = txvData.getText().toString();

                exibirEscolha(tarefa, data);

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHelper db = new DBHelper(getBaseContext());
        SQLiteDatabase banco = db.getWritableDatabase();

        //CURSOR PARA PERCORRER O BANCO DE DADOS
        mCursor = banco.rawQuery("SELECT _id, tarefa, data FROM tarefas", null);

        //PEGA OS DADOS DA COLUNA DE TAREFA E DE DATA
        String[] from = {
                "tarefa",
                "data"
        };

        //COLOCA OS DADOS NAS TEXTVIEWS
        int[] to = {
                R.id.txvTarefa,
                R.id.txvData
        };

        //CRIA O ADAPTER COM OS VETORES from E to INSTANCIADOS ANTERIORMENTE
        MeuAdapter adapter = new MeuAdapter(getBaseContext(), R.layout.lista_tarefas, mCursor, from ,to ,0);

        //COLOCA OS DADOS DO ADAPTER NA LISTVIEW QUE MOSTRA O HISTÓRICO
        ListView ltvTarefas = (ListView)findViewById(R.id.ltvTarefas);
        ltvTarefas.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //FECHA O CURSOR
        mCursor.close();
    }

    //ADAPTER CUSTOMIZADO PARA MOSTRAR OS DADOS EM VERMELHO CASO A DATA DA TAREFA SEJA ANTERIOR À ATUAL
    public class MeuAdapter extends SimpleCursorAdapter {

        public MeuAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            TextView txvData   = (TextView) view.findViewById(R.id.txvData);

            String data = cursor.getString(2);

            //6 primeiros caracteres descarta, sobra dd/mm/aaaa
            //00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15
            //S  E  X  .  -  .  2  9  /  0  5  /  2  0  2  0

            int ano = Integer.parseInt(data.substring(12,16));
            int mes = (Integer.parseInt(data.substring(9,11)) - 1);
            int dia = Integer.parseInt(data.substring(6,8)) + 1;    // Faz +1 porque se o pela biblioteca do Calendar, se a data for a mesma que a de hoje, ele já consideraria a tarefa como atrasada

            //DATA DO BANCO DE DADOS
            Calendar user = new GregorianCalendar(ano, mes, dia);
            //DATA ATUAL
            Calendar now = new GregorianCalendar();

            //RETORNA TRUE SE A DATA INFORMADA FOR ANTERIOR A ATUAL
            if(user.before(now))
            {
                txvData.setTextColor(Color.RED);
            }
        }

        private void esvaziarTarefas() //UTILIZADO DURANTE O DESENVOLVIMENTO DA APLICAÇÃO
        {
            //ATUALIZAR BANCO DE DADOS
            Bundle bundle = new Bundle();
            bundle.putInt("id", 3);

            Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    //CRIA UMA CAIXA DE DIÁLOGO ONDE O USUÁRIO ESCOLHE SE QUER FINALIZAR OU EDITAR A TAREFA
    private void exibirEscolha(final String tarefa, final String data)
    {
        //CRIA A CAIXA DE DIÁLOGO
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setMessage(R.string.dialog_message);

        //FINALIZAR TAREFA
        msgbox.setPositiveButton(R.string.end_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ATUALIZAR BANCO DE DADOS
                Bundle bundle = new Bundle();
                bundle.putString("tarefa", tarefa);
                bundle.putInt("id", 4);

                Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "Tarefa finalizada", Toast.LENGTH_SHORT).show();
            }
        });

        //EDITAR TAREFA
        msgbox.setNegativeButton(R.string.edit_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putString("tarefa", tarefa);
                bundle.putString("data", data);

                Intent intent = new Intent(getBaseContext(), EditActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //MOSTRA A CAIXA DE DIÁLOGO NA TELA
        msgbox.show();
    }

    // USADO PARA CRIAR A NOTIFICAÇÃO
    private void definirAlarme()
    {
        Intent broadcastIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //DEFINE O HORÁRIO DA PRIMEIRA NOTIFICAÇÃO
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);

        long startUpTime = calendar.getTimeInMillis();

        //REPETE A NOTIFICAÇÃO A CADA 4h
        long repeat = 4*60*60*1000;

        if (System.currentTimeMillis() > startUpTime) {
            startUpTime = startUpTime + repeat;
        }

        if(alarmManager!=null)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startUpTime, repeat, actionIntent);
    }
}
