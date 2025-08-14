package com.takeiteasy.chatchat.binder.chatting;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.takeiteasy.chatchat.databinding.ActivityChatRoomBinding;
import com.takeiteasy.chatchat.databinding.ActivityChatSettingsBinding;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.adapter.ChattingMsgAdapterList;
import com.takeiteasy.chatchat.viewmodel.ChatMsgViewModel;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

public class ChatSettingBinder {
  private final ActivityChatSettingsBinding binding;
  private final ChatRoomViewModel viewModel;
  private final Context context;

  public ChatSettingBinder(ActivityChatSettingsBinding binding, ChatRoomViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  public void bind(AppCompatActivity activity, LifecycleOwner lifecycleOwner) {

  }
}
