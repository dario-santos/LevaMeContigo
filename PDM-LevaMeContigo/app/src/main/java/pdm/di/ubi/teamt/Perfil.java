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
import java.util.concurrent.TimeUnit;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

public class Perfil extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private int numberOfBoleias = 0;
    private float userRating = 0;

    private ArrayList<Avaliar> avaliacoes =  new ArrayList<>();
    private ArrayList<String> avaliacoesKeys =  new ArrayList<>();
    private ArrayList<String> publishedAvaliacoesKeys =  new ArrayList<>();

    private ArrayList<Integer> buttonsUserIds =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Intent intent = getIntent();
        String idUser = intent.getStringExtra("idUser");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(idUser != null && mFirebaseUser != null)
        {
            if(idUser.equals(mFirebaseUser.getUid()))
            {
                ImageButton logout = findViewById(R.id.perfil_logout);
                logout.setVisibility(View.VISIBLE);
            }

            GetNumberOfPubFromDB(idUser);
        }
    }

    private void GetUserRate()
    {
        float nota = 0;

        if(avaliacoes.size() < 5 || numberOfBoleias < 10)
        {
            userRating = -1;
            return;
        }

        for(Avaliar avaliar : avaliacoes)
            nota += avaliar.getNota();

        userRating = nota/avaliacoes.size();
    }

    private void GetNumberOfPubFromDB(final String idUser)
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao pub = value.getValue(Publicacao.class);
                    if(pub.getIdUser().equals(idUser))
                        numberOfBoleias++;
                }
                GetAvaliarFromDB(idUser);
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

    private void GetUserFromDB(String idUser)
    {
        DatabaseReference myRef = mDatabase.child("User");

        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    User user = value.getValue(User.class);
                    UpdateGUI(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        };

        Query query = myRef.orderByKey().equalTo(idUser);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void GetAvaliarFromDB(final String idUser)
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    if(avaliacoesKeys.contains(value.getKey()))
                        continue;

                    Avaliar avaliar = value.getValue(Avaliar.class);

                    avaliacoes.add(avaliar);
                    avaliacoesKeys.add(value.getKey());
                }

                ShowAvaliarToUser();
                GetUserRate();
                GetUserFromDB(idUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        DatabaseReference myRef = mDatabase.child("Avaliar");
        myRef.orderByChild("idUserAvaliado").equalTo(idUser).addListenerForSingleValueEvent(valueEventListener);
    }

    private void ShowAvaliarToUser()
    {
        LinearLayout oLL = findViewById(R.id.perfil_llsv);

        for(int i = 0 ; i < avaliacoes.size() ; i++)
        {
            if(publishedAvaliacoesKeys.contains(avaliacoesKeys.get(i)))
                continue;

            publishedAvaliacoesKeys.add(avaliacoesKeys.get(i));

            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.perfil_line, null);
            oCL1.setId(View.generateViewId());

            ImageView userProfile = oCL1.findViewById(R.id.perfil_line_userimage);
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

            TextView userName = oCL1.findViewById(R.id.perfil_line_username);
            userName.setText(avaliacoes.get(i).getNomeAvaliador());
            userName.setId(View.generateViewId());

            TextView nota = oCL1.findViewById(R.id.perfil_line_rate);
            nota.setText("Nota: " + avaliacoes.get(i).getNota());
            nota.setId(View.generateViewId());

            TextView comentario = oCL1.findViewById(R.id.perfil_line_comment);
            ImageView background = oCL1.findViewById(R.id.perfil_line_background);

            if(avaliacoes.get(i).getComentario().equals(" "))
                background.setVisibility(View.INVISIBLE);

            comentario.setText(avaliacoes.get(i).getComentario());
            comentario.setId(View.generateViewId());

            oLL.addView(oCL1);
        }
    }

    private void UpdateGUI(User user)
    {
        TextView username = findViewById(R.id.perfil_username);
        TextView viagens = findViewById(R.id.perfil_viagens);
        TextView nota = findViewById(R.id.perfil_avaliacao);
        TextView anos = findViewById(R.id.perfil_anos);

        String rating = userRating == -1 ? "Calculating" : String.format("%.1f", userRating);

        username.setText(user.getNome());
        viagens.setText(Integer.toString(numberOfBoleias));
        nota.setText(rating);
        anos.setText(String.format("%.1f", DateDifference(user.getDataInscricao())));
    }

    private float DateDifference(String firstDate)
    {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String secondDate = df.format(c);

        long diff = 0;
        try
        {
            long diffInMillies = Math.abs(df.parse(firstDate).getTime() - df.parse(secondDate).getTime());
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        catch (Exception e){}

        return diff/365f;
    }

    public void HandleEnterProfile(View v)
    {
        int i = buttonsUserIds.indexOf(v.getId());

        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("idUser", avaliacoes.get(i).getIdUserAvaliador());
        startActivity(intent);
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void HandleLogout(View v)
    {
        mFirebaseAuth.signOut();

        Toast.makeText(this, "Adeus!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}
