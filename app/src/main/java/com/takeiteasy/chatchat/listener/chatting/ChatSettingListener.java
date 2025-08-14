package com.takeiteasy.chatchat.listener.chatting;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.takeiteasy.chatchat.ChatSettingsActivity;
import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityChatSettingsBinding;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

public class ChatSettingListener {
  private final ActivityChatSettingsBinding binding;
  private final ChatRoomViewModel viewModel;
  private final Context context;

  public ChatSettingListener(ActivityChatSettingsBinding binding, ChatRoomViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  public void initListeners(AppCompatActivity activity, LifecycleOwner lifecycleOwner) {
    Intent intent = activity.getIntent();
    String currentUserId = intent.getStringExtra("userId");
    ChattingRoom chattingRoom = (ChattingRoom) intent.getParcelableExtra("chattingRoom");

    binding.imageViewBack.setOnClickListener(v -> activity.finish());

    // 친구 초대 화면 이동
    binding.textViewInviteFriend.setOnClickListener(v -> {
      Intent friendAddIntent = new Intent(activity, FriendAddActivity.class);
      activity.startActivity(friendAddIntent);
    });

    // '채팅방 나가기' 버튼 클릭 리스너 설정
    binding.buttonLeaveChatRoom.setOnClickListener(v -> {
      // ChatRoomActivity로 전달할 Intent를 생성합니다.
      // 이 Intent에 "action"이라는 키로 "leave_chat"이라는 값을 넣어,
      // ChatRoomActivity가 이 버튼이 눌렸음을 알 수 있도록 합니다.
      viewModel.leaveChattingRooms(currentUserId, chattingRoom.getUid(), () -> {
        Toast.makeText(activity, "방을 나갑니다.", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("action", "leave_chat");

        // 결과 코드를 RESULT_OK로 설정하고, 위에서 생성한 Intent를 함께 전달합니다.
        activity.setResult(RESULT_OK, resultIntent);

        // 현재 ChatSettingsActivity를 종료합니다.
        activity.finish();

      });
    });
  }
}
