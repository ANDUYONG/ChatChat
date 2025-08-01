package com.takeiteasy.chatchat.model.signup;

public interface SignUpCheckListener {
    void onSignUpChecked(boolean isExist);
    void onSignUpCheckedFailed(Exception e);
}
