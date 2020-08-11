package com.example.gtapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

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

        Button calendarButton = (Button) findViewById(R.id.calendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        assert dados != null;
        tarefaAntiga = dados.getString("tarefa");
        dataAntiga = dados.getString("data");

        final EditText tarefaEdit = (EditText) findViewById(R.id.editInsira2);
        final TextView dataEdit = (TextView) findViewById(R.id.editInsiraData2);

        //DEFINE O TEXTO DA FORMA QUE ELE ESTAVA ANTES DO USUÁRIO CLICAR PRA EDITAR A TAREFA
        //PARA FACILITAR QUE SE FASSAM PEQUENAS MODIFICAÇÕES
        tarefaEdit.setText(tarefaAntiga);

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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView date = (TextView) findViewById(R.id.editInsiraData2);
        date.setText(currentDateString);
    }

    private void inserirTarefa()
    {
        //ADICIONAR A TAREFA AO BANCO DE DADOS
        atualizarBanco(tarefaAntiga, dataAntiga, tarefaNova, formatarData(dataNova));
        Toast.makeText(getApplicationContext(), "Tarefa editada", Toast.LENGTH_SHORT).show();
        finish();
    }

    String formatarData(String data)
    {
        String diaSemana = data.substring(0, 3).toUpperCase();
        String mesDia = (data.substring(data.indexOf(",")+2, data.lastIndexOf(",")));
        String ano = (data.substring(data.lastIndexOf(",")+2));
        String dia = getDia(mesDia);
        String mes = getMes(mesDia);


        Log.d("date", data);
        String dataFinal = diaSemana +" - "+ dia +"/"+ mes +"/"+  ano;
        Log.d("date", dataFinal);
        return dataFinal;
    }

    String getMes(String mesDia)
    {
        String mes = (mesDia.substring(0, mesDia.length() - 2)).trim();
        String finalMes = "00";

        switch (mes)
        {
            case "January":
                finalMes = "01";
                break;
            case "February":
                finalMes = "02";
                break;
            case "March":
                finalMes = "03";
                break;
            case "April":
                finalMes = "04";
                break;
            case "May":
                finalMes = "05";
                break;
            case "June":
                finalMes = "06";
                break;
            case "July":
                finalMes = "07";
                break;
            case "August":
                finalMes = "08";
                break;
            case "September":
                finalMes = "09";
                break;
            case "October":
                finalMes = "10";
                break;
            case "November":
                finalMes = "11";
                break;
            case "December":
                finalMes = "12  ";
                break;
            default:
                finalMes = "00";
        }

        return finalMes;
    }

    String getDia(String mesDia)
    {
        String finalDay = "0";
        int dia = Integer.parseInt((mesDia.substring(mesDia.length() - 2)).trim());
        if(dia < 10)
            finalDay = finalDay + dia;
        else
            finalDay = String.valueOf(dia);

        return finalDay;
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
}
