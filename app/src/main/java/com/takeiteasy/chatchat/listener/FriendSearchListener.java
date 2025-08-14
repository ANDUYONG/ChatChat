package com.takeiteasy.chatchat.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.takeiteasy.chatchat.ChatListActivity;
import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityFriendSearchBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;
import com.takeiteasy.chatchat.model.chatting.repository.ChattingRoomRepository;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendSearchListener {
  private final ActivityFriendSearchBinding binding;
  private final MainViewModel viewModel;
  private final Context context;

  public FriendSearchListener(ActivityFriendSearchBinding binding, MainViewModel viewModel) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
  }

  @SuppressLint("ClickableViewAccessibility")
  public void initListeners(Consumer<String> consumer) {
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

    // 채팅방 추가 버튼
    binding.addButton.setOnClickListener(v -> {
      ProfileData me = viewModel.getProfile().getValue();
      ChattingRoom data = new ChattingRoom();

      data.setLstProfileUrl(me.getProfileUrl());
      data.setLstSendDate(Timestamp.now());
      data.setCreateDate(Timestamp.now());

      List<ChattingUser> users = this.getSelectedFriend();
      data.setUsers(users);

      ChattingRoomRepository repository = new ChattingRoomRepository();
      repository.createChattingRoom(data, consumer::accept);
    });
  }

  private List<ChattingUser> getSelectedFriend() {
    List<ChattingUser> result = new ArrayList<>();
    List<ProfileData> list = Objects.requireNonNull(viewModel.getProfiles().getValue());
    if(list != null && list.size() > 0) {
      long unreadCnt = list.size();
      Stream<ProfileData> stream = list.stream();

      ProfileData me = viewModel.getProfile().getValue();
      result.add(this.convertToChatUser(me, unreadCnt));

      List<ChattingUser> friends = stream.filter(ProfileData::isSelected).map(x -> {
        return this.convertToChatUser(x, unreadCnt);
      }).collect(Collectors.toList());

      result.addAll(friends);
    }
    return result;
  }

  private ChattingUser convertToChatUser(ProfileData profile, long unreadCnt) {
    ChattingUser user = new ChattingUser();
    user.setUserId(profile.getUserId());
    user.setProfileUrl(profile.getProfileUrl());
    user.setNickName(profile.getNickName());
    user.setUnreadCnt(unreadCnt);
    user.setExist(true);
    user.setPush(true);
    return user;
  }
}
