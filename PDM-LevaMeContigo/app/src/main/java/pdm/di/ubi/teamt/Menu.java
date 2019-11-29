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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Publicacao;

public class Menu extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private ArrayList<Publicacao> posts = new ArrayList<>();
    private ArrayList<Integer> userIds = new ArrayList<>();
    private ArrayList<Integer> buttonsPostIds = new ArrayList<>();
    private ArrayList<String> postIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ReadPostsFromDataBase();
    }

    private void ReadPostsFromDataBase()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao post = value.getValue(Publicacao.class);

                    if(postIds.contains(value.getKey()))
                        continue;

                    posts.add(post);
                    postIds.add(value.getKey());
                }
                ShowPostsToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String s = df.format(c);
        String a = df.format(c);


        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").startAt(a).addValueEventListener(valueEventListener);
    }

    private void ShowPostsToUser()
    {
        LinearLayout oLL = findViewById(R.id.menu_llsv);

        for(int i = 0 ; i < posts.size() ; i++)
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
            userIds.add(userProfile.getId());

            TextView origemDestino = oCL1.findViewById(R.id.postline_origemdestino);
            origemDestino.setText(posts.get(i).getOrigem() + " -> " + posts.get(i).getDestino());
            origemDestino.setId(View.generateViewId());

            TextView data = oCL1.findViewById(R.id.postline_data);
            data.setText("Dia: " + posts.get(i).getData());
            data.setId(View.generateViewId());

            TextView hora = oCL1.findViewById(R.id.postline_hora);
            hora.setText("Hora: " + posts.get(i).getHora());
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
            buttonsPostIds.add(background.getId());

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
        int i = userIds.indexOf(v.getId());

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", posts.get(i).getIdUser());
        startActivity(intent);

    }

    public void HandleEnterPost(View v)
    {
        int i = buttonsPostIds.indexOf(v.getId());

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", postIds.get(i));
        startActivity(intent);
    }
}
