package com.takeiteasy.chatchat.model.auth.repository;

import android.os.Parcelable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.takeiteasy.chatchat.model.auth.LoginData;
import com.takeiteasy.chatchat.model.auth.LoginLoadListener;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginRepository {
    private FirebaseFirestore db;

    public LoginRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchLogin(LoginLoadListener listener) {
        db.collection("users")
            .get()
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    listener.onLoginLoaded(this.getUsers(task));
                } else {
                    listener.onLoginLoadFailed(task.getException());
                }
            });
    }

    private Stream<LoginData> getUsers(Task<QuerySnapshot> task) {
        return this.getStream(task.getResult().getDocuments().stream());
    }

    private Stream<LoginData> getStream(Stream<DocumentSnapshot> stream) {
        return stream.map(x -> x.toObject(LoginData.class));
    }
}
