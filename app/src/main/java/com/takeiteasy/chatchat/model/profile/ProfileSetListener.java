package com.takeiteasy.chatchat.model.profile;

import com.takeiteasy.chatchat.model.ReponseStatus;
import com.takeiteasy.chatchat.model.signup.SignUpStatus;

public interface ProfileSetListener {
    void onComplete(ReponseStatus status);
    void onFailed(Exception e);
}
