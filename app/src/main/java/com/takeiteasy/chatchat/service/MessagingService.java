package com.takeiteasy.chatchat.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.takeiteasy.chatchat.R;

public class MessagingService extends FirebaseMessagingService {
  private static final String TAG = "FirebaseMessagingService";

  @Override
  public void onNewToken(@NonNull String token) {
    Log.d(TAG, "Refreshed token: " + token);

    this.sendRegistrationToServer(token);
  }

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    // 앱이 포그라운드에 있을 때 메시지를 수신했을 경우 호출됩니다.
    if (remoteMessage.getNotification() != null) {
      String title = remoteMessage.getNotification().getTitle();
      String body = remoteMessage.getNotification().getBody();
      sendNotification(title, body);
    }
  }

  private void sendNotification(String title, String body) {
    String channelId = "chat_notification_channel";
    NotificationManager notificationManager =
      getSystemService(NotificationManager.class);

    // 안드로이드 8.0 (Oreo) 이상에서는 알림 채널이 필요합니다.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        channelId,
        "채팅 알림",
        NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.mipmap.ic_launcher) // 앱 아이콘으로 설정
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true);

    notificationManager.notify(0, notificationBuilder.build());
  }

  // 이 메서드는 토큰을 서버(Firestore)에 저장하는 역할을 합니다.
  private void sendRegistrationToServer(String token) {
    // Firebase Authentication을 통해 현재 로그인된 사용자 정보를 가져옵니다.
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // 사용자가 로그인되어 있을 경우에만 토큰을 Firestore에 저장합니다.
    if (user != null) {
      String userId = user.getUid();
      FirebaseFirestore db = FirebaseFirestore.getInstance();

      db.collection("users").document(userId)
        .update("fcmTokens", FieldValue.arrayUnion(token))
        .addOnSuccessListener(aVoid -> Log.d(TAG, "Token successfully written!"))
        .addOnFailureListener(e -> Log.w(TAG, "Error writing token", e));
    } else {
      Log.w(TAG, "No user is signed in. Cannot save FCM token.");
    }
  }
}
