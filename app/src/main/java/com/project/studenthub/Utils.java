package com.project.studenthub;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.studenthub.Models.User;

public class Utils {
    public static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("development");
    public static User currentUser;
}
