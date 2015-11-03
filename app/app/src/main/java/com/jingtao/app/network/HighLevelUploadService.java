package com.jingtao.app.network;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.jingtao.app.data.MessageDTO;
import com.jingtao.app.data.Question;

import java.io.File;

import lombok.Data;

/**
 * Created by maoningguo on 2015-09-27.
 */
@Data
public class HighLevelUploadService {

    private final Context context;
    private final File file;
    private final String hashId;
    private final Question questionSetting;

    public HighLevelUploadService(Context context, File file, String hashId, Question questionSetting) {
        this.context = context;
        this.file = file;
        this.hashId = hashId;
        this.questionSetting = questionSetting;
    }


    public TransferObserver processS3Service() {


        AmazonS3 s3 = new AmazonS3Client(
                new BasicAWSCredentials("123", "456"));

        TransferUtility transferManager = new TransferUtility(s3, context);
        // Starts a download
        String name = file.getName();



        final TransferObserver observer = transferManager.upload("testmaoninguo", this.hashId, file);

        observer.setTransferListener(new TransferListener() {


            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("State:", "Id: " + hashId + ", " + "state: " + state.toString());
                String s3Location = "https://s3.amazonaws.com/testmaoninguo/" + hashId;
                if (state == TransferState.COMPLETED) {
                    MessageDTO messagedto = new MessageDTO(questionSetting.getMessageDTO().getTextMsg(),s3Location);
                    questionSetting.setMessageDTO(messagedto);
                    //  MessageDTO messageDTO = new MessageDTO("testText", s3Location);
                    //  Question question = new Question("math", "testPerson", messageDTO);
                    HttpService post = new HttpService("https://easyace-api-staging.herokuapp.com/questions", "POST", questionSetting);
                    post.start();
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Log.d("Id: ", +id + ", " + "percentage: " + percentage);
                //Display percentage transfered to user
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("hehe", "error: ", ex);
            }

        });
        return observer;
    }


}
