package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Inscrito;
import pdm.di.ubi.teamt.tables.Publicacao;

public class NotificationSubscribed extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private String idUser = null;

    private ArrayList<Publicacao> pubs = new ArrayList<>();
    private ArrayList<String> pubIds = new ArrayList<>();
    private ArrayList<Integer> buttonsPubIds = new ArrayList<>();

    ArrayList<String> inscritos = new ArrayList<>();
    ArrayList<String> avaliacoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_subscribed);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        idUser = mFirebaseUser.getUid();

        GetAvaliarFromDB(idUser);
    }

    private void GetAvaliarFromDB(final String idUser)
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Avaliar avaliar = value.getValue(Avaliar.class);
                    avaliacoes.add(avaliar.getIdPub());
                }

                GetInscritoFromDB(idUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Avaliar");
        postRef.orderByChild("idUserAvaliador").equalTo(idUser).addValueEventListener(valueEventListener);
    }

    private void GetInscritoFromDB(final String idUser)
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
                    Inscrito inscr = value.getValue(Inscrito.class);

                    if(inscr.getAccepted())
                        inscritos.add(inscr.getIdPub());
                }

                GetPubFromDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Inscrito");
        postRef.orderByChild("idUser").equalTo(idUser).addValueEventListener(valueEventListener);
    }

    private void GetPubFromDB()
    {
        ValueEventListener pubValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(!inscritos.contains(value.getKey()))
                        continue;

                    if(avaliacoes.contains(value.getKey()))
                        continue;

                    Publicacao post = value.getValue(Publicacao.class);

                    pubs.add(post);
                    pubIds.add(value.getKey());
                }

                ShowPostsToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").addValueEventListener(pubValueEventListener);
    }

    private void ShowPostsToUser()
    {
        LinearLayout oLL = findViewById(R.id.notification_subscribed_llsv);

        for(int i = 0 ; i < pubs.size() ; i++)
        {
            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.notification_subscribed_line, null);
            oCL1.setId(View.generateViewId());

            TextView origemDestino = oCL1.findViewById(R.id.subscribed_line_origemdestino);
            origemDestino.setText(pubs.get(i).getOrigem() + " -> " + pubs.get(i).getDestino());
            origemDestino.setId(View.generateViewId());

            TextView data = oCL1.findViewById(R.id.subscribed_line_data);
            data.setText("Dia: " + pubs.get(i).getData());
            data.setId(View.generateViewId());

            TextView hora = oCL1.findViewById(R.id.subscribed_line_hora);
            hora.setText("Hora: " + pubs.get(i).getHora());
            hora.setId(View.generateViewId());

            ImageView background = oCL1.findViewById(R.id.subscribed_line_background);
            background.setClickable(true);
            background.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    HandleEnterPub(view);
                }
            });
            background.setId(View.generateViewId());
            buttonsPubIds.add(background.getId());

            oLL.addView(oCL1);
        }
    }

    public void HandleNotificationRequested(View view)
    {
        Intent intent = new Intent(this, NotificationRequested.class);
        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void HandleEnterPub(View v)
    {
        int i = buttonsPubIds.indexOf(v.getId());

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", pubIds.get(i));
        startActivity(intent);
    }
}
