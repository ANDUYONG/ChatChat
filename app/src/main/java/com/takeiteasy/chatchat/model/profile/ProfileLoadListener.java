package com.takeiteasy.chatchat.model.profile;

import android.os.Parcelable;

import java.util.List;

public interface ProfileLoadListener {
    void onProfilesLoaded(ProfileData profile);
    void onProfilesLoadFailed(Exception e);
}
