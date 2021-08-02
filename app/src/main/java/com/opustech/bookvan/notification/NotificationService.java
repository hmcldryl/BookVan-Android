package com.opustech.bookvan.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.opustech.bookvan.R;

import java.util.HashMap;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    String title, message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey("title") && data.containsKey("message")) {
            title = data.get("title");
            message = data.get("message");

            Log.d("TEST", "TYPE: NOTIFICATION SENT FROM APP");
            Log.d("TEST", "TITLE: " + title);
            Log.d("TEST", "MESSAGE: " + message);

            String channelId = "activity_notification";
            Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notif_sound);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_icon_book)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(defaultSoundUri)
                            .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Activity Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        } else if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();

            Log.d("TEST", "TYPE: NOTIFICATION SENT FROM FIREBASE CONSOLE");
            Log.d("TEST", "TITLE: " + title);
            Log.d("TEST", "MESSAGE: " + message);

            String channelId = "system_notification";
            Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.notif_sound);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_icon_book)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSound(defaultSoundUri)
                            .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "System Notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        } else {
            Log.d("TEST", "TYPE: NOTIFICATION DOES NOT MATCH ANY GIVEN CONDITIONS");
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                updateToken(task.getResult());
                            }
                        }
                    }
                });
    }

    private void updateToken(String token) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);
        FirebaseFirestore.getInstance().collection("tokens")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(hashMap);
    }
}
