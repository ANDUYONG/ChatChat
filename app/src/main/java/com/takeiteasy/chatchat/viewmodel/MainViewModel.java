package com.takeiteasy.chatchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.takeiteasy.chatchat.model.ReponseStatus;
import com.takeiteasy.chatchat.model.profile.FriendData;
import com.takeiteasy.chatchat.model.profile.FriendLoadedListener;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;
import com.takeiteasy.chatchat.model.profile.ProfileSetListener;
import com.takeiteasy.chatchat.model.profile.repository.ProfileRepository;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<ProfileData>> profiles;
    private MutableLiveData<ProfileData> profile;
    private MutableLiveData<ReponseStatus> status;
    private List<ProfileData> originalProfiles; // 필터링을 위한 원본 데이터
    private ProfileRepository repository;

    public MainViewModel() {
        profiles = new MutableLiveData<>();
        profile = new MutableLiveData<>();
        originalProfiles = new ArrayList<>();
        repository = new ProfileRepository();
        this.status = new MutableLiveData<>();
    }

    public LiveData<List<ProfileData>> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileData> dataList) {
      profiles.postValue(dataList);
    }

    public LiveData<ProfileData> getProfile() {
        return profile;
    }

    public void setProfile(ProfileData setData) {
        profile.postValue(setData);
    }

    public LiveData<ReponseStatus> getStatus() {
        return status;
    }

    public void loadProfile(String userId) {
        // ⭐ ProfileRepository.ProfileLoadListener 사용 및 시그니처 일치 ⭐
        repository.fetchUser(userId, new ProfileLoadListener() {
            @Override
            public void onProfilesLoaded(ProfileData response) {
                try {
                    profile.postValue(response); // 검색 결과만 표시
                } catch (Exception e) {
                    System.out.println("Exception -> " + e);
                }
            }

            @Override
            public void onProfilesLoadFailed(Exception e) {
                // 로드 실패 시
                profile.postValue(null); // 빈 목록으로 설정
                status.postValue(ReponseStatus.FAILURE); // 상태 업데이트
            }
        });
    }

    public void loadProfiles(String userId) {
        // ⭐ ProfileRepository.ProfileLoadListener 사용 및 시그니처 일치 ⭐
        repository.fetchUsers(userId, new FriendLoadedListener() {
            @Override
            public void onBatchProfilesLoaded(List<ProfileData> friendProfiles) {
                if(friendProfiles != null) {
                    profiles.postValue(friendProfiles); // 검색 결과만 표시
                } else {
                    profiles.postValue(new ArrayList<>()); // ViewModel의 LiveData 업데이트 (결과 없음)
                }
            }

            @Override
            public void onBatchProfilesLoadFailed(Exception e) {
                // 로드 실패 시
                profiles.postValue(new ArrayList<>()); // 빈 목록으로 설정
                status.postValue(ReponseStatus.FAILURE); // 상태 업데이트
            }
        });
    }

  public void loadProfilesForAddChattings(String userId) {
    // ⭐ ProfileRepository.ProfileLoadListener 사용 및 시그니처 일치 ⭐
    repository.fetchUsers(userId, new FriendLoadedListener() {
      @Override
      public void onBatchProfilesLoaded(List<ProfileData> friendProfiles) {
        profile.postValue(friendProfiles.remove(0));
        profiles.postValue(friendProfiles); // 검색 결과만 표시
//        List<ProfileData> original = (List<ProfileData>) SerializationUtils.clone((Serializable) friendProfiles);
        originalProfiles.addAll(friendProfiles);
      }

      @Override
      public void onBatchProfilesLoadFailed(Exception e) {
        // 로드 실패 시
        profiles.postValue(new ArrayList<>()); // 빈 목록으로 설정
        status.postValue(ReponseStatus.FAILURE); // 상태 업데이트
      }
    });
  }

    public void setProfile(String userId, Map<String, Object> updates) {
        // ⭐ ProfileRepository.ProfileLoadListener 사용 및 시그니처 일치 ⭐
        repository.updateProfile(userId, updates, new ProfileSetListener() {
            @Override
            public void onComplete(ReponseStatus reponse) {
                status.postValue(reponse);

                if(ReponseStatus.SUCCESS == reponse) {
                    loadProfile(userId);
                }
            }

            @Override
            public void onFailed(Exception e) {
                status.postValue(ReponseStatus.FAILURE);
            }
        });
    }

    public void searchProfiles(String email) {
        // ⭐ ProfileRepository.ProfileLoadListener 사용 및 시그니처 일치 ⭐
        repository.fetchProfiles(email, new ProfileLoadListener() {
            @Override
            public void onProfilesLoaded(ProfileData profileData) {
                List<ProfileData> profile = new ArrayList<>();
                profile.add(profileData);
                profiles.postValue(profile);
            }

            @Override
            public void onProfilesLoadFailed(Exception e) {
                List<ProfileData> profile = new ArrayList<>();
                profiles.postValue(profile);
            }
        });
    }

    public void filterProfiles(String value) {
        if (originalProfiles == null || originalProfiles.isEmpty()) {
            profiles.postValue(new ArrayList<>());
            return;
        }

        if (value.trim().isEmpty()) {
            profiles.postValue(new ArrayList<>(originalProfiles));
        } else {
            List<ProfileData> results = originalProfiles
                    .stream()
                    .filter(x -> x.getNickName().toLowerCase().contains(value.toLowerCase())) // 대소문자 구분 없이 검색
                    .collect(Collectors.toList());
            profiles.postValue(results);
        }
    }

    public void addFriend(String userId, FriendData friendData) { // 인자 변경
        repository.addFriends(userId, friendData, new ProfileSetListener() {
            @Override
            public void onComplete(ReponseStatus reponse) {
                status.setValue(reponse);
                // 친구 추가 성공 후, 친구 목록을 새로고침할 수 있습니다.
                // loadProfiles(myEmail); // 필요하다면 여기서 호출
            }

            @Override
            public void onFailed(Exception e) {
                status.setValue(ReponseStatus.FAILURE);
            }
        });
    }
}
