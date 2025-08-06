package com.takeiteasy.chatchat.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.takeiteasy.chatchat.ChatListActivity;
import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.SignUpActivity;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class MainListener {
    private final ActivityMainBinding binding;
    private final MainViewModel viewModel;
    private final Context context;

    public MainListener(ActivityMainBinding binding, MainViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListeners() {
        // 1. 검색바 터치
        binding.searchEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (binding.searchEditText.getRight() - binding.searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                    // 돋보기 버튼 클릭 시 검색 수행
                    viewModel.filterProfiles(binding.searchEditText.getText().toString());
//                    performSearch(searchEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.filterProfiles(binding.searchEditText.getText().toString());
                return true;
            }
            return false;
        });

        // 친구 추가 화면 이동
        binding.addButton.setOnClickListener(v -> {
            // 친구 추가 화면으로 이동하는 Intent
            Intent friendAddIntent = new Intent(context, FriendAddActivity.class); // AddFriendActivity는 실제 파일명으로 변경해야 합니다.
//            friendAddIntent.putExtra("email", loginEmail);
            context.startActivity(friendAddIntent);
            Toast.makeText(context, "친구 추가 화면으로 이동", Toast.LENGTH_SHORT).show();
        });

        // 채팅목록 화면 이동
        binding.bottomTabLayout.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_chats) { // @menu/bottom_navigation_menu에 정의된 채팅 목록 탭 ID
                // '채팅 목록' 탭 클릭 시 ChatListActivity로 이동
                Intent chatListIntent = new Intent(context, ChatListActivity.class);
                context.startActivity(chatListIntent);
                // MainActivity를 종료하여 뒤로가기 버튼 시 채팅 목록 -> 홈 화면으로 가게 할 수 있습니다. (선택 사항)
//                finish();
                return true; // 이벤트를 소비했음을 알림
            }
            // TODO: 다른 탭 아이템에 대한 처리 (예: 설정 탭 등)
            return false; // 이벤트를 소비하지 않았음을 알림
        });
    }
}
