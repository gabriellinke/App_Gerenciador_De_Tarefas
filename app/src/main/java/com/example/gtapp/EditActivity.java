package com.example.gtapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {

    private String tarefaNova;
    private String dataNova;
    private String tarefaAntiga;
    private String dataAntiga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();  //RECUPERA DADOS DA ACTIVITY ANTERIOR

        assert dados != null;
        tarefaAntiga = dados.getString("tarefa");
        dataAntiga = dados.getString("data");

        final EditText tarefaEdit = (EditText) findViewById(R.id.editInsira2);
        final EditText dataEdit = (EditText) findViewById(R.id.editInsiraData2);

        //DEFINE O TEXTO DA FORMA QUE ELE ESTAVA ANTES DO USUÁRIO CLICAR PRA EDITAR A TAREFA
        //PARA FACILITAR QUE SE FASSAM PEQUENAS MODIFICAÇÕES
        tarefaEdit.setText(tarefaAntiga);
        assert dataAntiga != null;
        dataEdit.setText(dataAntiga.substring(6));

        Button editar = (Button) findViewById(R.id.edit_button);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tarefaNova = tarefaEdit.getText().toString();
                dataNova = dataEdit.getText().toString();

                inserirTarefa();
            }
        });
    }


    private void inserirTarefa()
    {
        //VERIFICA O FORMATO DA DATA
        int[] dataInt = verificaData(dataNova);
        //VERIFICA QUAL É O DIA DA SEMANA DA DATA
        String dataBD = getDiaSemana(dataInt);
        //VERIFICA SE A STRING DA DATA ESTÁ BEM FORMADA PARA SER INCLUIDA NO BANCO DE DADOS
        dataBD = verificarStringData(dataInt, dataBD);
        //ADICIONAR A TAREFA AO BANCO DE DADOS
        atualizarBanco(tarefaAntiga, dataAntiga, tarefaNova, dataBD);
        Toast.makeText(getApplicationContext(), "Tarefa editada", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void esvaziarTarefas() //UTILIZADO DURANTE O DESENVOLVIMENTO DA APLICAÇÃO
    {
        //ESVAZIAR O BANCO DE DADOS
        Bundle bundle = new Bundle();
        bundle.putInt("id", 3);

        Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void atualizarBanco(String tarefa, String data, String tarefaNova, String dataNova)
    {
        Log.d("edit", tarefaNova);
        Log.d("edit", dataNova);

        //ATUALIZAR BANCO DE DADOS
        //PASSANDO ID 2 VAI SUBSTITUIR A TAREFA E A DATA
        Bundle bundle = new Bundle();
        bundle.putString("tarefa", tarefa);
        bundle.putString("data", data);
        bundle.putString("tarefaNova", tarefaNova);
        bundle.putString("dataNova", dataNova);
        bundle.putInt("id", 2);

        Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private String getDiaSemana(int[] dataInt)
    {
        Calendar dataTarefa = new GregorianCalendar(dataInt[2], dataInt[1]-1, dataInt[0]);
        int dds = dataTarefa.get(GregorianCalendar.DAY_OF_WEEK);

        String dSemana;

        switch(dds)
        {
            case 1:
                dSemana = "DOM - ";
                break;
            case 2:
                dSemana = "SEG - ";
                break;
            case 3:
                dSemana = "TER - ";
                break;
            case 4:
                dSemana = "QUA - ";
                break;
            case 5:
                dSemana = "QUI - ";
                break;
            case 6:
                dSemana = "SEX - ";
                break;
            case 7:
                dSemana = "SAB - ";
                break;
            default:
                dSemana = "ERRO- ";
        }

        return dSemana;
    }

    private int[] verificaData(String data)
    {
        int[] dataInt = new int[3];

        //VERIFICA SE A DATA ESTÁ NO FORMATO CERTO
        try{
            if(!data.contains("/")) {
                int erro = Integer.parseInt("/"); //PARA FORÇAR UM THROW SE O USUÁRIO DIGITAR QUALQUER COISA QUE NÃO CONTENHA BARRA
            }

            dataInt[0] = Integer.parseInt(data.substring(0 ,data.indexOf("/")));
            dataInt[1] = Integer.parseInt(data.substring(data.indexOf("/")+1, data.lastIndexOf("/")));
            dataInt[2] = Integer.parseInt(data.substring(data.lastIndexOf("/")+1));

            if(dataInt[2] < 2000) {
                int erro = Integer.parseInt("/"); //PARA FORÇAR UM THROW CASO O USUÁRIO TENTE DIGITAR UMA DATA MUITO ANTIGA - TAMBÉM PARA EVITAR QUE A DATA TENHA MENOS DE 4 DÍGITOS, O QUE CAUSARIA UM ERRO NA LEITURA DO BANCO DE DADOS
            }

        }catch(NumberFormatException e){
            //VALOR DEFAULT PARA A DATA CASO ESTEJA NO FORMATO ERRADO
            dataInt[0] = 1;
            dataInt[1] = 1;
            dataInt[2] = 2000;

            Toast.makeText(getApplicationContext(), "Data inválida", Toast.LENGTH_SHORT).show();
            return dataInt;
        }

        return dataInt;
    }

    private String verificarStringData(int[] dataInt, String dataBD)
    {
        //VERIFICA SE O DIA É MENOR QUE 31 E O MES MENOR QUE 12. ADICIONA UM 0 NA STRING SE O NÚMERO DO DIA OU MÊS FOR MENOR QUE 10.
        //UMA MELHOR VERIFICAÇÃO PARA O DIA SERIA FEITA SE FOSSE BASEADA NA QUANTIDADE DE DIAS DO MÊS, ALÉM DE LEVAR EM CONTA SE O ANO É BISSEXTO, PORÉM, ACREDITO QUE ISSO SEJA DESNECESSÁRIO

        if (dataInt[0] > 31) {
            Toast.makeText(getApplicationContext(), "Data inválida", Toast.LENGTH_SHORT).show();
            dataBD = dataBD + 31 + "/";
        } else if (dataInt[0] < 10)
            dataBD = dataBD + "0" + dataInt[0] + "/";
        else
            dataBD = dataBD + dataInt[0] + "/";

        if (dataInt[1] > 12) {
            Toast.makeText(getApplicationContext(), "Data inválida", Toast.LENGTH_SHORT).show();
            dataBD = dataBD + 12 + "/";
        } else if (dataInt[1] < 10)
            dataBD = dataBD + "0" + dataInt[1] + "/";
        else
            dataBD = dataBD + dataInt[1] + "/";

        dataBD = dataBD + dataInt[2];

        return dataBD;
    }
}