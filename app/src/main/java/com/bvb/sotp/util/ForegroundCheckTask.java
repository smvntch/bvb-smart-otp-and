package com.bvb.sotp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Context... params) {
        final Context context = params[0].getApplicationContext();
        return isAppOnForeground(context);
    }

    //
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                Log.i("Foreground App", appProcess.processName);

                if (context.getPackageName().equalsIgnoreCase(appProcess.processName)) {
                    Log.i("TAG", "foreground true:" + appProcess.processName);
                    return true;
                    // close_app();
                }
            }
        }
//        Log.d(Constants.TAG, "foreground value:" + foreground);
//        if (foreground) {
//            foreground = false;
//            close_app();
////            Log.i(Constants.TAG, "Close App and start Login Activity:");
//
//        } else {
//            //if not foreground
//            close_app();
//            foreground = false;
////            Log.i(Constants.TAG, "Close App");
//
//        }
        return false;
    }
//    private boolean isAppOnForeground(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        if (appProcesses == null) {
//            return false;
//        }
//        final String packageName = context.getPackageName();
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
//class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {
//
//    @Override
//    protected Boolean doInBackground(Context... params) {
//        final Context context = params[0].getApplicationContext()
//        ActivityManager activityManager = (ActivityManager) get.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                Log.i("Foreground App", appProcess.processName);
//
//                if (context.getPackageName().equalsIgnoreCase(appProcess.processName)) {
////                    Log.i(Constants.TAG, "foreground true:" + appProcess.processName);
//                    foreground = true;
//                    // close_app();
//                }
//            }
//        }
//        Log.d(Constants.TAG, "foreground value:" + foreground);
//        if (foreground) {
//            foreground = false;
//            close_app();
//            Log.i(Constants.TAG, "Close App and start Login Activity:");
//
//        } else {
//            //if not foreground
//            close_app();
//            foreground = false;
//            Log.i(Constants.TAG, "Close App");
//
//        }
//
//        return null;
//    }
//}
