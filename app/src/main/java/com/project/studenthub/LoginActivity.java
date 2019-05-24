package com.project.studenthub;

import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.project.studenthub.APIRequests.ImageAPI;
import com.project.studenthub.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.project.studenthub.APIRequests.ImageAPI.BASE_URL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button registerBTN, loginBTN;
    private EditText emailET, passwordET;
    private FirebaseAuth mAuth;
    private static final String apiKey = "AIzaSyCw6dLHoce8gNL7Rni-AUStAe2VCmIc8A4";
    private String pictureJson = "{"+
            "\"requests\": [" +
            "{ " +
            "\"image\": {"+
            "\"source\": {"+
            "\"imageUri\": \"salut\""+
            "}"+
            "}," +
            "\"features\": ["+
            "{"+
            "\"type\": \"DOCUMENT_TEXT_DETECTION\" " +
            "}"+
            "]"+
            "}"+
            "]"+
            "}";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String url = "http://my-json-feed";
        Gson gson = new Gson();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(pictureJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.getJSONArray("requests").getJSONObject(0).getJSONObject("image").getJSONObject("source").put("imageUri","https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/studenthub-3a6ba.appspot.com\\/o\\/images%2Fusers%2F5Hca5XsTMMaYL3uEVLVHrtaQPFI2%2F25052019_0019587263388878344898660.jpg?alt=media&token=141b66f3-a5a2-4d3a-9ab6-e2c200eab5d8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://vision.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageAPI imageAPI = retrofit.create(ImageAPI.class);

        Call<ResponseBody> call = imageAPI.sendImage(apiKey, jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "Acesta este raspunsul : " + response.body().toString());
                }catch(Exception ex){
                    Log.d(TAG, "NU A MERS 1 : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "NU A MERS : " + t.getMessage());
            }
        });
        Log.d(TAG,"Acesta este jsonu-ul : " + jsonObject.toString());

        loginBTN = findViewById(R.id.loginBTN);
        registerBTN = findViewById(R.id.registerBTN);
        emailET = findViewById(R.id.utilizatorET);
        passwordET = findViewById(R.id.passET);

        mAuth = FirebaseAuth.getInstance();

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App.getInstance(), RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(emailET.getText().toString().trim(), passwordET.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(App.getInstance(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        emailET.setText("test@gmail.com");
        passwordET.setText("123456");
        //loginBTN.performClick();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI (FirebaseUser user){
        if (user != null){
            DatabaseReference ref = Utils.mDatabase.child("users").child(user.getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Utils.currentUser = new User();
                    Utils.currentUser = dataSnapshot.getValue(User.class);
                    Log.d(TAG,"Cursurile : " + Utils.currentUser.getClasses().toString());
                    Intent intent = new Intent(App.getInstance(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(App.getInstance(), "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
