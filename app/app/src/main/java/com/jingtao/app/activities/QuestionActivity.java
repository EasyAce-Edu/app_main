package com.jingtao.app.activities;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.jingtao.app.data.Compressor;
import com.jingtao.app.data.MessageDTO;
import com.jingtao.app.data.Question;
import com.jingtao.app.network.HighLevelUploadService;
import com.jingtao.app.views.ImageDisplay;
import com.jingtao.app.views.SoundView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.provider.Settings.Secure;

import org.joda.time.DateTime;
import com.jingtao.app.R;


public class QuestionActivity extends Activity {

    private static final String FOLDER = "QA";
    private static final String TEMP_FOLDER = "QA/TEMP";

    private static  String logtag = "CameraApp";
    private static int TakePicture = 1;
    private static int pickPicture = 2;
    private static int DisplayImage = 3;
    private Uri imageUri;
    private HashMap<Integer, Uri>  URIs=new HashMap<Integer, Uri>();
    private int view_id=0;
    private String selected_subject="undefined";
    private String hint_type="undefined";
    String[] subjects = new String[] {"Calculus", "Stat", "Computer Science","Linear Algebra"};
    ArrayAdapter<String> adapter;

    private SoundView soundView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        ImageView add_icon=(ImageView)findViewById(R.id.add_icon);
        add_icon.setImageResource(R.mipmap.ic_camera);
        add_icon.setOnClickListener(upload_photo);
        Button subject_btn=(Button)findViewById(R.id.select_subject);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, subjects);
        subject_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                subject_dialog();
            }
        });
        Button btn_send=(Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(send_question);
        Button btn = (Button) findViewById(R.id.btn_hint);
        btn.setSelected(true);

        ImageButton btnBack = (ImageButton)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });



        //get soundView
        soundView = (SoundView)findViewById(R.id.sound_view);
    }

    private void clearTempFolder() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                TEMP_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }
    }

    private OnClickListener send_question=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selected_subject.equals("undefined")||hint_type.equals("undefined")){
                AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
                alertDialog.setTitle("Cannot send question");
                alertDialog.setMessage("Make sure your subject and hint/full sollution is selected");
                alertDialog.show();
                return;
            }
            clearTempFolder();
            String folderPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    TEMP_FOLDER).getAbsolutePath();

            //put files into temp folder
            for ( int key : URIs.keySet() ) {
                Uri image_uri=URIs.get(key);
                Bitmap bitmap;
                //String filename=key+".jpg";
                File imageFile = new File(folderPath, key + ".jpg");

                FileOutputStream out = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                    out = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                }catch(Exception e){
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //put sound clip into temp folder
            String target = new File(folderPath, "0.3gp").getAbsolutePath();
            if (soundView.getSoundClipPath() != null) {
                try {
                    File from = new File(soundView.getSoundClipPath());
                    File to = new File(target);
                    FileInputStream in = new FileInputStream(from);
                    FileOutputStream out = new FileOutputStream(to);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File dir = new File(folderPath);
            File[] directoryListing = dir.listFiles();
            int size = directoryListing.length;
            String[] filesPaths = new String[size];
            int i = 0;
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    filesPaths[i] = child.getAbsolutePath();
                    i++;
                }
            }
            String finalPath = folderPath + "/zipfile.zip";
            Compressor compressor = new Compressor(filesPaths,finalPath);
            compressor.zip();

            File uploadfile = new File(finalPath);
            // initiate the upload
            String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                    Secure.ANDROID_ID);
            MessageDTO messageDTO = new MessageDTO(((EditText)findViewById(R.id.text)).getText().toString(), "");
            Question question = new Question(selected_subject,android_id,messageDTO);

            HighLevelUploadService highLevelUploadService = new HighLevelUploadService(getApplicationContext(),uploadfile,md5(uploadfile.getName()+ DateTime.now().toString()),question);
            TransferObserver upload = highLevelUploadService.processS3Service();
            TransferState state = upload.getState();
            AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
            alertDialog.setTitle("Uploading");
            alertDialog.setMessage("Please wait");
            alertDialog.show();

            //now zip folderPath, upload zipped file to cloud
            //and post question to server
        }
    };

    private void subject_dialog(){
        new AlertDialog.Builder(this)
                .setTitle("Select A Subject")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_subject=subjects[which];
                        Button subject_btn=(Button)findViewById(R.id.select_subject);
                        subject_btn.setText(selected_subject);
                        dialog.dismiss();
                    }
                }).create().show();
    }


    protected void onSaveInstanceState(Bundle extra) {
        super.onSaveInstanceState(extra);
        extra.putSerializable("HashMap", URIs);
        extra.putString("string", URIs.toString());
    }
    protected void onRestoreInstanceState(Bundle extra){
        //TextView uri_text= (TextView)findViewById(R.id.uri_text);
        URIs= (HashMap) extra.getSerializable("HashMap");
        //uri_text.setText(extra.getString("string"));//DEBUG#####
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


    private OnClickListener upload_photo = new OnClickListener() {
        public void onClick(View v) {
            startDialog();
        }
    };

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, pickPicture);
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        String picname = System.currentTimeMillis()+".jpg";
                        File photo = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), picname);
                        imageUri = Uri.fromFile(photo);
                        Log.e(logtag, imageUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TakePicture);
                    }
                });
        myAlertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LinearLayout ll = (LinearLayout)findViewById(R.id.image_views);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 20, 0);
        switch (requestCode) {
            case 1://take picture
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView= new ImageView(this);
                    imageView.setAdjustViewBounds(true);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    imageView.setOnClickListener(imageClickerListener);
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large,450,450,true);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        ll.addView(imageView);
                        Toast.makeText(getBaseContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
            case 2://pick from gellary
                if(resultCode == RESULT_OK){
                    Uri selectedImage = intent.getData();
                    Bitmap bitmap;
                    Bitmap bitmap_large;
                    try {
                        bitmap_large = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large, 400, 400, true);
                        ImageView imageView= new ImageView(this);
                        imageView.setAdjustViewBounds(true);
                        imageView.setImageBitmap(bitmap);
                        imageView.setLayoutParams(lp);
                        imageView.setOnClickListener(imageClickerListener);
                        imageView.setId(view_id);
                        URIs.put(view_id, selectedImage);
                        view_id++;
                        ll.addView(imageView);
                    } catch (Exception e) {
                        Log.e(logtag, e.toString());
                    }
                }
                break;
            case 3://display image
                try {
                    int image_id = intent.getIntExtra("delete_id", 0);
                    ImageView imageview = (ImageView) findViewById(image_id);
                    imageview.setVisibility(View.GONE);
                    URIs.remove(image_id);
                    break;
                }catch(Exception e) {
                }
        }
        check_image_limit();
    }

    private OnClickListener imageClickerListener=new OnClickListener(){
        public void onClick(View v) {
            Intent display = new Intent(getBaseContext(), ImageDisplay.class);
            display.putExtra("uri",URIs.get(v.getId()).toString());
            display.putExtra("id",v.getId());
            startActivityForResult(display, DisplayImage);

        }
    };

    private void check_image_limit(){
        ImageView add=(ImageView)findViewById(R.id.add_icon);
        if(URIs.size()>=3){
            add.setVisibility(View.INVISIBLE);
        }else{
            add.setVisibility(View.VISIBLE);
        }

    }

    public void changeColor(View view){
        Button btn = (Button)view;
        Button btn2;
        btn.setSelected(true);
        if(btn.getId() == R.id.btn_hint) {
            btn2 = (Button) findViewById(R.id.btn_fullSol);
            hint_type="2";
        }
        else {
            btn2 = (Button) findViewById(R.id.btn_hint);
            hint_type="1";
        }
        btn2.setSelected(false);
        setColor(btn);
        setColor(btn2);
    }

    private void setColor(Button btn){
        if(btn.isSelected()) {
            btn.setBackgroundColor(Color.parseColor("#157efb"));
            btn.setTextColor(Color.parseColor("#ffffff"));
            btn.setSelected(true);
        }
        else {
            btn.setBackgroundColor(Color.parseColor("#ffffff"));
            btn.setTextColor(Color.parseColor("#110000"));
            btn.setSelected(false);
        }
    }

    private String md5(String s) {
        try {
            // create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
