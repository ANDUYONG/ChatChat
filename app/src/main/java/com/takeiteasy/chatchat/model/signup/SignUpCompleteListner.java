package com.takeiteasy.chatchat.model.signup;

public interface SignUpCompleteListner {
    void onComplete(SignUpStatus status);
    void onFailed(Exception e);
}
