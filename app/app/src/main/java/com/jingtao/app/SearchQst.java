package com.jingtao.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchQst extends Activity {
    ArrayList<Model> listItems;
    ArrayList<Model> AllItems;
    ItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_qst);Intent intent = getIntent();
        AllItems  =(ArrayList<Model> ) intent.getSerializableExtra("qst_lst");
        listItems = new ArrayList<>();
        EditText search_field=(EditText)findViewById(R.id.search_field);
        search_field.addTextChangedListener(perform_search);

    }
    TextWatcher perform_search = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = ((EditText)findViewById(R.id.search_field)).getText().toString();
            listItems.clear();
            for(int i=0;i<AllItems.size();i++){
                if(!text.equals("")&&
                        (AllItems.get(i).getText().contains(text)||AllItems.get(i).getSubject().contains(text))){
                        listItems.add(AllItems.get(i));
                }
            }
            adapter = new ItemAdapter(SearchQst.this, listItems);
            ListView listView = (ListView) findViewById(R.id.result_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Model model = (Model) parent.getItemAtPosition(position);
                    Intent intent = new Intent(SearchQst.this, QuestionDetail.class);
                    intent.putExtra("model", model);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_qst, menu);
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

    public void back(View view){
        finish();
    }
}
