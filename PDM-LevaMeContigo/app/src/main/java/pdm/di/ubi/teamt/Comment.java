package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.User;

public class Comment extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private DatabaseReference mDatabase = null;

    private User user = null;
    private String idPub = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        idPub = intent.getStringExtra("idPub");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        GetUserFromDB(mFirebaseUser.getUid());
    }

    private void GetUserFromDB(String idUser) {
        DatabaseReference myRef = mDatabase.child("User");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                for (DataSnapshot value : dataSnapshot.getChildren())
                    user = value.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        Query query = myRef.orderByKey().equalTo(idUser);
        query.addListenerForSingleValueEvent(valueEventListener);
    }


    private void AddCommmentToDB(String idPub, String idUser, String userName)
    {
        EditText oComentario = findViewById(R.id.comment_text);
        String text = oComentario.getText().toString();
        if(text.isEmpty())
        {
            Toast.makeText(Comment.this, "Erro: Necessita de introduzir um comentário",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Comentario comment = new Comentario(idUser, userName, idPub, text);

        String key = mDatabase.child("comentario").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Comentario/" + key, comment.toMap());

        mDatabase.updateChildren(childUpdates);

    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
        startActivity(intent);

    }

    public void HandleAddComment(View v)
    {
        AddCommmentToDB(idPub, mFirebaseUser.getUid(), user.getNome());

        Intent intent = new Intent(this, Post.class);
        intent.putExtra("idPub", idPub);
        startActivity(intent);
    }
}
