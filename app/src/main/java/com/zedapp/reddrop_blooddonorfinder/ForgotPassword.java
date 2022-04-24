package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private String email;
    private EditText emailtxt;
    private TextView login;
    private Button reset;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailtxt = (EditText)findViewById(R.id.email);
        reset = (Button) findViewById(R.id.reset);
        login = (TextView) findViewById(R.id.login);
        fauth =FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailtxt.getText().toString();
                if(email.equals("") || !email.contains("@") || !email.contains("."))
                {
                    emailtxt.setError("inavlid email !");
                }
                else {
                    fauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgotPassword.this,"Plaese Check Email ",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
                                finish();
                            }
                            else {
                                emailtxt.setError(task.getException().getMessage());
                                emailtxt.requestFocus();
                            }

                        }
                    });
                }

            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}