package com.project.studenthub.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.studenthub.Adapters.ClassesSpinnerAdapter;
import com.project.studenthub.App;
import com.project.studenthub.MainActivity;
import com.project.studenthub.Models.Class;
import com.project.studenthub.R;
import com.project.studenthub.Utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class EnrollClassFragment extends DialogFragment {
    public static interface OnCompleteListener {
        public abstract void onComplete(Map.Entry<String, String> studentClass);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
    private final static String TAG = EnrollClassFragment.class.getSimpleName();
    private Spinner classesSP;
    private ClassesSpinnerAdapter spinnerAdapter;
    private List<Class> classesList1;
    private Button enrollBTN;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        classesSP = view.findViewById(R.id.selectClassSP);
        enrollBTN = view.findViewById(R.id.enrollBTN);
        LoadNotAttendedClasses();

        enrollBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class selectedClass = (Class)classesSP.getSelectedItem();
                Utils.mDatabase.child("users").child(Utils.currentUser.getUID()).child("classes")
                        .child(selectedClass.getClassInfo().getKey()).setValue(selectedClass.getClassInfo()
                        .getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utils.currentUser.getClasses().put(selectedClass.getClassInfo().getKey(), selectedClass.getClassInfo().getValue());
                        mListener.onComplete(selectedClass.getClassInfo());
                        dismiss();
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.enroll_to_class_fragment_layout,container,false);
    }

    public static EnrollClassFragment getInstance (){
        return new EnrollClassFragment();
    }

    private void LoadNotAttendedClasses (){
        classesList1 = new ArrayList<>();
        DatabaseReference classesRef = Utils.mDatabase.child("classes");
        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int contor = 1;
                for (DataSnapshot child : dataSnapshot.getChildren())
                    if (!Utils.currentUser.getClasses().containsKey(child.getKey())){
                        Class newClass = new Class();
                        newClass.setClassInfo(new AbstractMap.SimpleEntry<>(child.getKey(), child.getValue(String.class)));
                        newClass.setCount(contor);
                        classesList1.add(newClass);
                        contor++;
                    }
                Log.d(TAG, "Cursuri : " + classesList1.toString());
                spinnerAdapter = new ClassesSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, classesList1);
                classesSP.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
