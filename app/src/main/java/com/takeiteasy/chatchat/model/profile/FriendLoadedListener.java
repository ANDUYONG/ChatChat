package com.takeiteasy.chatchat.model.profile;

import java.util.List;

public interface FriendLoadedListener {
    void onBatchProfilesLoaded(List<ProfileData> friendProfiles);
    void onBatchProfilesLoadFailed(Exception e);
}
