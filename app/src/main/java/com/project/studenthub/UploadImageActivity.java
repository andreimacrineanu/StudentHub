package com.project.studenthub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.studenthub.Models.Image;
import com.project.studenthub.Models.Post;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class UploadImageActivity extends AppCompatActivity {

    private String pathToFile;
    private Button takePictureBTN;
    private Button uploadPictureBTN;
    private ImageView takenPictureIV;
    private ProgressDialog progressBar;
    private FirebaseStorage firebaseStorage;
    private Image currentImage = null;
    private String classId;
    private EditText descriptionET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                classId = null;
            } else {
                classId = extras.getString("classId");
            }
        } else {
            classId = (String) savedInstanceState.getSerializable("classId");
        }

        takePictureBTN = findViewById(R.id.takePictureBTN);
        takenPictureIV = findViewById(R.id.uploadedImageIV);
        uploadPictureBTN = findViewById(R.id.uploadBTN);
        progressBar = new ProgressDialog(UploadImageActivity.this);
        descriptionET = findViewById(R.id.descriptionET);

        firebaseStorage = FirebaseStorage.getInstance();

        takePictureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(App.getInstance(),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(App.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    PictureTakerAction();
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= 23){
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }
            }
        });

        uploadPictureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImage != null) {
                    try {
                        progressBar.setMessage("Uploading Image...");
                        progressBar.show();
                        DatabaseReference ref = Utils.mDatabase.child("grupa" + Utils.currentUser.getGrupa()).child(Utils.currentUser.getUID());
                        String newKey = ref.push().getKey();
                    /*Image newImage = new Image();
                    newImage.setUID(newKey);
                    newImage.setImage_title(pathToFile.substring(pathToFile.lastIndexOf("/")+1));*/
                        ref.child(newKey).setValue(currentImage.getImage_title()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                StorageReference sr = firebaseStorage.getReference().child("images/users/" + Utils.currentUser.getUID() + "/" + currentImage.getImage_title());
                                sr.putFile(currentImage.getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Post post = new Post();
                                                post.setUserId(Utils.currentUser.getUID());
                                                post.setUserDisplayName(Utils.currentUser.getPrenume() + " " + Utils.currentUser.getNume());
                                                post.setPictureUri(uri.toString());
                                                post.setDescription(descriptionET.getText().toString().trim());
                                                post.setId(Utils.mDatabase.child("posts").child(classId).push().getKey());
                                                Utils.mDatabase.child("posts").child(classId).child(post.getId()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //while(!task.isComplete()){}
                                                        if (task.isSuccessful()) {
                                                            progressBar.dismiss();
                                                            Toast.makeText(App.getInstance(), "Upload Success", Toast.LENGTH_LONG);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.dismiss();
                                        Toast.makeText(App.getInstance(), "Uploaded Failed", Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        });

                    } catch (Exception ex) {

                    }

                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch  (requestCode){
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    PictureTakerAction();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            takenPictureIV.setImageBitmap(bitmap);
        }
    }

    private void PictureTakerAction(){
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            photoFile = createPhotoFile();
            if (photoFile != null){
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(App.getInstance(), "com.example.android.fileprovider", photoFile);
                currentImage = new Image();
                currentImage.setPath(pathToFile);
                currentImage.setUri(photoURI);
                currentImage.setImage_title(pathToFile.substring(pathToFile.lastIndexOf("/")+1));
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePic, 1);
            }
        }
    }

    private File createPhotoFile(){
        String name = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name,".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
