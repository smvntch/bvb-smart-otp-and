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

import com.bvb.sotp.BuildConfig;
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
    //    tuanld
//    This method is called when a new token is created or an existing token has been updated on the device.
    public void onNewToken(String s) {
        // Call the parent class's onNewToken() method
        super.onNewToken(s);
        if (BuildConfig.DEBUG) {
            Log.d("FCM Token", "Token" + s);
        }
//06/03: Sau khi kích hoạt thì đây là log của FCM Token:
//        2024-03-06 18:02:50.353 16279-16925 FCM Token               com.bvb.sotp.smvn                    D  TokeneWg3GfBuTfeOeKgLlmgsOX:APA91bEe1ETFvzBprILINILLBJsOlHRLiPx3lD_JDxZrSGfCkFcyx6taw_qfqYlSRw8t8MeN6usNUHlSf0dcmzj8xrG_QT5swVLoI89w_kFWgTV69TlGWHHtuy6GDrdzKDpjryMMPjNQ
//        Còn đây là log ghi nhận trên centagate khi thực hiện lệnh push:
//        Push message to eWg3GfBuTfeOeKgLlmgsOX:APA91bEe1ETFvzBprILINILLBJsOlHRLiPx3lD_JDxZrSGfCkFcyx6taw_qfqYlSRw8t8MeN6usNUHlSf0dcmzj8xrG_QT5swVLoI89w_kFWgTV69TlGWHHtuy6GDrdzKDpjryMMPjNQ successful
//        Hàm này là hàm khi kích hoạt sẽ sinh ra FCM Token và cập nhật vào PreferenceHelper. FCM Token này dùng để thực hiện push
//        Create a PreferenceHelper object to manage application preferences.
//        PreferenceHelper is a support class created to simplify the process of storing and retrieving values in an application's SharedPreferences.
        PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
        //        Use PreferenceHelper to save the new token to your application's SharedPreferences.
//        The setDeviceToken() method can be defined in PreferenceHelper to store the token value into SharedPreferences as a key-value pair.
//            In this case, the new token value passed to the method is s.
        preferenceHelper.setDeviceToken(s);

    }

    @Override
    // onMessageReceived(RemoteMessage remoteMessage) method is called when a message from the Firebase Cloud Messaging (FCM) server is received by the device
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // call the parent class's onMessageReceived() method to ensure that the parent class's initialization operations are performed properly
        super.onMessageReceived(remoteMessage);
        // Print out remoteMessage information to the console for debugging
        System.out.println("-----------------------" + remoteMessage.toString());
        // Print out message data from remoteMessage to the console for debugging
        System.out.println("-----------------------" + remoteMessage.getData().toString());
        // String dataObj = remoteMessage.getData().get("message");
        // System.out.println("------------------------" + dataObj);
        Log.e("dataChat", remoteMessage.getData().toString());
        // Initializes a JSONObject object to parse message data.
        JSONObject jsonObject = null;
        //        Get message data from remoteMessage and save it into a map with key and value of type String.
//        This data often contains parameters and custom values sent from the server.
        Map<String, String> params = remoteMessage.getData();
        try {
// Parse the message data from map params into a JSONObject object for easier retrieval and processing.
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
        //      Check if the app is running in the background by calling the isAppIsInBackground() method.
//      If the application is running in the background, display notification notifications by calling the setupNotification(params) method.
//      If not, save the session information to SharedPreferences using PreferenceHelper, then subscribe to an EventBus event to notify other application components about this event.

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
    // Declare the saveNoti method, which takes a message parameter of type String and returns no value (void).
    void saveNoti(String message) {
        System.out.println("----saveNoti---------" + message);
        // Create a Realm object to interact with the Realm database.
        Realm realm = Realm.getDefaultInstance();
        //        long id = PeepApp.Companion.getMobilePushPrimaryKey().getAndIncrement();: Retrieves a primary key for the new entry in the Realm database.
//        The getMobilePushPrimaryKey() method is called from the PeepApp.Companion object, through the getAndIncrement() method we ensure that each new entry has a unique primary key.
        long id = PeepApp.Companion.getMobilePushPrimaryKey().getAndIncrement();
//        realm.executeTransactionAsync(realm1 -> { ... }: Opens an asynchronous Realm transaction to add new data to the database.
//        The statements in the lambda expression (realm1 -> { ... }) will be executed in the body of the transaction.
        realm.executeTransactionAsync(realm1 -> {
            // Create a new object of the MobilePushRealmModel class in the Realm database with the primary key being the id obtained earlier.
            MobilePushRealmModel model = realm1.createObject(MobilePushRealmModel.class, id);
            // Assign the current time value to the date field of the model object.
            model.date = System.currentTimeMillis();
            // Assign the value of the message parameter to the detail field of the model object.
            model.detail = message;
            // Assign the value Constant.NOTI_TYPE_MOBILE_PUSH to the type field of the model object
            model.type = Constant.NOTI_TYPE_MOBILE_PUSH;
        });
    }
    // The setupNotification function is used to create and display notifications on the device based on data passed in the form of Map<String, String>.
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
    // Used to check if the application is running in the background or not.
    private boolean isAppIsInBackground(Context context) {
        // Initialize isInBackground variable: This variable is initially initialized to true, assuming that the application is running in the background.
        boolean isInBackground = true;
        // Use context.getSystemService(Context.ACTIVITY_SERVICE) to retrieve an ActivityManager, from which you can manage activities in the system.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // Use am.getRunningAppProcesses() to get a list of processes running on the device.
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        // If there is a current application process running in the foreground state, then set the isInBackground variable to false.
                        isInBackground = false;
                    }
                }
            }
        }
// Finally, returns the value of the isInBackground variable, which represents whether the application is running in the background or not.
        return isInBackground;
    }
    //    The createNotificationChannel function is used to create a new notification channel on devices running Android Oreo (API 26) and above.
//    Notification channels are a feature introduced from Android Oreo for more flexible notification management and configuration.
//    Using notification channels, apps can control how notifications are displayed and prioritized on the device.
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
