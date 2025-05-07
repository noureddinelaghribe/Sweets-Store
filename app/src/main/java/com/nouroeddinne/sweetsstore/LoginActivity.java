package com.nouroeddinne.sweetsstore;

import static com.nouroeddinne.sweetsstore.SignupActivity.EMAIL_PATTERN;
import static com.nouroeddinne.sweetsstore.SignupActivity.PASSWORD_PATTERN;
import static com.nouroeddinne.sweetsstore.SignupActivity.validate;

import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_EMAIL;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_PASSWORD;
import static Utel.UtelsDB.SHAREDPREFERNCES_FILENAME_USER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    Button login,tosingin,skip;
    TextView textViewForgottenPassword;
    ImageView imageView_facebook,imageView_google,imgShow;
    EditText email,password;
    private boolean PasswordVisible = false;
    SharedPreferences sharedPreferences;
    //DataBaseAccess db = DataBaseAccess.getInstance(this);

    FirebaseAuth auth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.button_login);
        tosingin = findViewById(R.id.button_goto_signup);
        skip = findViewById(R.id.button_skip);
        imageView_facebook = findViewById(R.id.imageView_facebook);
        imageView_google = findViewById(R.id.imageView_google);
        email = findViewById(R.id.editTextText_singup_email);
        password = findViewById(R.id.editTextText_singup_Password);
        imgShow = findViewById(R.id.imageView_login_show);
        textViewForgottenPassword = findViewById(R.id.textViewForgottenPassword);

        sharedPreferences = getSharedPreferences(SHAREDPREFERNCES_FILENAME_USER, Context.MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();

        imgShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordVisible = !PasswordVisible;

                if (PasswordVisible){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // show
                    imgShow.setImageResource(R.drawable.visibility_off);
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //hidden
                    imgShow.setImageResource(R.drawable.visibility_on);
                }

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (validate(EMAIL_PATTERN,email.getText().toString())&& !email.getText().toString().isEmpty()){
                    if (validate(PASSWORD_PATTERN,password.getText().toString())&& !password.getText().toString().isEmpty()){
//                        if (db.isEmailExist(email.getText().toString())){
//                        }else {
//                            email.setError("This email is not exist add other email or create a new account.");
//                        }

                        login(email.getText().toString(), password.getText().toString());

                    }else {
                        password.setError("At least 8 chars\n" +
                                "\n" +
                                "Contains at least one digit\n" +
                                "\n" +
                                "Contains at least one lower alpha char and one upper alpha char\n" +
                                "\n" +
                                "Contains at least one char within a set of special chars (@#%$^ etc.)\n" +
                                "\n" +
                                "Does not contain space, tab, etc.");
                    }
                }else {
                    email.setError("exp@gmail.com");
                }



            }
        });


        tosingin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();

            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                startActivity(intent);
//                finish();

            }
        });

        textViewForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgottenPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });


        imageView_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();

            }
        });



        imageView_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();

            }
        });


    }


    public void login(String email,String password){

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Sign in is not successful", Toast.LENGTH_SHORT).show();

                        }

                    }
                });



//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(SHAREDPREFERNCES_FILENAME_EMAIL, email.getText().toString());
//        editor.putString(SHAREDPREFERNCES_FILENAME_PASSWORD, password.getText().toString());
//        editor.apply();
//
//        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//        startActivity(intent);
//        finish();
//


    }




















}