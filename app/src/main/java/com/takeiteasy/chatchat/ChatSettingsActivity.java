package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.binder.chatting.ChatRoomBinder;
import com.takeiteasy.chatchat.binder.chatting.ChatSettingBinder;
import com.takeiteasy.chatchat.databinding.ActivityChatRoomBinding;
import com.takeiteasy.chatchat.databinding.ActivityChatSettingsBinding;
import com.takeiteasy.chatchat.listener.chatting.ChatRoomListener;
import com.takeiteasy.chatchat.listener.chatting.ChatSettingListener;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.viewmodel.ChatMsgViewModel;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

public class ChatSettingsActivity extends AppCompatActivity {
  private ActivityChatSettingsBinding binding;
  private ChatRoomViewModel viewModel;
  private ChatSettingBinder binder;
  private ChatSettingListener listener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      binding = ActivityChatSettingsBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());
      viewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
      binder = new ChatSettingBinder(binding, viewModel);
      listener = new ChatSettingListener(binding, viewModel);

      Intent intent = getIntent();
      String userId = intent.getStringExtra("userId");
      String id = intent.getStringExtra("id");
      ChattingRoom data = (ChattingRoom) intent.getParcelableExtra("chattingRoom");

      binder.bind(this, this);
      listener.initListeners(this, this);
  }
}
