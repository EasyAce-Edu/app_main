package com.jingtao.app;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity {

    RelativeLayout leftRL;
    RelativeLayout Drawer;
    DrawerLayout drawerLayout;
    Boolean IsStudent=true;
    ArrayList listItems = new ArrayList<>();
    ArrayAdapter<String> ListViewAdapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      I'm removing the ActionBar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        leftRL = (RelativeLayout)findViewById(R.id.Drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        findViewById(R.id.btn_switch).setOnClickListener(SwitchToTutor);
        SetListView();
    }
    protected void SetListView(){
        ListView listView = (ListView) findViewById(R.id.list);
        String getQuestion="https://easyace-api-staging.herokuapp.com/questions";
        new RetrieveQuestion().execute(getQuestion);


        // Defined Array values to show in ListView
        ListViewAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listItems);


        // Assign adapter to ListView
        listView.setAdapter(ListViewAdapter);
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
                    btn.setText("Switch To Student");
                    btn.setBackgroundColor(Color.parseColor("#79b6fc"));
                    btn.setOnClickListener(SwitchToTutor);
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
            String getQuestion="https://easyace-api-staging.herokuapp.com/questions";

        try{
            GetQstUrl = new URL(getQuestion);
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
                TextView tv = (TextView) findViewById(R.id.textView);
                JSONObject jsonObject = new JSONObject(str);
                JSONArray data = jsonObject.getJSONArray("data");
                tv.setText(data.toString());
                PrintJSONList(data);
            }catch (Exception e){
                Log.e("App","string2JsonException",e);
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText("Error: Bad Data or connection problem");
            }
        }
    }

    protected void PrintJSONList(JSONArray jarr){
        try{
        for(int i=0; i<jarr.length(); i++){
            try {
                JSONObject json_data = jarr.getJSONObject(i);
                listItems.add(json_data.getString("subject") + json_data.getString("createdAt"));
                ListViewAdapter.notifyDataSetChanged();
            }catch (Exception e){
                Log.e("app","Jsonarray exception",e);
            }
        }
        }catch (Exception e){
            Log.e("app","Jsonarray exception",e);
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText("Error: Bad Data");
        }

    }
}
