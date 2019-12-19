package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Search extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void HandleSearch(View v)
    {
        Spinner oOrigem = findViewById(R.id.search_origem);
        Spinner oDestino = findViewById(R.id.search_destino);
        EditText oDate = findViewById(R.id.search_datePicker);

        String origem = oOrigem.getSelectedItem().toString();
        String destino = oDestino.getSelectedItem().toString();
        String date = oDate.getText().toString();

        if(!date.isEmpty())
        {
            Date dateToCompare = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            String todayDate = dateFormatter.format(dateToCompare);

            if(DateUtil.getSignDateDifferenceInDays(todayDate, date) < 0)
            {
                Toast.makeText(Search.this, "Erro: não pode escolher uma data passada.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
        if(date.isEmpty())
        {
            Date dateToCompare = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            date = dateFormatter.format(dateToCompare);
        }

        Intent intent = new Intent(this, SearchResults.class);
        intent.putExtra("origem", origem);
        intent.putExtra("destino", destino);
        intent.putExtra("date", date);
        startActivity(intent);

    }

    public void HandleShowCalendar(View v)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final EditText dataPicker = findViewById(R.id.search_datePicker);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                month++;
                dataPicker.setText(String.format("%d-%02d-%02d", year, month, day));
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
}
