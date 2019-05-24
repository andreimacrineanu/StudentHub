package com.project.studenthub.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.studenthub.Models.Class;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassesSpinnerAdapter extends ArrayAdapter<Class> {

    private Context context;
    private List<Class> classes;


    public ClassesSpinnerAdapter(Context context, int resource, List<Class> classes) {
        super(context, resource);
        this.context = context;
        this.classes = classes;

    }

    @Override
    public int getCount(){
        return classes.size();
    }

    public Class getItem(int count){
        return classes.get(count);
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(classes.get(position).getClassInfo().getValue());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(classes.get(position).getClassInfo().getValue());

        return label;
    }
}
