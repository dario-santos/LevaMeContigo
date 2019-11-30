package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.Inscrito;
import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class NotificationRequestedRequest extends AppCompatActivity
{
    private DatabaseReference mDatabase = null;

    private String idPub = null;

    private ArrayList<Inscrito> inscritos = new ArrayList<>();
    private ArrayList<String> inscritoIds = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();

    private ArrayList<Integer> buttonsUserIds = new ArrayList<>();
    private ArrayList<Integer> buttonsResponseAcceptIds = new ArrayList<>();
    private ArrayList<Integer> buttonsResponseRejectIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_requested_request);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        GetPubFromDB(idPub);
        GetInscritosFromDB(idPub);
    }

    private void GetPubFromDB(String idPub)
    {
        ValueEventListener pubValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Publicacao pub = null;

                for(DataSnapshot value : dataSnapshot.getChildren())
                    pub = value.getValue(Publicacao.class);

                UpdateGUIWithPub(pub);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByKey().equalTo(idPub).addValueEventListener(pubValueEventListener);
    }

    private void UpdateGUIWithPub(Publicacao pub)
    {
        TextView origemDestino = findViewById(R.id.notification_requested_request_origemdestino);
        TextView data = findViewById(R.id.notification_requested_request_data);
        TextView hora = findViewById(R.id.notification_requested_request_hora);

        origemDestino.setText(pub.getOrigem() + " -> " + pub.getDestino());
        data.setText("Data: " + pub.getData());
        hora.setText("Hora: " + pub.getHora());
    }

    private void GetInscritosFromDB(final String idPub)
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

                    inscritos.add(inscr);
                    inscritoIds.add(value.getKey());
                    userIds.add(inscr.getIdUser());
                }

                ValueEventListener pubValueEventListener = new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        for(DataSnapshot value : dataSnapshot.getChildren())
                        {
                            if(!userIds.contains(value.getKey()))
                                continue;

                            User user = value.getValue(User.class);
                            users.add(user);
                        }

                        ShowInscritosToUser();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                DatabaseReference postRef = mDatabase.child("User");
                postRef.orderByChild("nome").addValueEventListener(pubValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("Inscrito");
        postRef.orderByChild("idPub").equalTo(idPub).addValueEventListener(valueEventListener);
    }

    private void ShowInscritosToUser()
    {
        LinearLayout oLL = findViewById(R.id.notification_requested_request_llsv);

        for(int i = 0 ; i < inscritos.size() ; i++)
        {
            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.notification_requested_request_line, null);
            oCL1.setId(View.generateViewId());

            ImageView userProfile = oCL1.findViewById(R.id.requested_request_useravatar);
            userProfile.setClickable(true);
            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    HandleEnterProfile(view);
                }
            });
            userProfile.setId(View.generateViewId());
            buttonsUserIds.add(userProfile.getId());

            TextView userName = oCL1.findViewById(R.id.requested_request_username);
            userName.setText(users.get(i).getNome());
            userName.setId(View.generateViewId());

            Button accept = oCL1.findViewById(R.id.requested_request_accept);
            accept.setClickable(true);
            accept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    HandleAcceptInscrito(view);
                }
            });
            accept.setId(View.generateViewId());
            buttonsResponseAcceptIds.add(accept.getId());

            Button reject = oCL1.findViewById(R.id.requested_request_reject);
            reject.setClickable(true);
            reject.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    HandleRejectInscrito(view);
                }
            });
            reject.setId(View.generateViewId());
            buttonsResponseRejectIds.add(reject.getId());

            oLL.addView(oCL1);
        }
    }

    public void HandleEnterProfile(View v)
    {
        int i = buttonsUserIds.indexOf(v.getId());

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", inscritos.get(i).getIdUser());
        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, NotificationRequested.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void HandleAcceptInscrito(View v)
    {
        int i = buttonsResponseAcceptIds.indexOf(v.getId());
        String inscrKey = inscritoIds.get(i);

        mDatabase.child("Inscrito/" + inscrKey + "/accepted").setValue(true);
        ResetActivity();
    }

    public void HandleRejectInscrito(View v)
    {
        int i = buttonsResponseRejectIds.indexOf(v.getId());
        String inscrKey = inscritoIds.get(i);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                for(DataSnapshot value : snapshot.getChildren())
                    value.getRef().removeValue();

                ResetActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference myRef = mDatabase.child("Inscrito");
        myRef.orderByKey().equalTo(inscrKey).addValueEventListener(valueEventListener);
    }

    private void ResetActivity()
    {
        Intent intent = new Intent(this, NotificationRequestedRequest.class);
        intent.putExtra("idPub", idPub);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
