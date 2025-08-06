package com.takeiteasy.chatchat.listener.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.takeiteasy.chatchat.ChatRoomActivity;
import com.takeiteasy.chatchat.ProfileBackgroundActivity;
import com.takeiteasy.chatchat.ProfileDetailActivity;
import com.takeiteasy.chatchat.ProfileEditActivity;
import com.takeiteasy.chatchat.ProfileImageActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.databinding.ActivityProfileDetailBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;

public class ProfileDetailListener {
    private final ActivityProfileDetailBinding binding;
    private final MainViewModel viewModel;
    private final Context context;
    private ProfileData profile;

    public ProfileDetailListener(ActivityProfileDetailBinding binding, MainViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    public void initListeners(Activity activity) {
        // 닫기 > 친구목록으로 이동
        binding.profileViewClose.setOnClickListener(v -> activity.finish());

        // 1. 프로필 상세로 이동
        binding.profileView.setOnClickListener(v -> {
            profile = viewModel.getProfile().getValue();
            Intent profileViewIntent = new Intent(context, ProfileImageActivity.class);
            profileViewIntent.putExtra("profileUrl", profile.getProfileUrl());
            context.startActivity(profileViewIntent);
        });

        // 2. 프로필 배경 상세로 이동
        binding.profileViewBackground.setOnClickListener(v -> {
            profile = viewModel.getProfile().getValue();
            Intent profileViewBackgroundIntent = new Intent(context, ProfileBackgroundActivity.class);
            profileViewBackgroundIntent.putStringArrayListExtra("backgroundUrls", (ArrayList<String>) profile.getBackgroundUrls());
            context.startActivity(profileViewBackgroundIntent);
        });

        // layoutChat에 클릭 리스너 설정
        // 1:1 대화  하기 버튼 클릭시
        binding.layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1:1 채팅 버튼이 클릭되었을 때 실행할 코드
                // 예: 채팅 화면으로 이동
                Intent chatIntent = new Intent(context,  ChatRoomActivity.class);
                context.startActivity(chatIntent);
            }
        });

        // layoutEditProfile에 클릭 리스너 설정
        // 프로필 편집 클릭시
        binding.layoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileEditIntent = new Intent(context,  ProfileEditActivity.class);
                profileEditIntent.putExtra("profileData", profile); // ProfileData 객체를 Intent에 담아 전달
                context.startActivity(profileEditIntent);
            }
        });
    }
}
