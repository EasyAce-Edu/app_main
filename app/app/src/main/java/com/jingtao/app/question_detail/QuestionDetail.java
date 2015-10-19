package com.jingtao.app.question_detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jingtao.app.main_page_list_view.Model;
import com.jingtao.app.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


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
        String Subject = model.getSubject();
        if(Subject.toLowerCase().substring(0,1).equals("m")) {
            subimg.setImageResource(R.mipmap.ic_m1);
        }else if(Subject.substring(0, 1).toLowerCase().equals("s")){
            subimg.setImageResource(R.mipmap.ic_s1);
        }else if(Subject.substring(0,1).toLowerCase().equals("c")) {
            subimg.setImageResource(R.mipmap.ic_c1);
        }else{
            subimg.setBackgroundColor(Color.parseColor("#5ca8cd"));
        }
        Log.e("info", model.getMsglst());//DEBUG
        LinearLayout container = (LinearLayout)findViewById(R.id.container);
        try {
            JSONArray msgArr = new JSONArray(model.getMsglst());
            Boolean QuestionAsker=true;
            Log.e("info",msgArr.toString());
            for(int i=0;i<msgArr.length();i++) {
                MessageView msgView = new MessageView(this, ((JSONObject) msgArr.get(i)),QuestionAsker);
                QuestionAsker=!QuestionAsker;
                container.addView(msgView);
            }
        }catch(Exception e){
            Log.e("Exception",e.toString());
        }

        Button btn = (Button) findViewById(R.id.btn_answer);
        if(status_str.equals("open") && model.IsStudent() == false) { // why is not open?
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    confirmDialog();
                }
            });
        }
        else {
            btn.setVisibility(View.GONE);
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

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Are you sure to answer this question?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        Model model =(Model) intent.getSerializableExtra("model");
                        String deviceId = Secure.getString(getApplicationContext().getContentResolver(),
                                Secure.ANDROID_ID);
                        String url = "https://easyace-api-staging.herokuapp.com/questions/status?";
                        url += "id=" + model.getId() + "&status=assigned" + "&answeredBy=" + deviceId;
                        String back = "https://easyace-api-staging.herokuapp.com/questions/status?id=56033adcdfc6330300a0f6e2&status=open&answeredBy=79dc3b50aab28fc8";
                        new pickUpQuestion().execute(back);
                        Log.d("updateStatus", back);
                        finish();
                    }
                })
                .show();
    }

    class pickUpQuestion extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            URL GetQstUrl;
            HttpURLConnection urlConnection;
            InputStream in;
            //String getQuestion="https://easyace-api-staging.herokuapp.com/questions";

            try{
                GetQstUrl = new URL(urls[0]);
                urlConnection = (HttpURLConnection) GetQstUrl.openConnection();
                urlConnection.setRequestMethod("POST");
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    return total.toString();
                }catch (Exception e){
                    Log.e("pickup", "exception", e);
                }finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e) {
                Log.e("pickup", "exception", e);
            }
            return null;
        }
    }
}
