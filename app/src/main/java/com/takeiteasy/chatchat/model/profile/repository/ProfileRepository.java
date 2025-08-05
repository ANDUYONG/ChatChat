package com.takeiteasy.chatchat.model.profile.repository;

import static android.content.ContentValues.TAG;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.takeiteasy.chatchat.model.ReponseStatus;
import com.takeiteasy.chatchat.model.profile.FriendData;
import com.takeiteasy.chatchat.model.profile.FriendLoadedListener;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;
import com.takeiteasy.chatchat.model.profile.ProfileSetListener;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileRepository {
    private FirebaseFirestore db;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // FriendProfilesLoadListener 인터페이스는 친구 프로필 목록을 전달하므로 ProfileLoadListener와 유사하게 변경했습니다.
    // 기존 FriendLoadedListener가 friendProfiles만 받는다면, 이대로 사용해도 됩니다.
    // 만약 본인 프로필도 함께 전달해야 한다면, ProfileLoadListener를 확장하거나 새로운 인터페이스를 정의합니다.
    public interface FriendProfilesLoadListener {
        void onProfilesLoaded(List<ProfileData> friendProfiles);
        void onProfilesLoadFailed(Exception e);
    }

    public void fetchUser(String userId,  ProfileLoadListener listener) { // 데이터를 직접 리턴하지 않고 리스너를 통해 전달
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ProfileData prfile = this.getProfile(task);
                        if(prfile == null) return;
                        listener.onProfilesLoaded(prfile);
                    }
                }).addOnFailureListener(e -> {
                    FirebaseCrashlytics.getInstance().recordException(e);
                });
    }

    public void fetchUsers(String userId,  FriendLoadedListener listener) { // 데이터를 직접 리턴하지 않고 리스너를 통해 전달
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ProfileData me = this.getProfile(task);
//                        List<String> ids = this.getFriends(task).stream().map(x -> x.getEmail()).collect(Collectors.toList());
                        if(me == null) return;

                        List<String> ids = null;
                        if(me.getFriends() != null && me.getFriends().size() > 0) {
                            ids = me.getFriends().stream().map(x -> x.getUserId()).collect(Collectors.toList());
                        }

                        List<ProfileData> results = new ArrayList<>();
                        results.add(me);
                        if(ids == null || ids.size() == 0) {
                            listener.onBatchProfilesLoaded(results);
                        } else {
                            this.batchProfiles(ids, new FriendProfilesLoadListener() {
                                @Override
                                public void onProfilesLoaded(List<ProfileData> friendProfiles) {
                                    results.addAll(friendProfiles);
                                    listener.onBatchProfilesLoaded(results);
                                }

                                @Override
                                public void onProfilesLoadFailed(Exception e) {
                                    listener.onBatchProfilesLoadFailed(e);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    FirebaseCrashlytics.getInstance().recordException(e);
                });

    }

    private void batchProfiles(List<String> ids, FriendProfilesLoadListener listener) {
        final int BATCH_SIZE = 10;
        List<Task<QuerySnapshot>> batchTasks = new ArrayList<>(); // 모든 배치 쿼리 Task를 저장할 리스트
        final List<ProfileData> allFriendProfiles = Collections.synchronizedList(new ArrayList<>()); // 모든 친구 프로필을 모을 스레드 안전한 리스트

        // 친구 ID 리스트를 BATCH_SIZE (10개) 단위로 분할하여 쿼리 Task 생성
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, ids.size());
            List<String> currentBatchIds = ids.subList(i, endIndex);

            // Firestore 쿼리 생성 (문서 ID를 기준으로 whereIn 쿼리)
            // 'users' 컬렉션의 문서 ID가 ProfileData의 userId와 동일하다고 가정
            Task<QuerySnapshot> batchTask = db.collection("profiles")
                    .whereIn("userId", currentBatchIds)
                    .get();
            batchTasks.add(batchTask);
        }

        // 모든 배치 쿼리가 성공적으로 완료될 때까지 기다립니다.
        Tasks.whenAllSuccess(batchTasks)
                .addOnSuccessListener(results -> {
                    // 모든 쿼리 결과들을 순회하며 ProfileData 객체로 변환하여 취합
                    for (Object result : results) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) result;
                        Stream<ProfileData> streams = querySnapshot.getDocuments().stream().map(x -> x.toObject(ProfileData.class));
                        allFriendProfiles.addAll(streams.collect(Collectors.toList()));
                    }
                    listener.onProfilesLoaded(allFriendProfiles); // 최종 결과 전달
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "친구 프로필 배치 쿼리 중 하나 이상 실패: " + e.getMessage(), e);
                    // Crashlytics에 예외 기록
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listener.onProfilesLoadFailed(e); // 오류 발생 시 콜백 호출
                });
    }

    public void fetchProfiles(String email, ProfileLoadListener listener) { // 데이터를 직접 리턴하지 않고 리스너를 통해 전달
        db.collection("profiles")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        listener.onProfilesLoaded(this.searchProfile(task));
                    } else {
                        listener.onProfilesLoadFailed(task.getException());
                    }
                }).addOnFailureListener(e -> {
                    FirebaseCrashlytics.getInstance().recordException(e);
                });
    }

    public void addFriends(String userId, FriendData friend, ProfileSetListener listener) { // 데이터를 직접 리턴하지 않고 리스너를 통해 전달
        db.collection("users")
                .document(userId)
                .update("friends", FieldValue.arrayUnion(friend))
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        listener.onComplete(ReponseStatus.SUCCESS);
                    } else {
                        listener.onFailed(task.getException());
                    }
                }).addOnFailureListener(e -> {
                    FirebaseCrashlytics.getInstance().recordException(e);
                });;
    }

    public void updateProfile(String userId, Map<String, Object> updates, ProfileSetListener listener) {
        // Firestore 인스턴스 가져오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // "users" 컬렉션에서 특정 userId를 가진 문서 참조
        DocumentReference userRef = db.collection("users").document(userId);

        // update() 메서드를 사용하여 필드 덮어쓰기
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
//                    frameLayoutOverlay.setVisibility(View.GONE); // 오버레이 숨김
//                    currentEditingField = EDIT_NONE; // 편집 모드 초기화
//                    editTextOverlayInput.setText(""); // EditText 내용 초기화

                    // TODO: 여기서 변경된 프로필 데이터를 서버에 저장하거나 로컬 데이터베이스에 업데이트하는 로직을 추가해야 합니다.
                    // 예: updateProfileOnServer(newText, currentEditingField);

//                    Toast.makeText(this, "변경사항이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                    listener.onComplete(ReponseStatus.SUCCESS);
                })
                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "정보 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    listener.onFailed(e);
                });
    }

    private ProfileData getProfile(Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) { // 문서가 존재하는지 확인
                // Stream.class 대신 ProfileData.class를 사용해야 할 수도 있습니다. 아래 2번 문제 참조.
                ProfileData profileData = document.toObject(ProfileData.class);
                if (profileData != null) {
                    return profileData;
                } else {
                    // toObject()가 null을 반환한 경우 (예: 데이터 형식이 맞지 않을 때)
                    System.err.println("Error: Stream object is null after toObject()");
                    return null; // 또는 적절한 기본값/오류 처리
                }
            } else {
                // 문서가 존재하지 않는 경우
                System.out.println("Document does not exist.");
                return null; // 또는 적절한 기본값/오류 처리
            }
        } else {
            // Task가 실패한 경우
            System.err.println("Error getting document: " + task.getException());
            return null; // 또는 적절한 기본값/오류 처리
        }
    }

    private ProfileData searchProfile(Task<QuerySnapshot> task) {
        return this.getStream(task.getResult().getDocuments().stream());
    }

    private List<ProfileData> getProfiles(Task<QuerySnapshot> task) {
        return this.getStreamFriendProfilesData(task.getResult().getDocuments().stream());
    }

    private ProfileData getStream(Stream<DocumentSnapshot> stream) {
        return stream.map(x -> x.toObject(ProfileData.class)).findAny().orElse(null);
    }

//    private List<FriendData> getFriends(Task<DocumentSnapshot> task) {
//        Stream<ProfileData> result = this.getStreamFriendData(task.getResult().toObject(ProfileData.class));
//        return result.filter(x -> x != null)
//                .flatMap(x -> x.getFriends().stream())
//                .collect(Collectors.toList());
//    }

    private Stream<ProfileData> getStreamFriendData(Stream<DocumentSnapshot> stream) {
        return stream.map(x -> x.toObject(ProfileData.class));
    }

    private List<ProfileData> getStreamFriendProfilesData(Stream<DocumentSnapshot> stream) {
        return stream.map(x -> x.toObject(ProfileData.class)).collect(Collectors.toList());
    }

    private void success() {

    }
}
