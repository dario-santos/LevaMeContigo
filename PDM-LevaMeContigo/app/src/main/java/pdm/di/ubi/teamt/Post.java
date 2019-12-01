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

import com.google.android.gms.common.util.DataUtils;
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

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.Inscrito;
import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class Post extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private Publicacao pub = null;
    private String idPub = null;

    private ArrayList<Comentario> comentarios = new ArrayList<>();

    private ArrayList<Integer> buttonsIdUsers = new ArrayList<>();

    private String inscritoKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser =  FirebaseAuth.getInstance().getCurrentUser();

        GetPostFromDB(idPub);

        GetComentariosFromDB();
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
                    GetAvaliarFromDB();
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

                GetInscritoFromDB(mFirebaseUser.getUid());
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
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Inscrito inscrito = value.getValue(Inscrito.class);

                    if(inscrito.getIdPub().equals(idPub))
                    {
                        inscritoKey = value.getKey();

                        Button cancelarConvite = findViewById(R.id.post_cancelarconvite);
                        cancelarConvite.setVisibility(View.VISIBLE);
                        return;
                    }
                }

                Button sendInvite = findViewById(R.id.post_action);
                sendInvite.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String dateToday = df.format(c);

        if(DateUtil.getSignDateDifferenceInDays(dateToday, pub.getData()) < 0)
            return;

        if(pub.getIdUser().equals(mFirebaseUser.getUid()))
            return;

        DatabaseReference myRef = mDatabase.child("Inscrito");
        myRef.orderByChild("idUser").equalTo(idUser).addListenerForSingleValueEvent(valueEventListener);
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

    private void GetComentariosFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                comentarios.clear();

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Comentario comentario = value.getValue(Comentario.class);

                    if(comentario.getIdPub().equals(idPub))
                        comentarios.add(comentario);
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
        oLL.removeAllViews();

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
                    HandleEnterCommentProfile(view);
                }
            });
            userProfile.setId(View.generateViewId());
            buttonsIdUsers.add(userProfile.getId());

            TextView nome = oCL1.findViewById(R.id.comment_line_username);
            nome.setText(comentarios.get(i).getUserName());
            nome.setId(View.generateViewId());

            TextView text = oCL1.findViewById(R.id.comment_line_text);
            text.setText(comentarios.get(i).getComentario());
            text.setId(View.generateViewId());

            oLL.addView(oCL1);
        }
    }

    private void GetAvaliarFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Avaliar avaliar = value.getValue(Avaliar.class);

                    if(avaliar.getIdPub() != idPub)
                        continue;

                    if(avaliar.getIdUserAvaliado().equals(pub.getIdUser()))
                    {
                        Button sendRate = findViewById(R.id.post_avaliar);
                        sendRate.setVisibility(View.INVISIBLE);
                        return;
                    }
                }

                Button sendRate = findViewById(R.id.post_avaliar);
                sendRate.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String dateToday = df.format(c);

        if(DateUtil.getSignDateDifferenceInDays(dateToday, pub.getData()) >= 0)
        {
            Button sendRate = findViewById(R.id.post_avaliar);
            sendRate.setVisibility(View.INVISIBLE);
            return;
        }

        if(pub.getIdUser().equals(mFirebaseUser.getUid()))
        {
            Button sendRate = findViewById(R.id.post_avaliar);
            sendRate.setVisibility(View.INVISIBLE);
            return;
        }

        DatabaseReference myRef = mDatabase.child("Avaliar");
        myRef.orderByChild("idUserAvaliador").equalTo(mFirebaseUser.getUid()).addListenerForSingleValueEvent(valueEventListener);
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
                    if(c.getIdPub().equals(idPub))
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
                    if(c.getIdPub().equals(idPub))
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
        DatabaseReference myRef = mDatabase.child("Post/"+ idPub);
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

    public void HandleEnterCommentProfile(View v)
    {
        int i = buttonsIdUsers.indexOf(v.getId());

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", comentarios.get(i).getIdUser());
        startActivity(intent);
    }

    public void HandleInscrito(View v)
    {
        v.setVisibility(View.INVISIBLE);

        Inscrito newPost = new Inscrito(mFirebaseUser.getUid(), idPub, false);

        String key = mDatabase.child("Inscrito").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Inscrito/" + key, newPost.toMap());
        inscritoKey = key;

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(Post.this, "Pedido enviado",
                Toast.LENGTH_SHORT).show();

        Button cancelarConvite = findViewById(R.id.post_cancelarconvite);
        cancelarConvite.setVisibility(View.VISIBLE);
    }

    public void HandleCancelarInscricao(View v)
    {
        v.setVisibility(View.INVISIBLE);

        DatabaseReference myRef = mDatabase.child("Inscrito/" + inscritoKey);
        myRef.removeValue();

        Button enviarConvite = findViewById(R.id.post_action);
        enviarConvite.setVisibility(View.VISIBLE);
    }

    public void HandleAddComment(View v)
    {
        Intent intent = new Intent(this, Comment.class);
        intent.putExtra("idPub", idPub);
        startActivity(intent);
    }

    public void HandleRateUser(View v)
    {
        Intent intent = new Intent(this, Rate.class);
        intent.putExtra("idUserAvaliado", pub.getIdUser());
        intent.putExtra("idPub", idPub);
        startActivity(intent);
    }

    public void HandleDeletePost(View v)
    {
        DeleteComentariosFromDB();
    }
}
