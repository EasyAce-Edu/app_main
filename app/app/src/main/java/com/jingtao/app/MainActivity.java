package com.jingtao.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingtao.app.question_detail.QuestionDetail;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    RelativeLayout leftRL;
    DrawerLayout drawerLayout;
    Boolean IsStudent=true;
    ArrayList<Model> listItems = new ArrayList<>();
    //ArrayList listItems = new ArrayList<>();
    ItemAdapter adapter;
    String userName="user1";
    TextView tv;
    TextView log;
    SwipeRefreshLayout swipeContainer;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        log = (TextView) findViewById(R.id.log);
        leftRL = (RelativeLayout)findViewById(R.id.Drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        findViewById(R.id.btn_switch).setOnClickListener(SwitchToTutor);
        TextView user=(TextView)findViewById(R.id.user);
        user.setText(userName + "\n" + (IsStudent ? "Student" : "Tutor"));
        SetListView(true);
        Button history_btn=(Button)findViewById(R.id.btn_history);
        history_btn.setOnClickListener(search_history);
        Button askQst_QstPool = (Button)findViewById(R.id.btn_QA);
        askQst_QstPool.setOnClickListener(questionAndAnswer);

        //swipeRefresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tv.getText() == "My Qst") SetListView(false);
                        else SetListView(true);
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);
            }
        });

    }

    public void search_view(View view){
        Intent search_question=new Intent(MainActivity.this,SearchQst.class);
        search_question.putExtra("qst_lst",listItems);
        startActivity(search_question);
    }

    View.OnClickListener search_history = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawerLayout.closeDrawer(leftRL);
            SetListView(false);
            tv.setText("My Qst");
            log.setText("");
            log.setBackgroundColor(Color.parseColor("#00000000"));
        }
    };
    View.OnClickListener questionAndAnswer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawerLayout.closeDrawer(leftRL);
            if(IsStudent) {
                //TODO: jump to send question
            }else {
                SetListView(true);
                tv.setText("Qst Pool");
                log.setText("");
                log.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
    };
    protected void SetListView(Boolean QuestionPool){
        //log.setText("Loading, Please wait");
        //log.setBackgroundColor(Color.parseColor("#ffcece"));
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Model model = (Model)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, QuestionDetail.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });
        String getQuestion="https://easyace-api-staging.herokuapp.com/questions";
        if(IsStudent) {
            getQuestion = getQuestion+"?askedBy="+userName;
            tv.setText("My Qst");
        }else {
            if(QuestionPool){
                getQuestion=getQuestion+"?status=open";
                tv.setText("Qst Pool");
            }else{
                getQuestion=getQuestion+"?answeredBy="+userName;
                tv.setText("My Qst");
            }
        }
        listItems = new ArrayList<>();
        new RetrieveQuestion().execute(getQuestion);


        // Defined Array values to show in ListView
        adapter = new ItemAdapter(this, listItems);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }


    View.OnClickListener SwitchToTutor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IsStudent=false;
            drawerLayout.closeDrawer(leftRL);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    leftRL.setBackgroundColor(Color.parseColor("#ecfedd"));
                    findViewById(R.id.Logo).setBackgroundColor(Color.parseColor("#7efb15"));
                    Button btn = (Button) findViewById(R.id.btn_profile);
                    btn.setBackgroundColor(Color.parseColor("#8cfb2e"));
                    btn = (Button) findViewById(R.id.btn_history);
                    btn.setText("My Qst");
                    btn.setBackgroundColor(Color.parseColor("#a8fc60"));
                    btn = (Button) findViewById(R.id.btn_QA);
                    btn.setText("Qst Pool");
                    btn.setBackgroundColor(Color.parseColor("#c3fd92"));
                    btn = (Button) findViewById(R.id.btn_switch);
                    btn.setText("Switch To Student");
                    btn.setBackgroundColor(Color.parseColor("#d1fdab"));
                    btn.setOnClickListener(SwitchToStudent);
                    TextView user=(TextView)findViewById(R.id.user);
                    user.setBackgroundColor(Color.parseColor("#7efb15"));
                    user.setText(userName + "\n" + (IsStudent ? "Student" : "Tutor"));
                    SetListView(false);
                }
            }, 500);
        }
    };
    View.OnClickListener SwitchToStudent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IsStudent=true;
            drawerLayout.closeDrawer(leftRL);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    leftRL.setBackgroundColor(Color.parseColor("#abd1fd"));
                    findViewById(R.id.Logo).setBackgroundColor(Color.parseColor("#157efb"));
                    Button btn=(Button)findViewById(R.id.btn_profile);
                    btn.setBackgroundColor(Color.parseColor("#2e8cfb"));
                    btn=(Button)findViewById(R.id.btn_history);
                    btn.setText("My Qst");
                    btn.setBackgroundColor(Color.parseColor("#479afb"));
                    btn=(Button)findViewById(R.id.btn_QA);
                    btn.setText("Ask Qst");
                    btn.setBackgroundColor(Color.parseColor("#60a8fc"));
                    btn=(Button)findViewById(R.id.btn_switch);
                    btn.setText("Switch To Tutor");
                    btn.setBackgroundColor(Color.parseColor("#79b6fc"));
                    btn.setOnClickListener(SwitchToTutor);
                    TextView user=(TextView)findViewById(R.id.user);
                    user.setBackgroundColor(Color.parseColor("#157efb"));
                    user.setText(userName + "\n" + (IsStudent ? "Student" : "Tutor"));
                    SetListView(false);
                }
            }, 500);
        }
    };

    public  void opendraw(View view)
    {
        drawerLayout.openDrawer(leftRL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    class RetrieveQuestion extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            URL GetQstUrl;
            HttpURLConnection urlConnection;
            InputStream in;
            //String getQuestion="https://easyace-api-staging.herokuapp.com/questions";

        try{
            GetQstUrl = new URL(urls[0]);
            urlConnection = (HttpURLConnection) GetQstUrl.openConnection();
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
                Log.e("app", "exception", e);
            }finally {
                urlConnection.disconnect();
            }
        }catch (Exception e) {
            Log.e("app", "exception", e);
        }
            return null;
        }

        protected void onPostExecute(String str) {
            try {
                JSONObject jsonObject = new JSONObject(str);
                if(!jsonObject.has("data")){
                    log.setText("You Don't have any Questions");
                    log.setBackgroundColor(Color.parseColor("#ffcece"));
                    return;
                }
                JSONArray data = jsonObject.getJSONArray("data");
                PrintJSONList(data);
            }catch (Exception e){
                Log.e("App","string2JsonException",e);
                log.setText("cannot connect to Server, please check your internet connection");
                log.setBackgroundColor(Color.parseColor("#ffcece"));
            }
        }
    }

    protected void PrintJSONList(JSONArray jarr){
        try{
        for(int i=0; i<jarr.length(); i++){
            try {
                JSONObject json_data = jarr.getJSONObject(i);
                JSONObject fstMsg=(JSONObject)json_data.getJSONArray("msgList").get(0);
                listItems.add(new Model(json_data.getString("subject"),fstMsg.getString("textMsg"),
                        json_data.getString("status"),json_data.getString("hintType"),json_data.getString("askedBy"),
                        json_data.getString("_id"),json_data.getString("createdAt"),json_data.getString("updatedAt"),
                        json_data.getJSONArray("msgList").toString(),json_data.getJSONArray("answeredBy").toString()));
                adapter.notifyDataSetChanged();
                log.setText("");
                log.setBackgroundColor(Color.parseColor("#00000000"));
            }catch (Exception e){
                Log.e("app","Jsonarray exception",e);
            }
        }
        }catch (Exception e){
            Log.e("app","Jsonarray exception",e);
            log.setText("Error: Bad Data");
        }

    }
}
