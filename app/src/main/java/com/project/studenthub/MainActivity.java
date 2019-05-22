package com.project.studenthub;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.UniversalTimeScale;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.studenthub.Adapters.PostAdapter;
import com.project.studenthub.Models.Image;
import com.project.studenthub.Models.Post;
import com.project.studenthub.Models.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.internal.Util;

import static android.os.Environment.getExternalStoragePublicDirectory;

/*
* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final long ONE_MEGABYTE = 1024 * 1024;
    private Button uploadImageBTN;
    private StorageReference storageReference;
    private ListView postsLV;
    private LinearLayout imagesLL;
    private List<Post> posts;
    private List<Uri> images;
    private PostAdapter postAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toast.makeText(App.getInstance(),"Welcome " + Utils.currentUser.getPrenume() + " !",Toast.LENGTH_SHORT).show();
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        SubMenu classesSubMenu = menu.getItem(0).getSubMenu();
        int k = 1 ;
        Log.d(TAG, "Item submenu " + classesSubMenu.getItem(0).getTitle());
        for(Map.Entry<String, String> studentClass : Utils.currentUser.getClasses().entrySet()){
            Log.d(TAG, "Class : " + studentClass.getValue());
            classesSubMenu.add(0, k,0,studentClass.getValue());
            k++;
        }

        uploadImageBTN = findViewById(R.id.uploadBTN);
        postsLV = findViewById(R.id.postsLV);

        uploadImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App.getInstance(), UploadImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



        //DatabaseReference getClasses = Utils.mDatabase.child("")

        DatabaseReference ref = Utils.mDatabase.child("grupa"+Utils.currentUser.getGrupa());
//        ref.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                posts = new ArrayList<>();
//                images = new ArrayList<>();
//                ExecutorService executorService = Executors.newSingleThreadExecutor();
////                Future<List<String>> getImageNames = getImageName(executorService, dataSnapshot);
//                for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                    for(DataSnapshot child : ds.getChildren()) {
//                        Log.d(TAG, child.getValue(String.class));
//                        StorageReference ref = storageReference.child("images").child("users").child(ds.getKey().toString())
//                                .child(child.getValue(String.class));
//                        Task<Uri> task = storageReference.child("images").child("users").child(ds.getKey().toString())
//                                    .child(child.getValue(String.class)).getDownloadUrl();
//                        while(!task.isComplete()){}
//                        try {
//                            Uri uri = task.getResult();
//                            ImageView iv = new ImageView(App.getInstance());
//                            Log.d(TAG, "Uri : " + uri.toString());
//                            Post newPost = new Post();
//                            newPost.setPicture(iv);
//                            newPost.setTitle("test");
//                            images.add(uri);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//                Log.d(TAG, "Am ajuns aici !!!!!!!!!!!!!!!!!!!! " + images.size());
//                PostAdapter postAdapter = new PostAdapter(getApplicationContext(), images);
//                postsLV.setAdapter(postAdapter);
//                postsLV.setScrollingCacheEnabled(false);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }


    private Future<List<String>> getImageName (ExecutorService executor, DataSnapshot dataSnapshot){
        return executor.submit(()->{
            List<String> imagesNames = new ArrayList<>();
            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                for (DataSnapshot child : ds.getChildren()) {
                    imagesNames.add(child.getValue(String.class));
                }
            }
            return imagesNames;
        });
    }

    private void AddClassesToDrawer (){

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d(TAG, "A apasat pe o materie " + Integer.toString(id));


        if (id == R.id.nav_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Utils.currentUser = new User();
                    Intent intent = new Intent(App.getInstance(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else if(id == 1){
            Log.d(TAG, "A apasat pe o materie !");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
