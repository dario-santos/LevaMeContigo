package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Inscrito;
import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class Post extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;
    private Publicacao pub = null;
    private String pubId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        pubId = intent.getStringExtra("idPub");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        GetPostFromDB(pubId);

        GetInscritoFromDB(mFirebaseUser.getUid());

        // Todo: O utilizador está inscrito?
    }

    private void GetUserFromDB(String idUser) {
        DatabaseReference myRef = mDatabase.child("User");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                for (DataSnapshot value : dataSnapshot.getChildren())
                {
                    User user = value.getValue(User.class);
                    TextView userName = findViewById(R.id.post_username);
                    userName.setText(user.getNome());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Query query = myRef.orderByKey().equalTo(idUser);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void GetPostFromDB(String idPost)
    {
        DatabaseReference myRef = mDatabase.child("Post");

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.exists())
                    return;

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    pub = value.getValue(Publicacao.class);
                    UpdateGUI(pub);
                    GetUserFromDB(pub.getIdUser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        Query query = myRef.orderByKey().equalTo(idPost);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void GetInscritoFromDB(final String idUser)
    {
        DatabaseReference myRef = mDatabase.child("Inscrito");

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.exists())
                    return;

                if(pub.getIdUser().equals(mFirebaseUser.getUid()))
                    return;

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Inscrito inscrito = value.getValue(Inscrito.class);

                    if(inscrito.getIdPub().equals(pubId))
                        return;
                }

                Button sendInvite = findViewById(R.id.post_action);
                sendInvite.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        Query query = myRef.orderByChild("idUser").equalTo(idUser);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void UpdateGUI(Publicacao pub)
    {
        if(pub == null)
            return;

        // Ir buscar as caixas
        TextView oOrigemDestino = findViewById(R.id.post_origemdestino);
        TextView oData= findViewById(R.id.post_data);
        TextView oHora = findViewById(R.id.post_time);
        TextView oNumPessoas = findViewById(R.id.post_numeropassageiros);
        TextView oContrapartidas = findViewById(R.id.post_contrapartidas);

        oOrigemDestino.setText(pub.getOrigem() + " -> " + pub.getDestino());
        oData.setText("Dia: " + pub.getData());
        oHora.setText("Horas: " + pub.getHora());
        oNumPessoas.setText("Número de passageiros: " + pub.getNumPassageiros());


        String tmp = pub.getContrapartidas().isEmpty() ? "Nenhuma" : pub.getContrapartidas();
        oContrapartidas.setText("Contrapartidas: " + tmp);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void HandleEnterProfile(View v)
    {
        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", pub.getIdUser());
        startActivity(intent);
    }

    public void HandleInscrito(View v)
    {
        v.setVisibility(View.INVISIBLE);

        Inscrito newPost = new Inscrito(mFirebaseUser.getUid(), pubId);

        String key = mDatabase.child("Inscrito").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Inscrito/" + key, newPost.toMap());

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(Post.this, "Pedido enviado",
                Toast.LENGTH_SHORT).show();
    }
}
