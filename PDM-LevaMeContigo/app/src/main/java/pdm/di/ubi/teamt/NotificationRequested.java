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

import pdm.di.ubi.teamt.tables.Publicacao;

public class NotificationRequested extends AppCompatActivity
{
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private ArrayList<Publicacao> pubs = new ArrayList<>();
    private ArrayList<String> pubIds = new ArrayList<>();
    private ArrayList<Integer> buttonsPubIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_requested);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GetPubFromDB(mFirebaseUser.getUid());
    }

    private void GetPubFromDB(final String idUser)
    {
        ValueEventListener pubValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao pub = value.getValue(Publicacao.class);

                    if(idUser.equals(pub.getIdUser()))
                    {
                        pubs.add(pub);
                        pubIds.add(value.getKey());
                    }
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
        LinearLayout oLL = findViewById(R.id.notification_requested_llsv);

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
                    HandleEnterRequest(view);
                }
            });
            background.setId(View.generateViewId());
            buttonsPubIds.add(background.getId());

            oLL.addView(oCL1);
        }
    }

    public void HandleEnterRequest(View v)
    {
        int i = buttonsPubIds.indexOf(v.getId());

        Intent intent = new Intent(this, NotificationRequestedRequest.class);
        intent.putExtra("idPub", pubIds.get(i));
        startActivity(intent);
    }

    public void HandleNotificationSubscribed(View view)
    {
        Intent intent = new Intent(this, NotificationSubscribed.class);
        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
