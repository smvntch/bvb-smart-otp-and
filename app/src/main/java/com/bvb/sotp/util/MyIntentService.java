package com.bvb.sotp.util;


public class MyIntentService {} /*extends GcmListenerService {

    //    @Override
    public void onMessageReceived(String from, Bundle data) {

        String dataObj = data.getString("message");
        System.out.println("------------------------" + dataObj);

        if (isAppIsInBackground(getApplicationContext())) {
            setupNotification(dataObj);
        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(dataObj);
//                String message = jsonObject.getString("message");
                setupNotification(dataObj);

                PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
                preferenceHelper.setSession(jsonObject.getString("sessioncode"));
                preferenceHelper.setName(jsonObject.getString("message"));
                preferenceHelper.setIsNotification(true);

                EventBus.getDefault().post(new PushEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        LogUtils.printLog();


//        System.out.println("------------------------" + message);
////        System.out.println("------------------------" + data.toString());
////        System.out.println("------------------------" + from);
//
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_custom_notification);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "OCB_CN_ID")
//                .setSmallIcon(R.drawable.ic_ocb_logo_rounded)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.notif_content))
//                .setAutoCancel(true);
//
//// Creates an explicit intent for an Activity in your app
////        Intent resultIntent = new Intent(getApplicationContext(), TransactionDetailActivity.class);
////
////        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
////// Adds the back stack for the Intent (but not the Intent itself)
//////        stackBuilder.addParentStack(TransactionDetailActivity.class);
////// Adds the Intent that starts the Activity to the top of the stack
////        stackBuilder.addNextIntent(resultIntent);
////
////
////        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
////                0,
////                PendingIntent.FLAG_UPDATE_CURRENT
////        );
//
//        Intent notificationIntent = new Intent(getApplicationContext(), TransactionDetailActivity.class);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
//                notificationIntent, 0);
//
//        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
//        mBuilder.setContentIntent(intent);
//        mBuilder.setCustomBigContentView(remoteViews);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////        as NotificationManager;
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(1000, mBuilder.build());

    }

    private void setupNotification(String dataObj) {
        try {
            JSONObject jsonObject = new JSONObject(dataObj);
            String message = jsonObject.getString("message");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channel_id = createNotificationChannel(getApplicationContext());


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id)
                    .setSmallIcon(R.drawable.ic_ocb_logo_rounded)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
//                    .setContentText(getString(R.string.notif_content))
                    .setAutoCancel(true);
            Intent notificationIntent = LoginFragment.Companion.newIntent(getApplicationContext(), "transaction_detail");

            PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
            preferenceHelper.setSession(jsonObject.getString("sessioncode"));

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);

            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

            mBuilder.setContentIntent(intent);
            if (notificationManager != null) {
                notificationManager.notify(1, mBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channel_id = createNotificationChannel(getApplicationContext());

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel_id)
                    .setSmallIcon(R.drawable.ic_ocb_logo_rounded)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(dataObj)
//                    .setContentText(getString(R.string.notif_content))
                    .setAutoCancel(true);
            Intent notificationIntent = LoginFragment.Companion.newIntent(getApplicationContext(), "transaction_detail");
//                notificationIntent.putExtra("data", "transaction_detail");
//            PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
//            preferenceHelper.setSession(jsonObject.getString("sessioncode"));
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);
            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
            mBuilder.setContentIntent(intent);
            notificationManager.notify(1, mBuilder.build());
        }
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
            CharSequence channelName = "OCB_iOTP";
            // The user-visible description of the channel.
            String channelDescription = "OCB iOTP Alert";
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
            return "OCB";
        }
    }
}*/