package com.takeiteasy.chatchat.binder.chatting;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.takeiteasy.chatchat.databinding.ActivityChatListBinding;
import com.takeiteasy.chatchat.databinding.ActivityChatRoomBinding;
import com.takeiteasy.chatchat.model.chatting.ChattingMsg;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;
import com.takeiteasy.chatchat.model.chatting.adapter.ChattingMsgAdapterList;
import com.takeiteasy.chatchat.model.chatting.adapter.ChattingRoomAdapterList;
import com.takeiteasy.chatchat.viewmodel.ChatMsgViewModel;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomBinder {
  private final ActivityChatRoomBinding binding;
  private final ChatMsgViewModel viewModel;
  private final Context context;
  private ChattingMsgAdapterList chattingMsgAdapterList;
  private boolean isEnter;

  public ChatRoomBinder(ActivityChatRoomBinding binding, ChatMsgViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  public void bind(AppCompatActivity activity, LifecycleOwner lifecycleOwner, String currentUserId, String id, ChattingRoom data) {
    // ViewModel을 바인딩 객체에 연결
    binding.setViewModel(viewModel);
    // LifecycleOwner를 설정하여 LiveData가 뷰와 생명주기를 같이하도록 함
    binding.setLifecycleOwner(lifecycleOwner);
    // 2. LayoutManager 설정 (수직 스크롤 목록)
    RecyclerView msgRecyclerView = binding.recyclerViewChatMessages;
    msgRecyclerView.setLayoutManager(new LinearLayoutManager(context));

    if(data.getUsers() != null) {
      String title = data.getUsers()
        .stream()
        .map(ChattingUser::getNickName)
        .collect(Collectors.joining(","));
      binding.textViewChatRoomTitle.setText(title);
    }
    viewModel.getData().observe(lifecycleOwner, group -> {
      // 또는 기존 어댑터의 데이터를 업데이트:
      List<ChattingMsg> list = new ArrayList<>();
      list.addAll(group.getChattings());
      if (chattingMsgAdapterList != null) {
        chattingMsgAdapterList.setDataList(list);
      } else {
        chattingMsgAdapterList = new ChattingMsgAdapterList(
          activity,
          context,
          list,
          currentUserId);
      }

      msgRecyclerView.setAdapter(chattingMsgAdapterList);

      if(!isEnter)
        enteredChattingRoom(currentUserId);
    });

    viewModel.fetchChattingMsg(id);
  }

  public void enteredChattingRoom(String currentUserId) {
    viewModel.enteredChattingRoom(currentUserId, reponseStatus -> {
      isEnter = true;
    });
  }
}
