package com.jingtao.app.main_page_list_view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jingtao.app.R;
import com.jingtao.app.SaveQuestion;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class QuestionTag extends Activity {
    private ArrayList<String> tags;
    private TagArrayAdapter TAdapter;
    private ListView listview;
    private ArrayList<Model> questions;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_tag);
        ImageButton back = (ImageButton) findViewById(R.id.btn_backTolist);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SaveQuestion sq = new SaveQuestion(this);
         questions = sq.ReadSavedQuestion();
         position = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(id)) {
                position = i;
            }
        }
        tags = questions.get(position).getTag();
        TAdapter = new TagArrayAdapter(this, R.layout.tag_row,tags);
        listview = (ListView)findViewById(R.id.tag_list);
        listview.setAdapter(TAdapter);
        Button add = (Button)findViewById(R.id.add_tag);
        final EditText edit = (EditText)findViewById(R.id.edit_tag);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TAdapter.add(edit.getText().toString());
                edit.setText("");
                questions.get(position).setTag(TAdapter.getTags());
                SaveQuestion sq = new SaveQuestion(QuestionTag.this);
                sq.SaveQuestions(questions);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question_tag, menu);
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

    public class TagArrayAdapter extends ArrayAdapter<String> {
        private ArrayList<String> tag = new ArrayList<>();
        private LayoutInflater Inflater;
        private int Layout;


        public TagArrayAdapter(Context context, int textViewResourceId,ArrayList<String> tags) {
            super(context, textViewResourceId,tags);
            this.Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.Layout = textViewResourceId;
            this.tag=tags;
        }
        public ArrayList<String> getTags(){
            return this.tag;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            final String tag_string = this.tag.get(position);

            View view = convertView;
            view = Inflater.inflate(this.Layout, null);
            TextView tag_tv = (TextView) view.findViewById(R.id.tag_tv);
            ImageButton delete_btn = (ImageButton) view.findViewById(R.id.btn_delete);
            tag_tv.setText(tag_string);
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(tag_string);
                }
            });
            return view;

        }



    }
}
