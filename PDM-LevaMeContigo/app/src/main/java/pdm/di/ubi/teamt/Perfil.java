package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import pdm.di.ubi.teamt.tables.User;

public class Perfil extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

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

            GetUserFromDB(idUser);
        }
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Query query = myRef.orderByKey().equalTo(idUser);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void UpdateGUI(User user)
    {
        TextView username = findViewById(R.id.perfil_username);
        TextView viagens = findViewById(R.id.perfil_viagens);
        TextView nota = findViewById(R.id.perfil_avaliacao);
        TextView anos = findViewById(R.id.perfil_anos);

        username.setText(user.getNome());
        viagens.setText(Integer.toString(user.getNumViagens()));
        nota.setText(Float.toString(user.getNota()));
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

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
}
