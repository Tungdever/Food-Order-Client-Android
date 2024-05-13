package com.uteating.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.uteating.foodapp.model.FireUser;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEditText, pwdEditText;
    Button btnRegister;
    TextView textView;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AnhXa();

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(pwdEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FireUser tmp = new FireUser(task.getResult().getUser().getUid(), email);
                            FirebaseDatabase.getInstance().getReference("Users").child(tmp.getUserId()).setValue(tmp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("Users").child(tmp.getUserId()).setValue(tmp);
                                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.w("REGISTER", "addUserToFirebase:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Account created failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.w("REGISTER", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });


            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AnhXa() {
        emailEditText = findViewById(R.id.etUserEmailRegister);
        pwdEditText = findViewById(R.id.etUserPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBarRegister);
        textView = findViewById(R.id.tvLogin);
    }
}