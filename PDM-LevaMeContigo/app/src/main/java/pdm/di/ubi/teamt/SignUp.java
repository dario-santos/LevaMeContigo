package pdm.di.ubi.teamt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

import pdm.di.ubi.teamt.tables.User;

public class SignUp extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth = null;
    private FirebaseUser mFirebaseUser = null;
    private FirebaseDatabase mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void SignUp(String email, String password)
    {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SignUp.this, "Bem Vindo.",
                                    Toast.LENGTH_SHORT).show();

                            mFirebaseUser = mFirebaseAuth.getCurrentUser();

                            // Todo: Start Menu com o utilizador logado
                            AddUserToDataBase();
                            StartMenu();
                        }
                        else
                        {
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Toast.makeText(SignUp.this, "Erro: A palavra-passe é fraca.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Toast.makeText(SignUp.this, "Erro: Email inválido.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Toast.makeText(SignUp.this, "Erro: O email já se encontra registado.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(SignUp.this, "Erro durante a autenticação.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void AddUserToDataBase()
    {
        EditText name = findViewById(R.id.signup_name);
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        User newUser = new User(name.getText().toString(), 0, 5f, df.format(c));

        DatabaseReference myRef = mDatabase.getReference("User");

        myRef.child(mFirebaseUser.getUid()).setValue(newUser.toMap());
    }

    private void StartMenu()
    {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }

    public void HandleRandomPassowrd(View v)
    {
        TextView password = findViewById(R.id.signup_randompassword);
        password.setText("Palavra-passe: " + Password.Generate(7));
        password.setVisibility(View.VISIBLE);
    }

    public void HandleSignUp(View v)
    {
        EditText email = findViewById(R.id.signup_email);
        EditText password = findViewById(R.id.signup_password);
        EditText name = findViewById(R.id.signup_name);

        if(email.getText().toString().isEmpty())
        {
            Toast.makeText(SignUp.this, "Erro: Necessita de introduzir um email.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.getText().toString().isEmpty())
        {
            Toast.makeText(SignUp.this, "Erro: Necessita de introduzir uma password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.getText().toString().isEmpty())
        {
            Toast.makeText(SignUp.this, "Erro: Necessita de introduzir um nome.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SignUp(email.getText().toString(), password.getText().toString());
    }

    public void HandleBack(View v)
    {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}