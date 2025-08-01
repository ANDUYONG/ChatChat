package com.takeiteasy.chatchat.model.auth;

import android.os.Parcelable;

import java.util.List;
import java.util.stream.Stream;

public interface LoginLoadListener {
    void onLoginLoaded(Stream<LoginData> users);
    void onLoginLoadFailed(Exception e);
}
