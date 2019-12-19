package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class Menu extends AppCompatActivity
{
    private DatabaseReference mDatabase = null;
    private FirebaseUser mFirebaseUser = null;

    private ArrayList<Publicacao> pubs = new ArrayList<>();
    private ArrayList<String> pubKeys = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<String> usersKeys = new ArrayList<>();

    private ArrayList<Integer> buttonsUserIds = new ArrayList<>();
    private ArrayList<Integer> buttonsPubIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        GetPubsFromDB();
    }

    private void GetPubsFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                pubs.clear();
                pubKeys.clear();
                usersKeys.clear();

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(!pubKeys.contains(value.getKey()))
                    {
                        Publicacao publicacao = value.getValue(Publicacao.class);

                        pubs.add(publicacao);
                        pubKeys.add(value.getKey());
                        usersKeys.add(publicacao.getIdUser());
                    }
                }
                GetUsersFromDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        String formatedDate = dateFormatter.format(date);

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").startAt(formatedDate).addValueEventListener(valueEventListener);
    }

    private void GetUsersFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                users.clear();

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(usersKeys.contains(value.getKey()))
                    {
                        User user = value.getValue(User.class);
                        users.add(user);
                    }
                }
                ShowPubsToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        DatabaseReference postRef = mDatabase.child("User");
        postRef.orderByKey().addValueEventListener(valueEventListener);
    }

    private void ShowPubsToUser()
    {
        LinearLayout oLL = findViewById(R.id.menu_llsv);
        oLL.removeAllViews();

        buttonsUserIds.clear();
        buttonsPubIds.clear();

        for(int i = 0 ; i < pubs.size() ; i++)
        {
            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.menu_postline, null);
            oCL1.setId(View.generateViewId());

            ImageView userProfile = oCL1.findViewById(R.id.postline_userprofile);
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

            TextView origemDestino = oCL1.findViewById(R.id.postline_origemdestino);
            origemDestino.setText(pubs.get(i).getOrigem() + " -> " + pubs.get(i).getDestino());
            origemDestino.setId(View.generateViewId());

            TextView data = oCL1.findViewById(R.id.postline_data);
            data.setText("Dia: " + pubs.get(i).getData());
            data.setId(View.generateViewId());

            TextView hora = oCL1.findViewById(R.id.postline_hora);
            hora.setText("Hora: " + pubs.get(i).getHora());
            hora.setId(View.generateViewId());

            ImageView background = oCL1.findViewById(R.id.postline_background);
            background.setClickable(true);
            background.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    HandleEnterPost(view);
                }
            });
            background.setId(View.generateViewId());
            buttonsPubIds.add(background.getId());

            oLL.addView(oCL1);
        }
    }

    public void HandleCreatePost(View v)
    {
        Intent intent = new Intent(this, CreatePost.class);

        startActivity(intent);
    }

    public void HandleUserProfile(View v)
    {
        if(mFirebaseUser == null)
            return;

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", mFirebaseUser.getUid());

        startActivity(intent);
    }

    public void HandleEnterProfile(View v)
    {
        int i = buttonsUserIds.indexOf(v.getId());

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", pubs.get(i).getIdUser());

        startActivity(intent);
    }

    public void HandleEnterPost(View v)
    {
        int i = buttonsPubIds.indexOf(v.getId());

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", pubKeys.get(i));

        startActivity(intent);
    }

    public void HandleEnterNotification(View v)
    {
        Intent intent = new Intent(this, NotificationSubscribed.class);

        startActivity(intent);
    }

    public void HandleEnterSearch(View v)
    {
        Intent intent = new Intent(this, Search.class);

        startActivity(intent);
    }

    public void HandleVisitCovilha(View v)
    {
        Uri uri = Uri.parse("http://www.cm-covilha.pt/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
