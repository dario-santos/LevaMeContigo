package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private DatabaseReference mDatabase = null;
    private FirebaseUser mFirebaseUser = null;

    private String idPub = null;
    private User user = null;

    private ArrayList<Publicacao> publicacoes = new ArrayList<>();
    private ArrayList<String> publicacoesKeys = new ArrayList<>();
    private ArrayList<String> inscricoes = new ArrayList<>();

    private int numberOfBoleias = 0;
    private boolean pubExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DoesPubExist();
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

    private void DoesPubExist()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                pubExist = false;

                for(DataSnapshot value : dataSnapshot.getChildren())
                    if(value.getKey().equals(idPub))
                        pubExist = true;

                GetSuccessfulInscricoesFromDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByKey().equalTo(idPub).addValueEventListener(valueEventListener);
    }

    private void GetSuccessfulInscricoesFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(inscricoes.contains(value.getKey()))
                        continue;

                    Inscrito inscrito = value.getValue(Inscrito.class);

                    if(publicacoes.contains(inscrito.getIdPub()))
                        inscricoes.add(value.getKey());
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

    private void GetPubsFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(publicacoesKeys.contains(value.getKey()))
                        continue;

                    Publicacao pub = value.getValue(Publicacao.class);
                    publicacoesKeys.add(value.getKey());
                    publicacoes.add(pub);
                }
                GetSuccessfulInscricoesFromDB();
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

    private void AddCommentarioToDB(String idPub, String idUser, String userName)
    {
        EditText oComentario = findViewById(R.id.comment_text);
        String text = oComentario.getText().toString();

        if(text.isEmpty())
        {
            Toast.makeText(Comment.this, "Erro: Necessita de introduzir um comentário.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(numberOfBoleias <= 33) // Pelo menos
        {
            Toast.makeText(Comment.this, "Erro: Necessita de ter participado ou realizado 33 boleias",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(!pubExist)
        {
            Toast.makeText(Comment.this, "Erro: A publicação em questão foi eliminada.",
                    Toast.LENGTH_SHORT).show();

            ReturnToMenu();
            return;
        }

        Comentario comment = new Comentario(idUser, userName, idPub, text);

        String key = mDatabase.child("comentario").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Comentario/" + key, comment.toMap());

        mDatabase.updateChildren(childUpdates);
    }

    private void ReturnToMenu()
    {
        Intent intent = new Intent(this, Menu.class);

        startActivity(intent);
    }
    private void ReturnToPost(String idPub)
    {
        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void HandleAddComment(View v)
    {
        AddCommentarioToDB(idPub, mFirebaseUser.getUid(), user.getNome());

        ReturnToPost(idPub);
    }

    public void HandleBack(View v)
    {
        ReturnToPost(idPub);
    }
}
