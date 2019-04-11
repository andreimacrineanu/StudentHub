package com.project.studenthub;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.project.studenthub.Models.User;

import okhttp3.internal.Util;

public class RegisterActivity extends AppCompatActivity {

    private final static String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET, numeET, prenumeET, grupaET;
    private Button registerBTN;
    //private FirebaseDatabase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = findViewById(R.id.utilizatorET);
        passwordET = findViewById(R.id.passET);
        numeET = findViewById(R.id.numeET);
        prenumeET = findViewById(R.id.prenumeET);
        grupaET = findViewById(R.id.grupaET);
        registerBTN = findViewById(R.id.registerBTN);

        mAuth = FirebaseAuth.getInstance();
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    updateUI(firebaseUser);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(App.getInstance(), "Register failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });

    }
    private void updateUI(FirebaseUser user){
        if(user != null){
            User newUser = new User(user.getUid(), emailET.getText().toString().trim(), numeET.getText().toString().trim(), prenumeET.getText().toString().trim(), grupaET.getText().toString().trim());
            DatabaseReference ref = Utils.mDatabase.child("users").child(user.getUid());
            ref.setValue(newUser);
            Toast.makeText(App.getInstance(), "Studentul a fost inregistrat cu succes ! ",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            Toast.makeText(App.getInstance(), "Register failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
