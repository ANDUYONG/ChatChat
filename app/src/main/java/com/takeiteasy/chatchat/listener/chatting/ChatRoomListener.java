package com.takeiteasy.chatchat.listener.chatting;

import static android.app.Activity.RESULT_OK;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.takeiteasy.chatchat.ChatRoomActivity;
import com.takeiteasy.chatchat.ChatSettingsActivity;
import com.takeiteasy.chatchat.databinding.ActivityChatRoomBinding;
import com.takeiteasy.chatchat.model.chatting.ChattingGroup;
import com.takeiteasy.chatchat.model.chatting.ChattingMsg;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;
import com.takeiteasy.chatchat.model.chatting.adapter.ChattingMsgAdapterList;
import com.takeiteasy.chatchat.model.chatting.repository.ChattingRoomRepository;
import com.takeiteasy.chatchat.viewmodel.ChatMsgViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatRoomListener {
  private final ActivityChatRoomBinding binding;
  private final ChatMsgViewModel viewModel;
  private final Context context;
  private ChattingMsgAdapterList chattingMsgAdapterList;
  private AppCompatActivity activity;
  private boolean isMultimediaLayoutVisible = false; // 멀티미디어 레이아웃 가시성 상태
  private ActivityResultLauncher<Intent> chatSettingsLauncher;
  private String currentUserId;
  private ChattingRoom room;

  public ChatRoomListener(
    ActivityChatRoomBinding binding,
    ChatMsgViewModel viewModel,
    AppCompatActivity activity) {
    this.binding = binding;
    this.viewModel = viewModel;
    this.context = binding.getRoot().getContext();
    this.activity = activity;
  }

  public void initListeners(LifecycleOwner owner, String userId, ChattingRoom room) {
    this.currentUserId = userId;
    this.room = room;

    // 초기 상태 설정: 멀티미디어 레이아웃 숨김
    binding.layoutMultimediaAttach.setVisibility(View.GONE);
    // 애니메이션을 위해 초기 위치를 화면 아래로 설정 (onCreate 시점에는 height가 0일 수 있으므로 주의)
    // layoutMultimediaAttach.setTranslationY(layoutMultimediaAttach.getHeight());

    // 클릭 리스너 설정
    binding.imageViewBack.setOnClickListener(v -> activity.onBackPressed()); // 뒤로가기 버튼
    // 뒤로가기 버튼 콜백 등록 (Android 12 이상 권장 방식)
    activity.getOnBackPressedDispatcher().addCallback(owner, new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        // 멀티미디어 레이아웃이 열려있으면 먼저 닫기
        if (isMultimediaLayoutVisible) {
          hideMultimediaLayout();
        } else {
          // 멀티미디어 레이아웃이 닫혀있으면 액티비티 종료
          setEnabled(false); // 이 콜백을 비활성화하고
          activity.onBackPressed(); // 기본 뒤로가기 동작을 수행 (액티비티 종료)
        }
      }
    });

    // 채팅방 설정 화면 이동
    binding.imageViewMenu.setOnClickListener(v -> {
      Intent settingsIntent = new Intent(context, ChatSettingsActivity.class);
      settingsIntent.putExtra("userId", currentUserId);
      settingsIntent.putExtra("chattingRoom", room);
      chatSettingsLauncher.launch(settingsIntent);
    });
    binding.imageViewSend.setOnClickListener(v -> sendMessage());

    // '+' 아이콘 클릭 리스너
    binding.imageViewAttach.setOnClickListener(v -> toggleMultimediaLayout());

    // 멀티미디어 버튼 클릭 리스너 (예시)
    binding.buttonCamera.setOnClickListener(v -> Toast.makeText(context, "카메라 버튼 클릭", Toast.LENGTH_SHORT).show());
    binding.buttonPhoto.setOnClickListener(v -> Toast.makeText(context, "사진 버튼 클릭", Toast.LENGTH_SHORT).show());
    binding.buttonFile.setOnClickListener(v -> Toast.makeText(context, "파일 버튼 클릭", Toast.LENGTH_SHORT).show());

    // 메시지 입력 EditText에 포커스가 생기면 멀티미디어 레이아웃 숨기기
    binding.editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus && isMultimediaLayoutVisible) {
        hideMultimediaLayout();
      }
    });

    // 채팅 메시지 RecyclerView를 스크롤하면 멀티미디어 레이아웃 숨기기 (선택 사항)
    binding.recyclerViewChatMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && isMultimediaLayoutVisible) {
          hideMultimediaLayout();
        }
      }
    });



    // ActivityResultLauncher 등록
    chatSettingsLauncher = activity.registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      result -> {
        // ChatSettingsActivity로부터 결과가 돌아왔을 때 처리
        if (result.getResultCode() == RESULT_OK) {
          Intent data = result.getData();
          if (data != null && "leave_chat".equals(data.getStringExtra("action"))) {
            // ChatSettingsActivity에서 "채팅방 나가기"를 선택했으므로,
            // 현재 ChatRoomActivity도 종료하여 전전 액티비티로 돌아갑니다.
            activity.finish();
          }
        }
      }
    );
  }

  // 메시지 보내기 기능 (예시)
  private void sendMessage() {
    String message = binding.editTextMessage.getText().toString().trim();
    if (!message.isEmpty()) {
      Toast.makeText(context, "메시지 전송: " + message, Toast.LENGTH_SHORT).show();
      binding.editTextMessage.setText(""); // 메시지 입력창 비우기
      // TODO: 실제 메시지 전송 로직 (DB 저장, 서버 전송 등)

      ChattingGroup group = viewModel.getData().getValue();
      ChattingUser user = room.getUsers()
        .stream()
        .filter(x -> x.getUserId().equals(currentUserId))
        .findAny()
        .orElse(new ChattingUser());

      List<String> onUsers = Objects.requireNonNull(group).getOnUsers();
      Stream<ChattingUser> filterUser;
      List<String> unreadUsers = Collections.emptyList();
      if(onUsers != null && !onUsers.isEmpty()) {
        filterUser = room.getUsers()
          .stream()
          .filter(x -> !x.getUserId().equals(currentUserId) && !onUsers.contains(x.getUserId()));
      } else {
        filterUser = room.getUsers()
          .stream()
          .filter(x -> !x.getUserId().equals(currentUserId));
      }
      unreadUsers = filterUser.map(x -> x.getUserId()).collect(Collectors.toList());

      ChattingMsg msg = new ChattingMsg();
      msg.setUserId(currentUserId);
      msg.setProfileUrl(user.getProfileUrl());
      msg.setNickName(user.getNickName());
      msg.setUnreadUsers(unreadUsers);
      msg.setType("msg");
      msg.setMsg(message);
      msg.setSendDate(Timestamp.now());

      viewModel.sendMsg(group.getUid(), msg, reponseStatus -> {
        Toast.makeText(context, "메시지 전송 성공!!: " + message, Toast.LENGTH_SHORT).show();
      });
    } else {
      Toast.makeText(context, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
    }
  }

  // 멀티미디어 레이아웃 토글 기능
  private void toggleMultimediaLayout() {
    if (isMultimediaLayoutVisible) {
      hideMultimediaLayout();
    } else {
      showMultimediaLayout();
    }
  }

  // 멀티미디어 레이아웃 표시
  private void showMultimediaLayout() {
    // 키보드 숨기기
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(binding.editTextMessage.getWindowToken(), 0);

    binding.layoutMultimediaAttach.setVisibility(View.VISIBLE);
    isMultimediaLayoutVisible = true;

    // 애니메이션: 아래에서 위로 슬라이드
    // 뷰의 높이가 아직 측정되지 않았을 수 있으므로 ViewTreeObserver를 사용하여 안전하게 높이를 가져옵니다.
    binding.layoutMultimediaAttach.post(() -> {
      int targetHeight = binding.layoutMultimediaAttach.getHeight();
      binding.layoutMultimediaAttach.setTranslationY(targetHeight); // 초기 위치를 자신의 높이만큼 아래로 설정

      ObjectAnimator animator = ObjectAnimator.ofFloat(binding.layoutMultimediaAttach, "translationY", 0f);
      animator.setDuration(300); // 0.3초 애니메이션
      animator.start();
    });
  }

  // 멀티미디어 레이아웃 숨기기
  private void hideMultimediaLayout() {
    isMultimediaLayoutVisible = false;

    // 애니메이션: 위에서 아래로 슬라이드
    ObjectAnimator animator = ObjectAnimator.ofFloat(binding.layoutMultimediaAttach, "translationY", binding.layoutMultimediaAttach.getHeight());
    animator.setDuration(300); // 0.3초 애니메이션
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        binding.layoutMultimediaAttach.setVisibility(View.GONE); // 애니메이션 종료 후 숨김
        binding.layoutMultimediaAttach.setTranslationY(0); // 위치 초기화 (다음 표시를 위해)
      }
    });
    animator.start();
  }

  public void leaveChattingRoom() {
    viewModel.leaveChattingRoom(currentUserId, reponseStatus -> {});
  }
}
