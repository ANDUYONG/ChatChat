package com.takeiteasy.chatchat.model.chatting.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.takeiteasy.chatchat.model.ReponseStatus;
import com.takeiteasy.chatchat.model.chatting.ChattingGroup;
import com.takeiteasy.chatchat.model.chatting.ChattingMsg;
import com.takeiteasy.chatchat.model.chatting.ChattingMsgSetListener;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChattingMsgRepository {
  private FirebaseFirestore db;

  public ChattingMsgRepository() {
    db = FirebaseFirestore.getInstance();
  }

  public void fetchChattingMsg(String id, Consumer<ChattingGroup> consumer) {
    // 'users' 컬렉션의 특정 문서(userId)에 대한 실시간 리스너를 추가합니다.
    // 이 리스너는 문서의 내용이 변경될 때마다 호출됩니다.
    db.collection("chattings").document(id)
      .addSnapshotListener((documentSnapshot, e) -> {
        if (e != null) {
          // 오류가 발생하면 로그를 기록하고 Crashlytics에 전송합니다.
          Log.w(TAG, "리스너 오류: ", e);
          FirebaseCrashlytics.getInstance().recordException(e);
          return;
        }

        try {
          if (documentSnapshot != null && documentSnapshot.exists()) {
            ChattingGroup data = documentSnapshot.toObject(ChattingGroup.class);
            data.setUid(documentSnapshot.getId());
            consumer.accept(data);
          }
        } catch (Exception ex) {
          Log.w(TAG, "오류: ", ex);
          FirebaseCrashlytics.getInstance().recordException(e);
        }
      });
  }

  public void sendMsg(String id, ChattingMsg msg, Consumer<ReponseStatus> listener) {
    db.collection("chattings")
      .document(id)
      .update("chattings", FieldValue.arrayUnion(msg))
      .addOnCompleteListener(task -> {
        if(task.isSuccessful()) {
          listener.accept(ReponseStatus.SUCCESS);
        } else {
          Log.w(TAG, "리스너 오류: ", task.getException());
          FirebaseCrashlytics.getInstance().recordException(task.getException());
        }
      })
      .addOnFailureListener(FirebaseCrashlytics.getInstance()::recordException);
  }

  public void updateStatusInChattingRoom(String id, List<String> list, Consumer<ReponseStatus> listener) {
    db.collection("chattings")
      .document(id)
      .update("onUsers", list)
      .addOnCompleteListener(task -> {
        if(task.isSuccessful()) {
          listener.accept(ReponseStatus.SUCCESS);
        } else {
          Log.w(TAG, "리스너 오류: ", task.getException());
          FirebaseCrashlytics.getInstance().recordException(task.getException());
        }
      })
      .addOnFailureListener(e -> {
        Log.w(TAG, "Repository 오류: ", e);
        FirebaseCrashlytics.getInstance().recordException(e);
      });
  }
}
