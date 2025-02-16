package com.bvb.sotp.firebase;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bvb.sotp.Constant;
import com.bvb.sotp.PeepApp;
import com.bvb.sotp.realm.MobilePushRealmModel;
import com.centagate.module.device.FileSystem;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import com.bvb.sotp.R;
import com.bvb.sotp.helper.PreferenceHelper;
import com.bvb.sotp.screen.authen.login.LoginActivity;
import com.bvb.sotp.screen.main.PushEvent;
import com.bvb.sotp.screen.splash.SplashActivity;

import io.realm.Realm;

public class SoftOTPFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
        preferenceHelper.setDeviceToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("-----------------------" + remoteMessage.toString());
        System.out.println("-----------------------" + remoteMessage.getData().toString());
        // String dataObj = remoteMessage.getData().get("message");
        // System.out.println("------------------------" + dataObj);
        Log.e("dataChat", remoteMessage.getData().toString());
        JSONObject jsonObject = null;
        Map<String, String> params = remoteMessage.getData();
        try {

            jsonObject = new JSONObject(params);

            String message = jsonObject.getString("message");
            saveNoti(jsonObject.getString("sessioncode"));
            if (message.contains("#2004") || message.contains("#2005")) {
                FileSystem fileSystem = new FileSystem();
                fileSystem.deleteAccountFile(getString(R.string.file_name));
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (isAppIsInBackground(getApplicationContext())) {
            setupNotification(params);

        } else {

            try {
//                setupNotification(params);

                PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
                preferenceHelper.setSession(jsonObject.getString("sessioncode"));
                preferenceHelper.setName(jsonObject.getString("username"));
                preferenceHelper.setAccountId(jsonObject.getString("accountid"));
                preferenceHelper.setIsNotification(true);

                EventBus.getDefault().post(new PushEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
//        LogUtils.printLog();

    }

    void saveNoti(String message) {
        System.out.println("----saveNoti---------" + message);
        Realm realm = Realm.getDefaultInstance();
        long id = PeepApp.Companion.getMobilePushPrimaryKey().getAndIncrement();

        realm.executeTransactionAsync(realm1 -> {
            MobilePushRealmModel model = realm1.createObject(MobilePushRealmModel.class, id);
            model.date = System.currentTimeMillis();
            model.detail = message;
            model.type = Constant.NOTI_TYPE_MOBILE_PUSH;
        });
    }

    private void setupNotification(Map<String, String> dataObj) {
        System.out.println("---setupNotification-------------------------");
        try {
            JSONObject jsonObject = new JSONObject(dataObj);
            String message = jsonObject.getString("message");
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channel_id = createNotificationChannel(getApplicationContext());

            String appName = getString(R.string.app_name);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id)
                    .setSmallIcon(R.drawable.ic_launcher_round)
                    .setContentTitle(appName)
                    .setContentText(message)
                    .setAutoCancel(true);
            //Intent notificationIntent = LoginFragment.Companion.newIntent(getApplicationContext(), "transaction_detail");
            Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
            PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
            preferenceHelper.setSession(jsonObject.getString("sessioncode"));
            preferenceHelper.setName(jsonObject.getString("username"));
            preferenceHelper.setAccountId(jsonObject.getString("accountid"));
            preferenceHelper.setIsNotification(true);
            mBuilder.setColor(getResources().getColor(R.color.transparent));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity
                        (this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity
                        (this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }


            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

            mBuilder.setContentIntent(pendingIntent);
            NotificationManagerCompat.from(this).notify(1, mBuilder.build());
//            if (notificationManager != null) {
//                notificationManager.notify(1, mBuilder.build());
//            }

        } catch (Exception e) {
            e.printStackTrace();

//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            String channel_id = createNotificationChannel(getApplicationContext());
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id)
//                    .setSmallIcon(R.mipmap.ic_launcher_round)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(dataObj.toString())
////                    .setContentText(getString(R.string.notif_content))
//                    .setAutoCancel(true);
//            //Intent notificationIntent = LoginFragment.Companion.newIntent(getApplicationContext(), "transaction_detail");
////                notificationIntent.putExtra("data", "transaction_detail");
//            PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
//            preferenceHelper.setSession(jsonObject.getString("sessioncode"));
//            Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
//
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
//                    notificationIntent, 0);
//            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//            mBuilder.setContentIntent(intent);
//            notificationManager.notify(1, mBuilder.build());
        }
        System.out.println("---setupNotification-----------------end--------");

    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }

    public static String createNotificationChannel(Context context) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "Channel_id";

            // The user-visible name of the channel.
            CharSequence channelName = "BVB_SOTP";
            // The user-visible description of the channel.
            String channelDescription = "BVB SOTP Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
//            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
//            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return "BVB";
        }
    }

}
