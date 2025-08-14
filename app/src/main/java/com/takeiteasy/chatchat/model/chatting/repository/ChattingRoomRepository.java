package com.takeiteasy.chatchat.model.chatting.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.profile.ProfileData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ChattingRoomRepository {
  private FirebaseFirestore db;
  private List<ListenerRegistration> chattingRoomListeners = new ArrayList<>();

  public ChattingRoomRepository() {
    db = FirebaseFirestore.getInstance();
  }

  public void createChattingRoom(ChattingRoom data, Consumer<String> consumer) {
    db.collection("chattingRooms")
      .add(data)
      .addOnSuccessListener(dr -> {
        String uid = dr.getId();
        consumer.accept(uid);
      })
      .addOnFailureListener(e -> {
        FirebaseCrashlytics.getInstance().recordException(e);
      });
  }

  public void leaveChattingRoom(String userId, String uid, Action action) {
    db.collection("users")
      .document(userId)
      .update("chattingRooms", FieldValue.arrayRemove(uid))
      .addOnSuccessListener(dr -> {
        action.execute();
      })
      .addOnFailureListener(e -> {
        FirebaseCrashlytics.getInstance().recordException(e);
      });
  }

  // 이전 코드와 동일
  public void fetchChattingRooms(String userId, Consumer<List<ChattingRoom>> consumer) {
    // 'users' 컬렉션의 특정 문서(userId)에 대한 실시간 리스너를 추가합니다.
    // 이 리스너는 문서의 내용이 변경될 때마다 호출됩니다.
    db.collection("users").document(userId)
      .addSnapshotListener((documentSnapshot, e) -> {
        if (e != null) {
          // 오류가 발생하면 로그를 기록하고 Crashlytics에 전송합니다.
          Log.w(TAG, "리스너 오류: ", e);
          FirebaseCrashlytics.getInstance().recordException(e);
          return;
        }

        if (documentSnapshot != null && documentSnapshot.exists()) {
          ProfileData me = documentSnapshot.toObject(ProfileData.class);
          // ProfileData에서 채팅방 ID 리스트를 가져옵니다.
          List<String> list = Objects.requireNonNull(me).getChattingRooms();

          // 기존 리스너들을 모두 제거합니다. (중복 방지)
          removeChattingRoomListeners();

          // 채팅방 리스트가 비어있지 않은 경우에만 배치 쿼리 실행
          if (list != null && !list.isEmpty()) {
            this.batchChattingRooms(list, consumer);
          } else {
            List<ChattingRoom> result = new ArrayList<>();
            consumer.accept(result);
          }
        }
      });
  }

  private void batchChattingRooms(List<String> ids, Consumer<List<ChattingRoom>> consumer) {
    final int BATCH_SIZE = 10;
    // 여러 배치 쿼리 결과를 저장할 Map. key: 리스너 인덱스, value: 해당 배치 쿼리 결과
    final Map<Integer, List<ChattingRoom>> allDataMap = Collections.synchronizedMap(new HashMap<>());

    // 채팅방 ID 리스트를 10개씩 묶어 배치 쿼리를 생성
    for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
      final int batchIndex = i / BATCH_SIZE;
      int endIndex = Math.min(i + BATCH_SIZE, ids.size());
      List<String> currentBatchIds = ids.subList(i, endIndex);

      // 'whereIn' 쿼리로 실시간 리스너를 추가합니다.
      ListenerRegistration listener = db.collection("chattingRooms")
        .whereIn(FieldPath.documentId(), currentBatchIds)
        .addSnapshotListener((querySnapshot, e) -> {
          if (e != null) {
            Log.e(TAG, "채팅방 배치 쿼리 리스너 오류: " + e.getMessage(), e);
            FirebaseCrashlytics.getInstance().recordException(e);
            return;
          }

          if (querySnapshot != null) {
            // 현재 배치 쿼리의 결과를 리스트로 변환
            List<ChattingRoom> currentBatchData = new ArrayList<>();
            for (var doc : querySnapshot.getDocuments()) {
              ChattingRoom room = doc.toObject(ChattingRoom.class);
              room.setUid(doc.getId());
              currentBatchData.add(room);
            }

            // Map에 결과 업데이트
            allDataMap.put(batchIndex, currentBatchData);

            // 모든 배치 쿼리의 결과가 도착했는지 확인
            if (allDataMap.size() == (int) Math.ceil((double) ids.size() / BATCH_SIZE)) {
              // 모든 결과를 하나의 리스트로 합치기
              List<ChattingRoom> combinedList = new ArrayList<>();
              for (List<ChattingRoom> batch : allDataMap.values()) {
                combinedList.addAll(batch);
              }
              consumer.accept(combinedList);
            }
          }
        });
      chattingRoomListeners.add(listener);
    }
  }

  // 액티비티 또는 프래그먼트가 종료될 때 리스너를 제거하는 메서드
  public void removeChattingRoomListeners() {
    for (ListenerRegistration registration : chattingRoomListeners) {
      registration.remove();
    }
    chattingRoomListeners.clear();
  }
}
