package com.takeiteasy.chatchat.binder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.takeiteasy.chatchat.databinding.ActivityFriendSearchBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.List;

public class FriendSearchBinder {
  private final ActivityFriendSearchBinding binding;
  private final Context context;
  private ProfileDataListAdapter profileListAdapter;

  public FriendSearchBinder(ActivityFriendSearchBinding binding) {
    this.binding = binding;
    this.context = binding.getRoot().getContext();
  }

  public void bind(MainViewModel viewModel, LifecycleOwner lifecycleOwner, String userId) {
    RecyclerView profileView = binding.profileView;
    SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
    //        String loginUserId = preferenceManager.getString("userId", null);
    //        String loginUserId = preferenceManager.getString("userId", null);

    // ViewModel을 바인딩 객체에 연결
    binding.setViewModel(viewModel);
    // LifecycleOwner를 설정하여 LiveData가 뷰와 생명주기를 같이하도록 함
    binding.setLifecycleOwner(lifecycleOwner);

    // 2. LayoutManager 설정 (수직 스크롤 목록)
    profileView.setLayoutManager(new LinearLayoutManager(context));

    // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
    viewModel.getProfiles().observe(lifecycleOwner, profileList -> {
      Toast.makeText(context, "친구 목록 조회!", Toast.LENGTH_SHORT).show();

      // 또는 기존 어댑터의 데이터를 업데이트:
      if (profileListAdapter != null) {
        profileListAdapter.setProfileDatas(profileList);
      } else {
        profileListAdapter = new ProfileDataListAdapter(context, profileList, this::selectProfile);
        profileView.setAdapter(profileListAdapter);
      }
    });

    viewModel.loadProfilesForAddChattings(userId);
  }

  /**
   * 리사이클러뷰 아이템 클릭시 콜백
   * 선택된 데이터를 추가한다.
   * @param profile
   * @param position
   */
  private void selectProfile(ProfileData profile, int position) {
    MainViewModel viewModel = binding.getViewModel();
    List<ProfileData> profileDatas = viewModel.getProfiles().getValue();
    return;
  }
}
