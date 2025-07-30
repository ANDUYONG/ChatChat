package com.takeiteasy.chatchat.model.profile;

import android.os.Parcelable;

import java.util.List;

public interface ProfileLoadListener {
    void onProfilesLoaded(List<Parcelable> profiles);
    void onProfilesLoadFailed(Exception e);
}
