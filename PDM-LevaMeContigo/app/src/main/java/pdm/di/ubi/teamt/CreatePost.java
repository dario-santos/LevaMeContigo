package pdm.di.ubi.teamt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

public class CreatePost extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
    }

    public void HandleAddPost()
    {
        Spinner origem = findViewById(R.id.createpost_origem);
        Spinner destino = findViewById(R.id.createpost_destino);
        // Todo: Receber os dados da publicaçaõ
    }
}
