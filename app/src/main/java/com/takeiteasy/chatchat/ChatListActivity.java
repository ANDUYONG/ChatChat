package com.takeiteasy.chatchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.takeiteasy.chatchat.binder.FriendSearchBinder;
import com.takeiteasy.chatchat.binder.chatting.ChatListBinder;
import com.takeiteasy.chatchat.databinding.ActivityChatListBinding;
import com.takeiteasy.chatchat.databinding.ActivityFriendSearchBinding;
import com.takeiteasy.chatchat.listener.FriendSearchListener;
import com.takeiteasy.chatchat.listener.chatting.ChatListListener;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class ChatListActivity extends AppCompatActivity {

  private ActivityChatListBinding binding;
  private ChatRoomViewModel viewModel;
  private ChatListBinder binder;
  private ChatListListener listener;

  // ⭐ 1. FriendSearchActivity를 위한 런처
  private final ActivityResultLauncher<Intent> friendSearchLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
      if (result.getResultCode() == Activity.RESULT_OK) {
        Intent data = result.getData();
        if (data != null) {
          String receivedUid = data.getStringExtra("uid");
          if (receivedUid != null) {
            // 결과로 받은 uid를 사용하여 startChatRoomActivity 호출
            startChatRoomActivity(receivedUid);
            Toast.makeText(this, "Received UID: " + receivedUid, Toast.LENGTH_SHORT).show();
          }
        }
      }
    });

  // ⭐ 2. ChatRoomActivity를 위한 런처 (새로 추가)
  private final ActivityResultLauncher<Intent> chatRoomLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
      // ChatRoomActivity에서 돌아왔을 때 처리할 로직
      // 예시: Toast.makeText(this, "채팅방에서 돌아왔습니다.", Toast.LENGTH_SHORT).show();
    });


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityChatListBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    viewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
    binder = new ChatListBinder(binding, viewModel);
    listener = new ChatListListener(binding, viewModel);

    Intent intent = getIntent();
    String userId = intent.getStringExtra("userId");

    binder.bind(this, userId);
    listener.initListeners(viewModel, this, type -> {
      switch (type) {
        case "finish":
          finish();
          break;
        case "add":
          // startFriendSearchActivity() 호출
          startFriendSearchActivity();
          break;
      }
    });
  }

  // FriendSearchActivity를 시작하는 코드
  private void startFriendSearchActivity() {
    Intent intent = getIntent();
    String userId = intent.getStringExtra("userId");
    Intent searchIntent = new Intent(this, FriendSearchActivity.class);
    searchIntent.putExtra("userId", userId);
    friendSearchLauncher.launch(searchIntent);
  }

  // ChatRoomActivity를 시작하는 코드
  private void startChatRoomActivity(String uid) {
    Intent intent = new Intent(this, ChatRoomActivity.class);
    intent.putExtra("uid", uid);
    // ⭐ 수정된 부분: chatRoomLauncher를 사용해서 Activity 시작
    chatRoomLauncher.launch(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    Intent intent = getIntent();
    String userId = intent.getStringExtra("userId");
    viewModel.fetchChattingRooms(userId);
  }
}
