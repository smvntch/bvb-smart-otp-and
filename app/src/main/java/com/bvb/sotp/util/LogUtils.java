package com.bvb.sotp.util;

import android.os.Environment;
import android.util.Log;

import com.centagate.module.common.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogUtils {

    public static void printLog(String method, String log) {

//        if (!log.isEmpty()) {
//            return;
//        }
        try {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, "log_smvn.txt");
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("\n");
            myOutWriter.append("----------------------------------------------------------------------------\n");
            myOutWriter.append(method + " " + formattedDate + "\n");
            myOutWriter.append(log);
            myOutWriter.append("----------------------------------------------------------------------------\n");

            myOutWriter.close();
            fOut.close();


        } catch (Exception e) {

        }
    }
}
