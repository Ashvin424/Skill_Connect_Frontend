package com.ashvinprajapati.skillconnect;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ashvinprajapati.skillconnect.models.FcmTokenRequest;
import com.ashvinprajapati.skillconnect.networks.ApiClient;
import com.ashvinprajapati.skillconnect.networks.UserApiService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "SkillConnect";
        String message = "You got a notification";

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                message = remoteMessage.getNotification().getBody();
            }
        }

        // ✅ Then check data payload (usually from backend)
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get("title") != null) {
                title = remoteMessage.getData().get("title");
            }
            if (remoteMessage.getData().get("message") != null) {
                message = remoteMessage.getData().get("message");
            }
        }

        Log.d("FCM", "Notification received - Title: " + title + ", Message: " + message);

        showNotification(title, message);
    }



//    private void showNotification(String title, String message) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM_CHANNEL")
//                .setSmallIcon(R.drawable.icon_notification) // your icon
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        manager.notify(100, builder.build());
//    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed token: " + token);

        // Send to backend
        sendTokenToBackend(token);
    }

    private void sendTokenToBackend(String token) {
        // Get stored user email (you must have saved it after login)
        SharedPreferences prefs = getSharedPreferences("SkillConnectPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null); // or use your own key

        if (email == null) {
            Log.w("FCM", "User email not found. Cannot update token.");
            return;
        }
        UserApiService apiService = ApiClient.getClient(this).create(UserApiService.class);

        apiService.updateFcmToken(email, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Token updated on server");
                } else {
                    Log.e("FCM", "Token update failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Token update error: " + t.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "fcm_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for FCM notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_notification) // make sure you have this icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
