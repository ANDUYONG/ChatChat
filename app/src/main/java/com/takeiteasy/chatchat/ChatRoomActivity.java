package com.takeiteasy.chatchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.takeiteasy.chatchat.binder.chatting.ChatListBinder;
import com.takeiteasy.chatchat.binder.chatting.ChatRoomBinder;
import com.takeiteasy.chatchat.databinding.ActivityChatListBinding;
import com.takeiteasy.chatchat.databinding.ActivityChatRoomBinding;
import com.takeiteasy.chatchat.listener.chatting.ChatListListener;
import com.takeiteasy.chatchat.listener.chatting.ChatRoomListener;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.viewmodel.ChatMsgViewModel;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

public class ChatRoomActivity extends AppCompatActivity {
  private ActivityChatRoomBinding binding;
  private ChatMsgViewModel viewModel;
  private ChatRoomBinder binder;
  private ChatRoomListener listener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());
      viewModel = new ViewModelProvider(this).get(ChatMsgViewModel.class);
      binder = new ChatRoomBinder(binding, viewModel);
      listener = new ChatRoomListener(binding, viewModel, this);

      Intent intent = getIntent();
      String userId = intent.getStringExtra("userId");
      String id = intent.getStringExtra("id");
      ChattingRoom data = (ChattingRoom) intent.getParcelableExtra("chattingRoom");

      binder.bind(this, this, userId, id, data);
      listener.initListeners(this, userId, data);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if(viewModel.getData().getValue() != null)
      listener.leaveChattingRoom();
  }
}
