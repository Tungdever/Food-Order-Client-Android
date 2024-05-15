package com.uteating.foodapp.activity.Home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



public class ForgotActivity extends AppCompatActivity {
    private ActivityForgotBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtEmail.getText().toString().isEmpty()) {
                    new FailToast(ForgotActivity.this, "Please enter the email you want to reset password").showToast();
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(binding.edtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new SuccessfulToast(ForgotActivity.this, "Reset password successfully! Please check your email").showToast();
                                finish();
                            } else {
                                new FailToast(ForgotActivity.this, "Make sure your enter email is correct!").showToast();
                            }
                        }
                    });
                }
            }
        });

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}