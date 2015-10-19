package com.jingtao.app.question_detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingtao.app.MainActivity;
import com.jingtao.app.SaveQuestion;
import com.jingtao.app.main_page_list_view.Model;
import com.jingtao.app.R;
import com.jingtao.app.main_page_list_view.QuestionTag;

import org.json.JSONArray;
import org.json.JSONObject;


public class QuestionDetail extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Intent intent = getIntent();
        final Model model =(Model) intent.getSerializableExtra("model");
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
        String Subject = model.getSubject();
        if(Subject.toLowerCase().substring(0,1).equals("m")) {
            subimg.setImageResource(R.mipmap.ic_m);
        }else if(Subject.substring(0, 1).toLowerCase().equals("s")){
            subimg.setImageResource(R.mipmap.ic_s);
        }else if(Subject.substring(0,1).toLowerCase().equals("c")) {
            subimg.setImageResource(R.mipmap.ic_c);
        }else{
            subimg.setBackgroundColor(Color.parseColor("#5ca8cd"));
        }
        //Log.e("info", model.getMsglst());//DEBUG
        LinearLayout container = (LinearLayout)findViewById(R.id.container);
        try {
            JSONArray msgArr = new JSONArray(model.getMsglst());
            Boolean QuestionAsker;
            Log.e("info",msgArr.toString());
            for(int i=0;i<msgArr.length();i++) {
                QuestionAsker=((JSONObject)msgArr.get(i)).get("sentBy").equals(model.getAskedby());
                MessageView msgView = new MessageView(this, ((JSONObject) msgArr.get(i)),QuestionAsker);
                container.addView(msgView);
            }
        }catch(Exception e){
            Log.e("Exception",e.toString());
        }
        //deal with tags
        ImageButton btn_tag=(ImageButton)findViewById(R.id.btn_tag);
        if(model.IsStudent()) {
            ((TextView) findViewById(R.id.Tags)).setText("Tags: " + model.getTag().toString());
            SaveQuestion sq = new SaveQuestion(this, model);
            if ((sq.checksaved()) == null) {//not saved
                btn_tag.setVisibility(View.GONE);
            } else{
                btn_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuestionDetail.this, QuestionTag.class);
                        intent.putExtra("id", model.getId());
                        startActivityForResult(intent, 1);
                    }
                });
            }
        }else{
            btn_tag.setVisibility(View.GONE);
            TextView Tag_tv = (TextView)findViewById(R.id.Tags);
            Tag_tv.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)asker.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.subject_img);
            asker.setLayoutParams(params);
        }
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
