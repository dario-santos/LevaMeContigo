package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Menu extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private FirebaseDatabase mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
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
}
