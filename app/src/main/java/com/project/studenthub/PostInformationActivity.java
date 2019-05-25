package com.project.studenthub;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.project.studenthub.Models.Post;

import okhttp3.internal.Util;

public class PostInformationActivity extends AppCompatActivity {

    private Post post;
    private EditText pictureTextTV;
    private Button updateTextBTN;
    private String initialText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_information);

        pictureTextTV = findViewById(R.id.pictureTextTV);
        updateTextBTN = findViewById(R.id.updateConversionBTN);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                post = null;
            } else {
                post = (Post) extras.getSerializable("post");
            }
        } else {
            post = (Post) savedInstanceState.getSerializable("classId");
        }
        if (post != null) {
            initialText = post.getConvertedPicture();
            pictureTextTV.setText(post.getConvertedPicture());

            if (!post.getUserId().equals(Utils.currentUser.getUID())) {
                updateTextBTN.setEnabled(false);
            }

            updateTextBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updatedText = pictureTextTV.getText().toString();
                    if (!initialText.equals(updatedText)) {
                        Utils.mDatabase.child("posts").child(post.getClassId()).child(post.getId()).child("convertedPicture").setValue(updatedText).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(App.getInstance(), "Text updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }
}
