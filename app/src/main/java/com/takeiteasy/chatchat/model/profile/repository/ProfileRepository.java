package com.takeiteasy.chatchat.model.profile.repository;

import android.os.Parcelable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfileRepository {
    private FirebaseFirestore db;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchProfiles(ProfileLoadListener listener) { // 데이터를 직접 리턴하지 않고 리스너를 통해 전달
        db.collection("profiles")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        listener.onProfilesLoaded(this.getProfiles(task));
                    } else {
                        listener.onProfilesLoadFailed(task.getException());
                    }
                });
    }

    private List<Parcelable> getProfiles(Task<QuerySnapshot> task) {
        return this.getStream(task.getResult().getDocuments().stream());
    }

    private List<Parcelable> getStream(Stream<DocumentSnapshot> stream) {
        return stream.map(x -> x.toObject(ProfileData.class)).collect(Collectors.toList());
    }

    private void success() {

    }
}
