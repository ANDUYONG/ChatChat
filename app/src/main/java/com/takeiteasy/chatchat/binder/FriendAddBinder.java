package com.takeiteasy.chatchat.binder;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.databinding.ActivityFriendAddBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class FriendAddBinder {
    private final ActivityFriendAddBinding binding;
    private final MainViewModel viewModel;
    private final Context context;
    private ProfileDataListAdapter profileListAdapter;

    public FriendAddBinder(ActivityFriendAddBinding binding, MainViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    public void bind(LifecycleOwner lifecycleOwner, Action action) {
// 2. LayoutManager 설정 (수직 스크롤 목록)
        binding.profileView.setLayoutManager(new LinearLayoutManager(context));

        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfiles().observe(lifecycleOwner, profileList -> {
            // 또는 기존 어댑터의 데이터를 업데이트:
            if (profileListAdapter != null) {
                profileListAdapter.setProfileDatas(profileList);
            } else {
                profileListAdapter = new ProfileDataListAdapter(context, profileList, (profile, position) -> {
                    if(viewModel.getProfile().getValue() != profile)
                        viewModel.setProfile(profile);
                });
                binding.profileView.setAdapter(profileListAdapter);
            }
        });

        viewModel.getStatus().observe(lifecycleOwner, reponseStatus -> {
            switch(reponseStatus) {
                case SUCCESS:
                    Toast.makeText(context, "친구 추가!", Toast.LENGTH_SHORT).show();
                    action.execute();
                    break;
                case FAILURE:
                    Toast.makeText(context, "친구 추가 실패!", Toast.LENGTH_SHORT).show();
                    action.execute();
                    break;
            }
        });
    }
}
