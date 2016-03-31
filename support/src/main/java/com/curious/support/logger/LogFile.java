package com.curious.support.logger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/24.
 */
public class LogFile implements LogNode {
    // For piping:  The next node to receive Log data after this one has done its work.
    private LogNode mNext;

    /**
     * Returns the next LogNode in the linked list.
     */
    public LogNode getNext() {
        return mNext;
    }

    /**
     * Sets the LogNode data will be sent to..
     */
    public void setNext(LogNode node) {
        mNext = node;
    }

    private File DEFAULT_LOG;


    public LogFile() {
        DEFAULT_LOG = getOutputLogFile(null);


    }


    public LogFile(Context context) {
        DEFAULT_LOG = getOutputLogFile(context);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            stringBuilder.append("============================================================\n");
            stringBuilder.append("      ").append(context.getPackageName()).append("---V").append(packInfo.versionName);
            stringBuilder.append("\n==========================================================\n");
            writeLogToFile(DEFAULT_LOG, stringBuilder.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {

        if (DEFAULT_LOG != null) {
            writeLogToFile(DEFAULT_LOG, tag + ">---" + msg);
        }


        if (mNext != null) {
            mNext.println(priority, tag, msg, tr);
        }
    }

    private void writeLogToFile(File output, String log) {
        BufferedWriter writer = null;
        try {

            writer = new BufferedWriter(new FileWriter(output, true));
            writer.write("\n");
            writer.write(log);
            writer.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    static File getOutputLogFile(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        File logStorageDir = null;

        if (context != null) {
            logStorageDir = context.getExternalFilesDir(null);
        } else {
            logStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "Express");

            // Create the storage directory if it does not exist
            if (!logStorageDir.exists()) {
                if (!logStorageDir.mkdirs()) {
                    android.util.Log.d("Express", "failed to create directory");
                    //TODO create log directory
                    return null;
                }
            }
        }

        if (logStorageDir == null) {
            return null;
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File logFile;
        logFile = new File(logStorageDir.getPath() + File.separator +
                "LOG_" + timeStamp + ".txt");

        return logFile;
    }
}
