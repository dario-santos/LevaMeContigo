package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void SignIn(String email, String password)
    {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SignIn.this, "Bem Vindo.",
                                    Toast.LENGTH_SHORT).show();

                            StartMenu();
                        }
                        else
                        {
                            Toast.makeText(SignIn.this, "Erro durante a autenticação.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void StartMenu()
    {
        Intent intent = new Intent(this, Menu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void HandleSignIn(View v)
    {
        EditText email = findViewById(R.id.signin_email);
        EditText password = findViewById(R.id.signin_password);

        if(email.getText().toString().isEmpty())
        {
            Toast.makeText(SignIn.this, "Erro: Necessita de introduzir um email.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.getText().toString().isEmpty())
        {
            Toast.makeText(SignIn.this, "Erro: Necessita de introduzir uma password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SignIn(email.getText().toString(), password.getText().toString());
    }

    public void HandleSignUp(View v)
    {
        Intent intent = new Intent(this, SignUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void HandleSignInAnonimo(View v)
    {
        Intent intent = new Intent(this, MenuGuest.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
