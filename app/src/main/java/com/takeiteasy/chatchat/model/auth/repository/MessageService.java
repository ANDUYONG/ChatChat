package com.takeiteasy.chatchat.model.auth.repository;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MessageService {
  private FirebaseMessaging instance;
  private String loginUserId;

  public MessageService(String loginUserId) {
    this.instance = FirebaseMessaging.getInstance();
    this.loginUserId = loginUserId;
    this.getToken();
  }

  private void getToken() {
    // Firebase Messaging 토큰 가져오기 및 저장
    instance.getToken().addOnCompleteListener(task -> {
      if (!task.isSuccessful()) {
        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
        return;
      }
      String token = task.getResult();
      Log.d("FCM", "FCM Token: " + token);

      // Firestore의 'users' 컬렉션에 토큰 저장
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("users").document(loginUserId)
        .update("fcmTokens", FieldValue.arrayUnion(token))
        .addOnSuccessListener(aVoid -> Log.d("FCM", "Token successfully written!"))
        .addOnFailureListener(e -> Log.w("FCM", "Error writing token", e));
    });
  }
}
