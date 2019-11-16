package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void HandleCreatePost(View v)
    {
        Intent intent = new Intent(this, CreatePost.class);
        startActivity(intent);
    }
}
