package com.project.studenthub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.project.studenthub.APIRequests.ImageAPI;
import com.project.studenthub.Models.Image;
import com.project.studenthub.Models.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.project.studenthub.APIRequests.ImageAPI.BASE_URL;

public class UploadImageActivity extends AppCompatActivity {

    private final static String TAG = UploadImageActivity.class.getSimpleName();
    private String pathToFile;
    private Button takePictureBTN;
    private Button uploadPictureBTN;
    private ImageView takenPictureIV;
    private ProgressDialog progressBar;
    private FirebaseStorage firebaseStorage;
    private Image currentImage = null;
    private String classId;
    private static final String TARGET_URL =
            "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
            "key=AIzaSyCw6dLHoce8gNL7Rni-AUStAe2VCmIc8A4";
    private EditText descriptionET;
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
                                                post.setClassId(classId);
                                                Utils.mDatabase.child("posts").child(classId).child(post.getId()).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //while(!task.isComplete()){}
                                                        if (task.isSuccessful()) {
                                                            try {
                                                            ExecutorService executorService = Executors.newCachedThreadPool();
                                                            JSONObject jsonObject = new JSONObject(pictureJson);
                                                            jsonObject.getJSONArray("requests").getJSONObject(0).getJSONObject("image").getJSONObject("source").put("imageUri", post.getPictureUri());

                                                            Future<String> getImageText = getImageText(executorService, jsonObject);
                                                            while(!getImageText.isDone()){
                                                                Log.d(TAG, "Converting your image ... ");
                                                            }
                                                            String result = getImageText.get();
                                                            System.out.println("Textul : " +result);
                                                            Utils.mDatabase.child("posts").child(classId).child(post.getId()).child("convertedPicture").setValue(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressBar.dismiss();
                                                                    Toast.makeText(App.getInstance(), "Upload Success", Toast.LENGTH_LONG);
                                                                    finish();
                                                                }
                                                            });
                                                            }catch(Exception ex){

                                                            }


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

    private Future<String> getImageText (ExecutorService executor, JSONObject jsonObject){
        return executor.submit(()->{
            URL serverUrl = null;
            try {
                serverUrl = new URL(TARGET_URL + API_KEY);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection urlConnection = null;
            try {
                urlConnection = serverUrl.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
            try {
                httpConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);
            try {
                BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                        OutputStreamWriter(httpConnection.getOutputStream()));
                httpRequestBodyWriter.write
                        (jsonObject.toString());
                httpRequestBodyWriter.close();
                String response = httpConnection.getResponseMessage();
                if (httpConnection.getInputStream() == null) {
                    System.out.println("No stream");
                    return null;
                }

                Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
                String resp = "";
                while (httpResponseScanner.hasNext()) {
                    String line = httpResponseScanner.nextLine();
                    resp += line;
                    //   System.out.println(line);  //  alternatively, print the line of response
                }
                httpResponseScanner.close();
                //System.out.println("raspunsul : " + resp);
                JSONObject respJson = new JSONObject(resp);
                String result = respJson.getJSONArray("responses").getJSONObject(0)
                        .getJSONArray("textAnnotations").getJSONObject(0).get("description").toString();
                System.out.println("Raspuns in functie : " + result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
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
