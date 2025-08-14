package com.takeiteasy.chatchat.listener.chatting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.Timestamp;
import com.takeiteasy.chatchat.ChatListActivity;
import com.takeiteasy.chatchat.ChatRoomActivity;
import com.takeiteasy.chatchat.FriendSearchActivity;
import com.takeiteasy.chatchat.MainActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityChatListBinding;
import com.takeiteasy.chatchat.databinding.ActivityFriendSearchBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;
import com.takeiteasy.chatchat.model.chatting.repository.ChattingRoomRepository;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.List;
import java.util.function.Consumer;

public class ChatListListener {
  private final ActivityChatListBinding binding;
  private final ChatRoomViewModel viewModel;
  private final Context context;

  public ChatListListener(ActivityChatListBinding binding, ChatRoomViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  public void initListeners(ChatRoomViewModel viewModel, LifecycleOwner lifecycleOwner, Consumer<String> consumer) {
    binding.chatBottomTabLayout.setSelectedItemId(R.id.navigation_chats);
    binding.chatBottomTabLayout.setOnItemSelectedListener(item -> {
      if (item.getItemId() == R.id.navigation_friends) {
        // '친구 목록' 탭 클릭 시 MainActivity로 이동
        context.startActivity(new Intent(context, MainActivity.class));
        // ⭐⭐ 이 부분이 중요합니다! finish()를 호출하여 현재 Activity(ChatListActivity)를 스택에서 제거합니다. ⭐⭐
        consumer.accept("finish");
        return true;
      }
      // TODO: 다른 탭 아이템에 대한 처리 (예: 설정 탭 등)
      return false;
    });

    binding.createChatRoomButton.setOnClickListener(item -> {
      consumer.accept("add");
    });
  }
}
