package com.takeiteasy.chatchat.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.takeiteasy.chatchat.ChatListActivity;
import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityFriendAddBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.profile.FriendData;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class FriendAddListener {
    private final ActivityFriendAddBinding binding;
    private final MainViewModel viewModel;
    private final Context context;

    public FriendAddListener(ActivityFriendAddBinding binding, MainViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListeners(Action action) {
        // 닫기 > 친구목록으로 이동
        binding.imageViewBack.setOnClickListener(v -> {
            action.execute();
        });

        // 친구 추가 버튼 클릭
        binding.buttonAddFriend.setOnClickListener(v -> {
            SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
            String loginUserId = preferenceManager.getString("userId", null);
            ProfileData selectedProfile = viewModel.getProfile().getValue();
            if(selectedProfile != null) {
                String userId = selectedProfile.getUserId();
                String email = selectedProfile.getEmail();
                viewModel.addFriend(loginUserId, new FriendData(userId, email, false, false));
            }
            else
                Toast.makeText(context, "선택된 프로필이 없습니다.", Toast.LENGTH_SHORT).show();
        });

        binding.findEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (binding.findEditText.getRight() - binding.findEditText.getCompoundDrawables()[2].getBounds().width())) {
                    viewModel.searchProfiles(binding.findEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        binding.findEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchProfiles(binding.findEditText.getText().toString());
                return true;
            }
            return false;
        });
    }
}
