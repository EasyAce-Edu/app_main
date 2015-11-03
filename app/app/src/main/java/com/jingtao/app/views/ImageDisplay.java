package com.jingtao.app.views;

import android.app.ActionBar;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.ActionBar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageDisplay extends AppCompatActivity {
    int imageId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        Intent intent = getIntent();
        Uri myUri = Uri.parse(intent.getStringExtra("uri"));
        imageId=intent.getIntExtra("id",0);
        TouchImageView view = (TouchImageView)findViewById(R.id.image_view);
        view.setImageURI(myUri);
        //ActionBar actionbar=getActionBar();
        //actionbar.setDisplayHomeAsUpEnabled(true);
        ImageButton back =(ImageButton)findViewById(R.id.back);
        ImageButton Del=(ImageButton)findViewById(R.id.delete);
        Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent delete_image = new Intent();
                delete_image.putExtra("delete_id", imageId);
                setResult(RESULT_OK, delete_image);
                finish();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_display, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*switch (item.getItemId()) {
            case R.id.action_delete:
                Intent delete_image = new Intent();
                delete_image.putExtra("delete_id", imageId);
                setResult(RESULT_OK, delete_image);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }
}