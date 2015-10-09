package com.jingtao.app.question_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jingtao.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * Created by jingtao on 15-10-07.
 */
public class MessageView extends LinearLayout {
    protected String MsgText;
    protected String ZipURL;
    protected Boolean QuestionAsker;
    protected ProgressBar pb;
    protected String sentBy;
    MediaPlayer player;
    final MediaPlayer mp = new MediaPlayer();
    public MessageView(Context context,JSONObject msg,Boolean QuestionAsker) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.message_view, this);
        try {
            this.MsgText = msg.getString("textMsg");
            this.ZipURL = msg.getString("zipFileUri");
            this.sentBy = msg.getString("sentBy");
        }catch(Exception e){
            Log.e("Exception", e.toString());
        }
        TextView sender = (TextView)findViewById(R.id.sender);
        ((TextView)findViewById(R.id.messageText)).setText(this.MsgText);
        sender.setText(this.sentBy + ":");
        new DownloadZip().execute(ZipURL);
        this.QuestionAsker=QuestionAsker;
        LinearLayout msg_view = (LinearLayout)findViewById(R.id.MsgView);
        if(this.QuestionAsker){
            msg_view.setBackgroundColor(Color.parseColor("#d4ebf2"));
            sender.setTextColor(Color.parseColor("#1e20ff"));
        }else {
            msg_view.setBackgroundColor(Color.parseColor("#ebf2d4"));
            sender.setTextColor(Color.parseColor("#02d100"));
        }
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setProgress(0);
    }

    class DownloadZip extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            URL zipUrl;
            InputStream in;
            OutputStream output;
            int count;
            long total;
            try {
                String root = Environment.getExternalStorageDirectory().getPath();
                File zip = new File(root + "/QA_app.zip");
                if (zip.exists()){
                    zip.delete();
                }
                zipUrl = new URL(urls[0]);
                URLConnection connection = zipUrl.openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();
                in=new BufferedInputStream(zipUrl.openStream());
                output=new FileOutputStream(zip);
                byte data[] = new byte[1024];

                total = 0;

                while ((count = in.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                }                // Flush output
                output.flush();
                // Close streams
                output.close();
                in.close();
            }catch (Exception e){
                Log.e("Exception",e.toString());
            }

            return null;
        }
        protected void publishProgress(String...progress){
            pb.setProgress(Integer.parseInt(progress[0]));
        }
        protected void onPostExecute(String str) {
            Bitmap bitmap;
            Bitmap bitmap_large;
            try {
                String unzipDest=Environment.getExternalStorageDirectory().getPath()+"/QA_app";
                File folder = new File(unzipDest);
                if (!(folder.exists())) {
                    folder.mkdir();
                }else{
                    String[] children = folder.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(folder, children[i]).delete();
                    }
                    folder.mkdir();
                }
                unpackZip(Environment.getExternalStorageDirectory().getPath(), "/QA_app.zip", "/QA_app");
            }catch(Exception e){
                Log.e("Exception", e.toString());
            }
            for(int i=0;i<5;i++) {
                String img_path = Environment.getExternalStorageDirectory().getPath() + "/QA_app/"+i+".jpg";
                File img=new File(img_path);
                if(img.exists()) {
                    try {
                        ImageView img_view = new ImageView(getContext());
                        Uri img_uri = Uri.fromFile(new File(img_path));
                        LinearLayout imgViews = (LinearLayout) findViewById(R.id.image_views);
                        bitmap_large = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), img_uri);
                        bitmap = Bitmap.createScaledBitmap(bitmap_large, 300, 300, true);
                        img_view.setImageBitmap(bitmap);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(10, 0, 20, 0);
                        img_view.setLayoutParams(lp);
                        imgViews.addView(img_view);
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                }
            }
            final String sound_path = Environment.getExternalStorageDirectory().getPath() + "/QA_app/0.3gp";
            File sound=new File(sound_path);
            if(sound.exists()){
                ImageButton soundBtn=new ImageButton(getContext());
                if(QuestionAsker) {
                    soundBtn.setBackgroundColor(Color.parseColor("#99d0e0"));
                }else {
                    soundBtn.setBackgroundColor(Color.parseColor("#d0e099"));
                }
                soundBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                soundBtn.setImageResource(R.mipmap.ic_speaker);
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,120
                );
                params.setMargins(10, 20,10, 20);
                soundBtn.setLayoutParams(params);
                LinearLayout body = (LinearLayout)findViewById(R.id.msg_body);
                body.addView(soundBtn);
                soundBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        try {
                            mp.reset();
                            mp.setDataSource(sound_path);
                            mp.prepare();
                            mp.start();
                        } catch (IllegalStateException e) {
                            Log.e("Exception",e.toString());
                        } catch (IOException e) {
                            Log.e("Exception", e.toString());
                        }


                    }
                });
            }
            pb.setVisibility(View.GONE);

        }



    }
    private boolean unpackZip(String path, String zipname,String folder)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                FileOutputStream fout = new FileOutputStream(path+folder +"/"+ filename);
                Log.e("Info",path+folder+ "/"+ filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
