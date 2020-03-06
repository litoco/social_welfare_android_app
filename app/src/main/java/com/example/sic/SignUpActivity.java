package com.example.sic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText email, password, confirmPassword;
    private ImageView showPassword;
    private CardView signUpButton;
    private LinearLayout resendEmailVerficationLink;
    private boolean showPass=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);
        confirmPassword = findViewById(R.id.sign_up_confirm_password);
        showPassword = findViewById(R.id. sign_up_show_password);
        signUpButton = findViewById(R.id.sign_up_button);
        resendEmailVerficationLink = findViewById(R.id.sign_up_layout_resend_link);
        showPassword.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        resendEmailVerficationLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==showPassword){
            if(!showPass){
                showPassword.setImageDrawable(this.getDrawable(R.drawable.ic_hide_password));
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                if(password.isSelected())
                    password.setSelection(password.getText().toString().length());
                confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                if(confirmPassword.isSelected())
                    confirmPassword.setSelection(confirmPassword.getText().toString().length());
                showPass=true;
            }else{
                showPassword.setImageDrawable(this.getDrawable(R.drawable.ic_show_password));
                password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                if(password.isSelected())
                    password.setSelection(password.getText().toString().length());
                confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                if(confirmPassword.isSelected())
                    confirmPassword.setSelection(confirmPassword.getText().toString().length());
                showPass=false;
            }
        }else if(v==signUpButton){
            if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() &&
                password.getText().toString().length()>5 &&
                password.getText().toString().equals(confirmPassword.getText().toString())){
                registerUserWithFireBase();
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(this, "Not a valid email", Toast.LENGTH_SHORT).show();
                }else if(password.length()<=5){
                    Toast.makeText(this, "Password must be at 6 digit long", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Both passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(v==resendEmailVerficationLink){
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this, "Link send successfully!!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(SignUpActivity.this, "Failed to send verification link!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void registerUserWithFireBase() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().trim(),
                password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(SignUpActivity.this, "Please verify your email to login", Toast.LENGTH_LONG).show();
                authResult.getUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Link sent successfully!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Failed to send verification link!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Unable to process request at the moment!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
