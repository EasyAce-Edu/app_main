package com.jingtao.app.network;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.jingtao.app.data.Compressor;
import com.jingtao.app.data.MessageDTO;
import com.jingtao.app.data.Question;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observer;

/**
 * Created by maoningguo on 2015-10-06.
 */
public class HighLevelDownloadService {

    private final String s3paths;
    private final Context context;

    public HighLevelDownloadService(String s3paths, Context context) {
        this.s3paths = s3paths;
        this.context = context;
    }

    public void processS3download() {
        AmazonS3 s3 = new AmazonS3Client(
                new BasicAWSCredentials("123", "456"));
        String s3key = "xxxxx";
        TransferUtility transferManager = new TransferUtility(s3, context);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path + "/download/image.zip");
        TransferObserver observer = transferManager.download("testmaoninguo",s3key,file);

        observer.setTransferListener(new TransferListener() {


            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.d("state","downloaded");

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
    }
}
