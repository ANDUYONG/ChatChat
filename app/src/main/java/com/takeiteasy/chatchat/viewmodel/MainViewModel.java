package com.takeiteasy.chatchat.viewmodel;

import android.os.Parcelable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;
import com.takeiteasy.chatchat.model.profile.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<Parcelable>> profiles;
    private List<Parcelable> originalProfiles;
    private ProfileRepository repository;

    public MainViewModel() {
        profiles = new MutableLiveData<>();
        originalProfiles = new ArrayList<>();
        repository = new ProfileRepository();
    }

    public LiveData<List<Parcelable>> getProfiles() {
        return profiles;
    }

    public void loadProfiles() {
        repository.fetchProfiles(new ProfileLoadListener() {
            @Override
            public void onProfilesLoaded(List<Parcelable> loadedProfiles) {
                originalProfiles.addAll(loadedProfiles);
                profiles.setValue(loadedProfiles); // ViewModel의 LiveData 업데이트
            }

            @Override
            public void onProfilesLoadFailed(Exception e) {
                // 오류 처리 (예: Toast 메시지, 다른 LiveData로 오류 상태 전달)
                profiles.setValue(new ArrayList<>()); // 실패 시 빈 목록이라도 전달
            }
        });
    }

    /**
     * 프로필 검색
     * @param value
     */
    public void filterProfiles(String value) {
        if (value.trim().isEmpty()) {
            profiles.setValue(originalProfiles);
        } else {
            List<Parcelable> results = originalProfiles
                    .stream()
                    .map(x -> (ProfileData) x)
                    .filter(x -> x.getNickName().contains(value))
                    .collect(Collectors.toList());
            profiles.setValue(results);
        }
    }

}
