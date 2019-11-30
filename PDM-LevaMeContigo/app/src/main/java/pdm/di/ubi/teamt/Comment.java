package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.Inscrito;
import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class Comment extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private User user = null;
    private String idPub = null;

    private ArrayList<Publicacao> publicacoes = new ArrayList<>();
    private ArrayList<Inscrito> inscricoes = new ArrayList<>();

    private int numberOfBoleias = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

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

                GetPubsFromDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        };

        DatabaseReference myRef = mDatabase.child("User");
        myRef.orderByKey().equalTo(idUser).addListenerForSingleValueEvent(valueEventListener);
    }

    private void GetPubsFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao pub = value.getValue(Publicacao.class);
                    publicacoes.add(pub);
                }
                GetSuccessfulInscritoFromDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String s = df.format(c);

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").endAt(s).addValueEventListener(valueEventListener);
    }

    private void GetSuccessfulInscritoFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Inscrito inscrito = value.getValue(Inscrito.class);
                    if(publicacoes.contains(inscrito.getIdPub()))
                        inscricoes.add(inscrito);
                }

                CalculateNumberOfBoleias();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Inscrito");
        postRef.orderByChild("idUser").equalTo(mFirebaseUser.getUid()).addValueEventListener(valueEventListener);
    }

    private void CalculateNumberOfBoleias()
    {
        numberOfBoleias = inscricoes.size();

        for(Publicacao p : publicacoes)
            if(p.getIdUser().equals(mFirebaseUser.getUid()))
                numberOfBoleias++;
        ImageButton b = findViewById(R.id.comment_comment);
        b.setClickable(true);
    }


    private void AddCommmentToDB(String idPub, String idUser, String userName)
    {
        EditText oComentario = findViewById(R.id.comment_text);
        String text = oComentario.getText().toString();

        if(text.isEmpty())
        {
            Toast.makeText(Comment.this, "Erro: Necessita de introduzir um comentário",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(numberOfBoleias < 33)
        {
            Toast.makeText(Comment.this, "Erro: Necessita de ter participado ou realizado 33 boleias para poder comentar",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Comentario comment = new Comentario(idUser, userName, idPub, text);

        String key = mDatabase.child("comentario").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Comentario/" + key, comment.toMap());

        mDatabase.updateChildren(childUpdates);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void HandleAddComment(View v)
    {
        AddCommmentToDB(idPub, mFirebaseUser.getUid(), user.getNome());

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
