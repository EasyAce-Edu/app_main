package com.jingtao.app;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;

public class ItemAdapter extends ArrayAdapter<Model> {

    private final Context context;
    private final ArrayList<Model> modelsArrayList;

    public ItemAdapter(Context context, ArrayList<Model> modelsArrayList) {

        super(context, R.layout.item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;

            rowView = inflater.inflate(R.layout.item, parent, false);

            // 3. Get icon,title & counter views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.imageView);
            TextView subject_tv = (TextView) rowView.findViewById(R.id.subject_tv);
            TextView text_tv = (TextView) rowView.findViewById(R.id.text_tv);
            ImageView hint=(ImageView) rowView.findViewById(R.id.hint);


            // 4. Set the text for textView
            String Subject = modelsArrayList.get(position).getSubject();
            if(Subject.equals("math")){
                imgView.setImageResource(R.mipmap.ic_m);
            }else{
                imgView.setBackgroundColor(Color.parseColor("#5ca8cd"));
            }
            subject_tv.setText(Subject);
            text_tv.setText(modelsArrayList.get(position).getText());
            if(modelsArrayList.get(position).getHintType()=="1"){
                hint.setImageResource(R.mipmap.ic_hint);
            }else{
                hint.setImageResource(R.mipmap.ic_ans);;
            }



        // 5. retrn rowView
        return rowView;
    }
}