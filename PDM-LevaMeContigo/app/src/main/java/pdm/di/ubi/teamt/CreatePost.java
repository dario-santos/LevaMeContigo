package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Publicacao;

public class CreatePost extends AppCompatActivity
{
    private DatabaseReference mDatabase = null;
    private FirebaseUser mFirebaseUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private boolean IsDataCorrect(String origem, String destino, String date, String horas, String numPessoas)
    {
        if(origem.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher uma origem.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(destino.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um destino.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(date.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher uma data.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(horas.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher uma hora.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(numPessoas.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um número de passageiros.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        Date dateToCompare = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String todayDate = dateFormatter.format(dateToCompare);

        if(DateUtil.getSignDateDifferenceInDays(todayDate, date) < 3)
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher uma data daqui a mais do que 3 dias.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void HandleAddPost(View v)
    {
        // Receber os objetos
        Spinner oOrigem = findViewById(R.id.createpost_origem);
        Spinner oDestino = findViewById(R.id.createpost_destino);
        EditText dataPicker = findViewById(R.id.createpost_datePicker);
        EditText oHoras= findViewById(R.id.createpost_hourPicker);
        Spinner oNumPessoas= findViewById(R.id.createpost_numpessoas);
        EditText oContrapartidas= findViewById(R.id.createpost_contrapartidas);

        // Extraír os dados
        String origem = oOrigem.getSelectedItem().toString();
        String destino = oDestino.getSelectedItem().toString();
        String date = dataPicker.getText().toString();
        String horas = oHoras.getText().toString();
        String numPessoas = oNumPessoas.getSelectedItem().toString();
        String contrapartidas = oContrapartidas.getText().toString();

        if(!IsDataCorrect(origem, destino, date, horas, numPessoas))
            return;

        if(mFirebaseUser == null)
            return;

        String idUser = mFirebaseUser.getUid();
        int numeroPessoas = Integer.parseInt(numPessoas);

        contrapartidas = contrapartidas.isEmpty() ? " " : contrapartidas;


        Publicacao newPub = new Publicacao(date, horas, origem, destino, numeroPessoas, contrapartidas, idUser);

        String key = mDatabase.child("posts").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Post/" + key, newPub.toMap());

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(CreatePost.this, "Publicação adicionada com sucesso.",
                Toast.LENGTH_SHORT).show();

        ReturnToMenu();
    }

    private void ReturnToMenu()
    {
        Intent intent = new Intent(this, Menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        ReturnToMenu();
    }

    public void HandleShowCalendar(View v)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final EditText dataPicker = findViewById(R.id.createpost_datePicker);

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

    public void HandleShowClock(View v)
    {
        TimePickerDialog picker;
        final EditText hourPicker = findViewById(R.id.createpost_hourPicker);

        final Calendar calendar = Calendar.getInstance();
        int hour    = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker tp, int chooseHour, int chooseMinute)
            {
                hourPicker.setText(String.format("%02d:%02d", chooseHour, chooseMinute));
            }
        }, hour, minutes, true);

        picker.show();
    }
}
