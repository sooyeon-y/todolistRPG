package com.example.yukkuri.todolistrpg;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {

    private Activity m_context;
    private ArrayList<String> m_arrayList;
    private ArrayList<CheckBox> m_checkBoxList;

    public MyAdapter(Activity context, int textViewResourceId,
                     ArrayList<String> objects) {
        super(context, textViewResourceId, objects);

        m_context = context;
        m_arrayList = objects;
        setCheckBoxList();
    }

    private void setCheckBoxList()
    {
        m_checkBoxList = new ArrayList<CheckBox>();
        for(int i = 0; i < m_arrayList.size(); i++)
        {
            CheckBox checkBox = new CheckBox(m_context);
            checkBox.setFocusable(false);
            checkBox.setClickable(false);
            checkBox.setText(m_arrayList.get(i));
            m_checkBoxList.add(checkBox);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return m_checkBoxList.get(position);
    }

    public void set_checkbox(int position, boolean checked){
        m_checkBoxList.get(position).setChecked(checked);
    }

    public boolean get_checkbox_status(int position){
        if (m_checkBoxList.get(position).isChecked())
            return true;
        else
            return false;
    }

    public String get_todo_string(int position){
        return m_arrayList.get(position);
    }
}
