package com.example.sic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private CardView loginButton;
    private LinearLayout signUpLayout;
    private ImageView showPassword;
    private boolean showPass=false;
    private SharedPreferences sp;
    private FirebaseUser user;
    private TextView forgotPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = this.getSharedPreferences("SiC", Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signUpLayout = findViewById(R.id.login_sign_up_layout);
        showPassword = findViewById(R.id.login_show_password);
        forgotPassword = findViewById(R.id.login_forgot_password);
        loginButton.setOnClickListener(this);
        signUpLayout.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==loginButton){
            if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && password.getText().toString().trim().length()>5) {
                performFireBaseLogin();
            }else{
                Toast.makeText(this, "Invalid credentials! Please re-check", Toast.LENGTH_LONG).show();
            }
        }else if(v==signUpLayout){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }else if(v==showPassword){
            if(!showPass){
                showPassword.setImageDrawable(this.getDrawable(R.drawable.ic_hide_password));
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setSelection(password.getText().toString().length());
                showPass=true;
            }else{
                showPassword.setImageDrawable(this.getDrawable(R.drawable.ic_show_password));
                password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.getText().toString().length());
                showPass=false;
            }
        }else if(v==forgotPassword){
            if(user!=null && user.isEmailVerified()) {
                firebaseAuth.sendPasswordResetEmail(sp.getString("email",""))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Link send to your registered email, please check your email", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Cannot process request at the moment", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                if(!user.isEmailVerified())
                    Toast.makeText(this, "Email not verfied", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Not a registered user!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        if(user!=null && user.isEmailVerified()){
            moveToHomePageActivity();
        }
    }

    private void moveToHomePageActivity() {
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void performFireBaseLogin() {
        user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(user.isEmailVerified()) {
                        firebaseAuth.signInWithEmailAndPassword(email.getText().toString().trim(),
                                password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user = authResult.getUser();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email", email.getText().toString().trim());
                                editor.putString("password", password.getText().toString());
                                editor.apply();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Unable to login, please check you credentials and login later", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this, "Email not verified, please verify and login again", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Unable to process request at the moment!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
