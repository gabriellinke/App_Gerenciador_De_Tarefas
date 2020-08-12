package com.example.gtapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.transform.dom.DOMLocator;

public class InsertActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        Button inserir = (Button) findViewById(R.id.add_button);

        Button calendarButton = (Button) findViewById(R.id.calendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tarefaEdit = (EditText) findViewById(R.id.editInsira);
                TextView date = (TextView) findViewById(R.id.editInsiraData);
                String tarefa = tarefaEdit.getText().toString();
                String data = date.getText().toString();

                inserirTarefa(tarefa, formatarData(data));
            }
        });

    }

    String formatarData(String data)
    {
        String diaSemana = data.substring(0, 3).toUpperCase();
        String mesDia = (data.substring(data.indexOf(",")+2, data.lastIndexOf(",")));
        String ano = (data.substring(data.lastIndexOf(",")+2));
        String dia = getDia(mesDia);
        String mes = getMes(mesDia);

        String dataFinal = diaSemana +" - "+ dia +"/"+ mes +"/"+  ano;
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView date = (TextView) findViewById(R.id.editInsiraData);
        date.setText(currentDateString);
    }

    private void inserirTarefa(String tarefa, String data)
    {
        //ADICIONAR A TAREFA AO BANCO DE DADOS
        atualizarBanco(tarefa, data);
        Toast.makeText(getApplicationContext(), "Tarefa adicionada", Toast.LENGTH_SHORT).show();
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

    private void atualizarBanco(String tarefa, String data)
    {
        //ATUALIZAR BANCO DE DADOS
        Bundle bundle = new Bundle();
        bundle.putString("tarefa", tarefa);
        bundle.putString("data", data);
        bundle.putInt("id", 1);

        Intent intent = new Intent(getBaseContext(), CursoresActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
