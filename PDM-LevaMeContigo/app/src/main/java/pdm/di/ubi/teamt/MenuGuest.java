package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pdm.di.ubi.teamt.tables.Publicacao;

public class MenuGuest extends AppCompatActivity
{
    ArrayList<Publicacao> posts = new ArrayList<>();
    private DatabaseReference mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_guest);

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
                if(!dataSnapshot.exists())
                    return;

                for(DataSnapshot value : dataSnapshot.getChildren())
                {
                    Publicacao post = value.getValue(Publicacao.class);
                    posts.add(post);
                }
                //Todo: Show to User
                ShowPostsToUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        String endDate = sdf.format(cal.getTime());

        Date today = Calendar.getInstance().getTime();
        String todayDate = sdf.format(today);

        DatabaseReference postRef = mDatabase.child("Post");
        postRef.orderByChild("data").startAt(todayDate).endAt(endDate).addValueEventListener(valueEventListener);
    }

    private void ShowPostsToUser()
    {
        LinearLayout oLL = findViewById(R.id.menuguest_llsv);

        for(int i = 0 ; i < posts.size() ; i++)
        {
            ConstraintLayout oCL1 = (ConstraintLayout) getLayoutInflater().inflate(R.layout.menuguest_line, null);
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
        startActivity(intent);
    }
}
