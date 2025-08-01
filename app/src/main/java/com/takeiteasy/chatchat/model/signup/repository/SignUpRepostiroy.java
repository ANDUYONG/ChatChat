package com.takeiteasy.chatchat.model.signup.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.model.signup.SignUpStatus;
import com.takeiteasy.chatchat.model.signup.SignUpCompleteListner;

public class SignUpRepostiroy {
    private FirebaseFirestore db;

    public SignUpRepostiroy() {
        db = FirebaseFirestore.getInstance();
    }

    public void signUp(SignUpData signUpData, SignUpCompleteListner listener) {
        checkEmailExist(signUpData.getEmail(), exist -> {
            if(exist) {
                listener.onComplete(SignUpStatus.EMAIL_ALREADY_EXISTS);
            } else {
                db.collection("users")
                        .add(signUpData)
                        .addOnCompleteListener(dc -> {
                            listener.onComplete(SignUpStatus.SUCCESS);
                        })
                        .addOnFailureListener(e -> {
                            listener.onComplete(SignUpStatus.FAILURE);
                        });
            }
        });
    }

    public void checkEmailExist(String email, ExistCallck callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        // document.isEmpty()가 true이면 일치하는 문서가 없다는 뜻 (이메일 없음)
                        // document.isEmpty()가 false이면 일치하는 문서가 있다는 뜻 (이메일 존재)
                        boolean exists = !document.isEmpty();
                        callback.onResult(exists); // 콜백을 통해 결과 전달
                    } else {
                        // 쿼리 실패 (네트워크 오류, 권한 문제 등)
                        // 오류 발생 시에도 false를 반환하거나, 오류 처리를 별도로 할 수 있습니다.
                        System.err.println("Error checking email existence: " + task.getException());
                        callback.onResult(false);
                    }
                });
    }

    // 이메일 존재 여부를 위한 내부 콜백
    private interface ExistCallck {
        void onResult(boolean exists);
    }
}
