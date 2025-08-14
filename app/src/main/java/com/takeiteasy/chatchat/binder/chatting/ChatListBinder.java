package com.takeiteasy.chatchat.binder.chatting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.takeiteasy.chatchat.databinding.ActivityChatListBinding;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.adapter.ChattingRoomAdapterList;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.ChatRoomViewModel;

public class ChatListBinder {
  private final ActivityChatListBinding binding;
  private final ChatRoomViewModel viewModel;
  private final Context context;
  private ChattingRoomAdapterList chattingRoomAdapterList;

  public ChatListBinder(ActivityChatListBinding binding, ChatRoomViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  public void bind(LifecycleOwner lifecycleOwner, String userId) {
    // ViewModel을 바인딩 객체에 연결
    binding.setViewModel(viewModel);
    // LifecycleOwner를 설정하여 LiveData가 뷰와 생명주기를 같이하도록 함
    binding.setLifecycleOwner(lifecycleOwner);
    // 2. LayoutManager 설정 (수직 스크롤 목록)
    RecyclerView chatRoomRecyclerView = binding.chatRoomRecyclerView;
    chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(context));

    viewModel.getDataList().observe(lifecycleOwner, list -> {
      Toast.makeText(context, "목록 조회!", Toast.LENGTH_SHORT).show();

      // 또는 기존 어댑터의 데이터를 업데이트:
      if (chattingRoomAdapterList != null) {
        chattingRoomAdapterList.setDataList(list);
      } else {
        chattingRoomAdapterList = new ChattingRoomAdapterList(
          context,
          list,
          userId,
          (ChattingRoom chatting, int position) -> {
            Toast.makeText(context, "채팅방 -> " + chatting.toString(), Toast.LENGTH_SHORT).show();


         });
      }

      chatRoomRecyclerView.setAdapter(chattingRoomAdapterList);
    });
  }
}
