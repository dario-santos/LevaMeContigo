package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class EditPost extends AppCompatActivity
{

    private DatabaseReference mDatabase = null;
    private FirebaseUser mFirebaseUser = null;

    private String idPub = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser =  FirebaseAuth.getInstance().getCurrentUser();

        GetPubFromDB();
    }

    private void GetPubFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.exists())
                    return;

                Publicacao pub = null;
                for (DataSnapshot value : dataSnapshot.getChildren())
                    pub = value.getValue(Publicacao.class);

                UpdateUI(pub);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference myRef = mDatabase.child("Post");
        myRef.orderByKey().equalTo(idPub).addListenerForSingleValueEvent(valueEventListener);
    }

    private void UpdateUI(Publicacao pub)
    {
        // Receber os objetos
        Spinner oOrigem = findViewById(R.id.editpost_origem);
        Spinner oDestino = findViewById(R.id.editpost_destino);
        Spinner oNumPessoas= findViewById(R.id.editpost_numpessoas);
        EditText oContrapartidas= findViewById(R.id.editpost_contrapartidas);

        List<String> cities = Arrays.asList(getResources().getStringArray(R.array.destinos_origens));
        oOrigem.setSelection(cities.indexOf(pub.getOrigem()));
        oDestino.setSelection(cities.indexOf(pub.getDestino()));
        oContrapartidas.setText(pub.getContrapartidas());

        List<String> nPessoas = Arrays.asList(getResources().getStringArray(R.array.numPessoas));
        oNumPessoas.setSelection(nPessoas.indexOf(String.valueOf(pub.getNumPassageiros())));
    }

    public void HandleUpdatePost(View v)
    {
        // Receber os objetos
        Spinner oOrigem = findViewById(R.id.editpost_origem);
        Spinner oDestino = findViewById(R.id.editpost_destino);
        EditText dataPicker = findViewById(R.id.editpost_datePicker);
        EditText oHoras= findViewById(R.id.editpost_hourPicker);
        Spinner oNumPessoas= findViewById(R.id.editpost_numpessoas);
        EditText oContrapartidas= findViewById(R.id.editpost_contrapartidas);

        // Extraír os dados
        String origem = oOrigem.getSelectedItem().toString();
        String destino = oDestino.getSelectedItem().toString();
        String date = dataPicker.getText().toString();
        String horas = oHoras.getText().toString();
        String numPessoas = oNumPessoas.getSelectedItem().toString();
        String contrapartidas = oContrapartidas.getText().toString();
        int numeroPessoas = Integer.parseInt(numPessoas);

        if(!date.isEmpty())
        {
            Date dateToCompare = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            String todayDate = dateFormatter.format(dateToCompare);

            if(DateUtil.getSignDateDifferenceInDays(todayDate, date) < 3)
            {
                Toast.makeText(EditPost.this, "Erro: necessita de escolher uma data daqui a mais do que 3 dias.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(!origem.isEmpty())
            mDatabase.child("Post/" + idPub).child("origem").setValue(origem);
        if(!destino.isEmpty())
            mDatabase.child("Post/" + idPub).child("destino").setValue(destino);
        if(!date.isEmpty())
            mDatabase.child("Post/" + idPub).child("data").setValue(date);
        if(!horas.isEmpty())
            mDatabase.child("Post/" + idPub).child("hora").setValue(horas);
        if(!numPessoas.isEmpty())
            mDatabase.child("Post/" + idPub).child("numPassageiros").setValue(numeroPessoas);
        if(!contrapartidas.isEmpty())
            mDatabase.child("Post/" + idPub).child("contrapartidas").setValue(contrapartidas);

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

        final EditText dataPicker = findViewById(R.id.editpost_datePicker);

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
        final EditText hourPicker = findViewById(R.id.editpost_hourPicker);

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
