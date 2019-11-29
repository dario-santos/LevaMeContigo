package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Post extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private Publicacao pub = null;
    private String pubId = null;

    private ArrayList<Comentario> comentarios = new ArrayList<>();
    private ArrayList<Integer> idUsers = new ArrayList<>();

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
        ReadComentariosFromDB();
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
                if(pub.getIdUser().equals(mFirebaseUser.getUid()))
                {
                    if(canUserDeleteEditPost())
                    {
                        ImageButton delete = findViewById(R.id.post_delete);
                        ImageButton edit = findViewById(R.id.post_edit);
                        delete.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        Query query = myRef.orderByKey().equalTo(idPost);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private boolean canUserDeleteEditPost()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String secondDate = df.format(c);

        long differenceInDays = DateUtil.getDateDifferenceInDays(pub.getData(), secondDate);
        return differenceInDays >= 3;
    }

    private void GetInscritoFromDB(final String idUser)
    {
        DatabaseReference myRef = mDatabase.child("Inscrito");

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
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

    private void ReadComentariosFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.exists())
                    return;

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Comentario comentario = value.getValue(Comentario.class);
                    if(comentario.getIdPub().equals(pubId))
                    {
                        boolean isAdded = false;
                        for(Comentario c : comentarios)
                            if(c.equals(comentario))
                            {
                                isAdded = true;
                                break;
                            }

                        if(!isAdded)
                            comentarios.add(comentario);
                    }
                }

                ShowComentariosToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Comentario");
        postRef.addValueEventListener(valueEventListener);
    }

    private void ShowComentariosToUser()
    {
        LinearLayout oLL = findViewById(R.id.post_llsv);

        for(int i = 0 ; i < comentarios.size() ; i++)
        {
            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.comment_line, null);
            oCL1.setId(View.generateViewId());

            ImageView userProfile = oCL1.findViewById(R.id.comment_line_userprofile);
            userProfile.setClickable(true);
            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    HandleEnterProfile(view);
                }
            });
            userProfile.setId(View.generateViewId());
            idUsers.add(userProfile.getId());

            TextView nome = oCL1.findViewById(R.id.comment_line_username);
            nome.setText(comentarios.get(i).getUserName());
            nome.setId(View.generateViewId());

            TextView text = oCL1.findViewById(R.id.comment_line_text);
            text.setText(comentarios.get(i).getComentario());
            text.setId(View.generateViewId());

            oLL.addView(oCL1);
        }
    }

    private void DeleteComentariosFromDB()
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Comentario");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                for(DataSnapshot value : snapshot.getChildren())
                {
                    Comentario c = value.getValue(Comentario.class);
                    if(c.getIdPub().equals(pubId))
                        value.getRef().removeValue();
                }
                DeleteInscritosFromDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void DeleteInscritosFromDB()
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Inscrito");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                for(DataSnapshot value : snapshot.getChildren())
                {
                    Comentario c = value.getValue(Comentario.class);
                    if(c.getIdPub().equals(pubId))
                        value.getRef().removeValue();
                }
                ReturnToMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void ReturnToMenu()
    {
        DatabaseReference myRef = mDatabase.child("Post/"+ pubId);
        myRef.removeValue();

        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);

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

    public void HandleAddComment(View v)
    {
        Intent intent = new Intent(this, Comment.class);
        intent.putExtra("idPub", pubId);
        startActivity(intent);
    }

    public void HandleRateUser(View v)
    {
        Intent intent = new Intent(this, Rate.class);
        intent.putExtra("idUser", pub.getIdUser());
        intent.putExtra("idPub", pubId);
        startActivity(intent);
    }

    public void HandleDeletePost(View v)
    {
        DeleteComentariosFromDB();
    }
}
