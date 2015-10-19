package com.jingtao.app.main_page_list_view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingtao.app.MainActivity;
import com.jingtao.app.R;
import com.jingtao.app.SaveQuestion;
import com.jingtao.app.main_page_list_view.Model;

import org.json.JSONArray;

public class ItemAdapter extends ArrayAdapter<Model> {

    private final Context context;
    private final ArrayList<Model> modelsArrayList;
    private final boolean drag;

    public ItemAdapter(Context context, ArrayList<Model> modelsArrayList) {

        super(context, R.layout.item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.drag=false;
    }

    public ItemAdapter(Context context, ArrayList<Model> modelsArrayList,boolean drag) {

        super(context, R.layout.item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.drag=drag;
    }

    public ArrayList<Model> getModelsArrayList(){
        return modelsArrayList;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Model model=modelsArrayList.get(position);
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
            RelativeLayout row = (RelativeLayout)rowView.findViewById(R.id.row);
           final ImageView star = (ImageView)rowView.findViewById(R.id.star);


            // 4. Set the text for textView
           JSONArray msgArr= new JSONArray();
           try {
               msgArr = new JSONArray(model.getMsglst());
            }catch (Exception e){
                 e.printStackTrace();
             }
            ((TextView)rowView.findViewById(R.id.msg_size)).setText("Total " + msgArr.length()+" Mssages");
            String Subject = model.getSubject();
            if(Subject.toLowerCase().substring(0,1).equals("m")) {
                imgView.setImageResource(R.mipmap.ic_m1);
            }else if(Subject.substring(0, 1).toLowerCase().equals("s")) {
                imgView.setImageResource(R.mipmap.ic_s1);
            }else if(Subject.substring(0,1).toLowerCase().equals("c")) {
                imgView.setImageResource(R.mipmap.ic_c1);
            }else{
                imgView.setBackgroundColor(Color.parseColor("#5ca8cd"));
            }
            subject_tv.setText(Subject.toUpperCase());
            text_tv.setText(modelsArrayList.get(position).getText());
            if(modelsArrayList.get(position).getHintType().equals("1")){
                hint.setImageResource(R.mipmap.ic_hint);
            }else{
                hint.setImageResource(R.mipmap.ic_ans);
            }
            if(modelsArrayList.get(position).getStatus().equals("close")){
                row.setBackgroundColor(Color.parseColor("#d5d5d5"));
            }
            final SaveQuestion sq=new SaveQuestion(getContext(),model);
            if(model.IsStudent()) {
                if (sq.checksaved()) {
                    star.setImageResource(R.mipmap.ic_star_filled);
                } else {
                    star.setImageResource(R.mipmap.ic_star_unfilled);
                }
                star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sq.checksaved()) {//already in the saved list
                            star.setImageResource(R.mipmap.ic_star_unfilled);
                            sq.delete();
                        } else {//not saved
                            star.setImageResource(R.mipmap.ic_star_filled);
                            sq.save();
                        }
                    }
                });
            }
        if(!drag){
            ImageView handler = (ImageView)rowView.findViewById(R.id.handler);
            handler.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)star.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            star.setLayoutParams(params);

        }else{
            ImageView handler = (ImageView)rowView.findViewById(R.id.handler);
            final Model question = modelsArrayList.get(position);

            // ハンドルタップでソート開始
            handler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ((MainActivity)getContext()).startDrag(question);
                        return true;
                    }
                    return false;
                }
            });

            // ドラッグ行のハイライト
            if (((MainActivity)getContext()).DragQuestion != null && ((MainActivity)getContext()).DragQuestion .equals(question)) {
                row.setBackgroundColor(Color.parseColor("#d4ebf2"));//9933b5e5
            } else {
                row.setBackgroundColor(Color.TRANSPARENT);
            }
        }





        // 5. retrn rowView
        return rowView;
    }
}
