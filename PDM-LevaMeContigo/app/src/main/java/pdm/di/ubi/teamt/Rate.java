package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.User;

public class Rate extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private User user = null;
    private String idUserAvaliado = null;
    private String idPub = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Intent intent = getIntent();
        idUserAvaliado = intent.getStringExtra("idUserAvaliado");
        idPub = intent.getStringExtra("idPub");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser =  FirebaseAuth.getInstance().getCurrentUser();

        GetUserFromDB(mFirebaseUser.getUid());
    }

    private void GetUserFromDB(String idUser)
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot value : dataSnapshot.getChildren())
                    user = value.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {}
        };

        DatabaseReference myRef = mDatabase.child("User");
        myRef.orderByKey().equalTo(idUser).addListenerForSingleValueEvent(valueEventListener);
    }


    private void AddRateToDB(String idUserAvaliador, String nomeAvaliador, String idUserAvaliado)
    {
        EditText oComentario = findViewById(R.id.rate_comment);
        String text = oComentario.getText().toString();

        if(text.isEmpty())
        {
            text = " ";
        }

        RatingBar rating = findViewById(R.id.rate_rating);
        float nota = rating.getRating();

        Avaliar avaliar = new Avaliar(idUserAvaliador, nomeAvaliador, idUserAvaliado, idPub, text, nota);

        String key = mDatabase.child("avaliar").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Avaliar/" + key, avaliar.toMap());

        mDatabase.updateChildren(childUpdates);
    }

    public void HandleAddRate(View v)
    {
        AddRateToDB(mFirebaseUser.getUid(), user.getNome(), idUserAvaliado);

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
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
