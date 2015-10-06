package com.jingtao.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class QuestionDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Intent intent = getIntent();
        Model model =(Model) intent.getSerializableExtra("model");
        TextView sbj=(TextView)findViewById(R.id.subject);
        sbj.setText(model.getSubject());
        ImageButton back =(ImageButton)findViewById(R.id.btn_backTolist);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView createdtime=(TextView)findViewById(R.id.CreatedTime);
        createdtime.setText("Created @ "+model.getCreatedtime()+"\nLast Update @ "+model.getUpdatedtime());
        TextView asker=(TextView)findViewById(R.id.asker);
        asker.setText("Asked by "+model.getAskedby());
        TextView status=(TextView)findViewById(R.id.status);
        String status_str=model.getStatus();
        status.setText("Status: "+status_str);
        if(status_str.equals("open")){
            status.setTextColor(Color.parseColor("#228b22"));
        }else if (status_str.equals("close")){
            status.setTextColor(Color.parseColor("#696969"));
        }
        TextView hint=(TextView)findViewById(R.id.hint);
        ImageView hint_img=(ImageView)findViewById(R.id.hint_img);
        String hinttype=model.getHintType();
        if(hinttype.equals("1")){
            hint.setText("Looking for hint");
            hint_img.setImageResource(R.mipmap.ic_hint);
        }else{
            hint.setText("Looking for full solution");
            hint_img.setImageResource(R.mipmap.ic_ans);
        }
        ImageView subimg=(ImageView)findViewById(R.id.subject_img);
        subimg.setImageResource(R.mipmap.ic_m);
        ((TextView)findViewById(R.id.debug)).setText(model.getMsglst()+"\n"+model.getAnsweredby());
        Log.e("info",model.getMsglst());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
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
}
