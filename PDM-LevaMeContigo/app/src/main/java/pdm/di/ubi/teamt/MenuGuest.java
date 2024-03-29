package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MenuGuest extends AppCompatActivity
{
    private DatabaseReference mDatabase = null;

    private ArrayList<Publicacao> posts = new ArrayList<>();

    // Number of days that the user can see ads from now
    private int numberOfDays = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_guest);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        GetPubsFromDB();
    }

    private void GetPubsFromDB()
    {
        ValueEventListener valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                posts.clear();

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao pub = value.getValue(Publicacao.class);
                    posts.add(pub);
                }
                ShowPostsToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numberOfDays);

        String endDate = sdf.format(cal.getTime());

        Date today = Calendar.getInstance().getTime();
        String todayDate = sdf.format(today);

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").startAt(todayDate).endAt(endDate).addValueEventListener(valueEventListener);
    }

    private void ShowPostsToUser()
    {
        LinearLayout oLL = findViewById(R.id.menuguest_llsv);
        oLL.removeAllViews();

        for(int i = 0 ; i < posts.size() ; i++)
        {

            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.menuguest_line,
                    null);
            oCL1.setId(View.generateViewId());

            TextView origemDestino = oCL1.findViewById(R.id.menuguest_line_origemdestino);
            origemDestino.setText(posts.get(i).getOrigem() + " -> " + posts.get(i).getDestino());
            origemDestino.setId(View.generateViewId());

            oLL.addView(oCL1);
        }
    }

    public void HandleCreateAccount(View v)
    {
        Intent intent = new Intent(this, SignUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
