package com.example.tcs_health_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button signup,login;

    DatabaseReference database;
    FirebaseUser user;
    Intent in;
    EditText EMAIL,PassWord;
    ImageView imgeye;
    ProgressBar progressBar;
    public static final String PREFS_NAME = "login_page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EMAIL=findViewById(R.id.editText);
        PassWord=findViewById(R.id.paswrd);

        progressBar=findViewById(R.id.progress);
        login=findViewById(R.id.button);


        signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in=new Intent(Login.this,signup.class);
                startActivity(in);
                finish();
                //  Toast.makeText(login_page.this, "registration activity will open", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = EMAIL.getText().toString();
                final String password = PassWord.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                final String po = email.replace('.', ',');
                database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users/" + po );

                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email_id = snapshot.child("email").getValue(String.class);
                        // String pname = dataSnapshot.child("password").getValue(String.class);
                        String id_pass = snapshot.child("password").getValue(String.class);
                       // Toast.makeText(getApplicationContext(),email_id+" "+id_pass+" "+email,Toast.LENGTH_LONG).show();

                        if (email.equals(email_id) && password.equals(id_pass)) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();

                            SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
                            SharedPreferences.Editor edit=pref.edit();
                            edit.putString("user",po);
                            edit.putString("pass",password);
                            edit.putBoolean("islog",true);
                            edit.apply();

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"not present",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            });


            }
}
