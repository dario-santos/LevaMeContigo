package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Publicacao;

public class CreatePost extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private boolean VerifyData(String origem, String destino, String dia, String mes, String ano
            , String horas, String minutos, String numPessoas)
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
        else if(dia.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um dia.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mes.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um mes.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(ano.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um ano.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(horas.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher uma hora.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(minutos.isEmpty())
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher os minutos.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(numPessoas.isEmpty() )
        {
            Toast.makeText(CreatePost.this, "Erro: necessita de escolher um número de passageiros.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void HandleAddPost(View v)
    {
        // Receber os objetos
        Spinner oOrigem = findViewById(R.id.createpost_origem);
        Spinner oDestino = findViewById(R.id.createpost_destino);
        Spinner oDia = findViewById(R.id.createpost_day);
        Spinner oMes = findViewById(R.id.createpost_month);
        Spinner oAno = findViewById(R.id.createpost_year);
        Spinner oHoras= findViewById(R.id.createpost_hour);
        Spinner oMinutos = findViewById(R.id.createpost_minutes);
        Spinner oNumPessoas= findViewById(R.id.createpost_numpessoas);
        EditText oContrapartidas= findViewById(R.id.createpost_contrapartidas);

        // Extraír os dados
        String origem = oOrigem.getSelectedItem().toString();
        String destino = oDestino.getSelectedItem().toString();
        String dia = oDia.getSelectedItem().toString();
        String mes = oMes.getSelectedItem().toString();
        String ano = oAno.getSelectedItem().toString();
        String horas = oHoras.getSelectedItem().toString();
        String minutos = oMinutos.getSelectedItem().toString();
        String numPessoas = oNumPessoas.getSelectedItem().toString();
        String contrapartidas = oContrapartidas.getText().toString();

        // Verificar os dados
        if(!VerifyData(origem, destino, dia, mes, ano, horas, minutos, numPessoas))
            return;

        if(mFirebaseUser == null)
            return;

        String idUser = mFirebaseUser.getUid();

        String date = ano + "-" + mes + "-" + dia;
        String hours = horas + ":" + minutos;
        int numeroPessoas = Integer.parseInt(numPessoas);

        contrapartidas = contrapartidas.isEmpty() ? " " : contrapartidas;


        Publicacao newPost = new Publicacao(date, hours, origem, destino, numeroPessoas, contrapartidas, idUser);

        String key = mDatabase.child("posts").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Post/" + key, newPost.toMap());

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(CreatePost.this, "Publicação adicionada com sucesso.",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
