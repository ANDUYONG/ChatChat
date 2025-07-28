package com.takeiteasy.chatchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

public class ChatRoomActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextView textViewChatRoomTitle;
    private ImageView imageViewMenu;
    private RecyclerView recyclerViewChatMessages;
    private ImageView imageViewAttach; // '+' 아이콘
    private EditText editTextMessage;
    private ImageView imageViewSend;

    // 새로 추가된 멀티미디어 첨부 레이아웃
    private ConstraintLayout layoutMultimediaAttach;
    private LinearLayout buttonCamera;
    private LinearLayout buttonPhoto;
    private LinearLayout buttonFile;

    private boolean isMultimediaLayoutVisible = false; // 멀티미디어 레이아웃 가시성 상태

    private ActivityResultLauncher<Intent> chatSettingsLauncher;

//    private ChatMessageAdapter chatMessageAdapter;
//    private List<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI 요소 초기화
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewChatRoomTitle = findViewById(R.id.textViewChatRoomTitle);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        recyclerViewChatMessages = findViewById(R.id.recyclerViewChatMessages);
        imageViewAttach = findViewById(R.id.imageViewAttach);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSend = findViewById(R.id.imageViewSend);

        // 멀티미디어 첨부 레이아웃 및 버튼 초기화
        layoutMultimediaAttach = findViewById(R.id.layoutMultimediaAttach);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonPhoto = findViewById(R.id.buttonPhoto);
        buttonFile = findViewById(R.id.buttonFile);

        // 초기 상태 설정: 멀티미디어 레이아웃 숨김
        layoutMultimediaAttach.setVisibility(View.GONE);
        // 애니메이션을 위해 초기 위치를 화면 아래로 설정 (onCreate 시점에는 height가 0일 수 있으므로 주의)
        // layoutMultimediaAttach.setTranslationY(layoutMultimediaAttach.getHeight());

        // 클릭 리스너 설정
        imageViewBack.setOnClickListener(v -> onBackPressed()); // 뒤로가기 버튼
        // 뒤로가기 버튼 콜백 등록 (Android 12 이상 권장 방식)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 멀티미디어 레이아웃이 열려있으면 먼저 닫기
                if (isMultimediaLayoutVisible) {
                    hideMultimediaLayout();
                } else {
                    // 멀티미디어 레이아웃이 닫혀있으면 액티비티 종료
                    setEnabled(false); // 이 콜백을 비활성화하고
                    onBackPressed(); // 기본 뒤로가기 동작을 수행 (액티비티 종료)
                }
            }
        });

        // 채팅방 설정 화면 이동
        imageViewMenu.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ChatRoomActivity.this, ChatSettingsActivity.class);
            chatSettingsLauncher.launch(settingsIntent);
        });
        imageViewSend.setOnClickListener(v -> sendMessage());

        // '+' 아이콘 클릭 리스너
        imageViewAttach.setOnClickListener(v -> toggleMultimediaLayout());

        // 멀티미디어 버튼 클릭 리스너 (예시)
        buttonCamera.setOnClickListener(v -> Toast.makeText(this, "카메라 버튼 클릭", Toast.LENGTH_SHORT).show());
        buttonPhoto.setOnClickListener(v -> Toast.makeText(this, "사진 버튼 클릭", Toast.LENGTH_SHORT).show());
        buttonFile.setOnClickListener(v -> Toast.makeText(this, "파일 버튼 클릭", Toast.LENGTH_SHORT).show());

        // 메시지 입력 EditText에 포커스가 생기면 멀티미디어 레이아웃 숨기기
        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isMultimediaLayoutVisible) {
                hideMultimediaLayout();
            }
        });

        // 채팅 메시지 RecyclerView를 스크롤하면 멀티미디어 레이아웃 숨기기 (선택 사항)
        recyclerViewChatMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && isMultimediaLayoutVisible) {
                    hideMultimediaLayout();
                }
            }
        });



        // ActivityResultLauncher 등록
        chatSettingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // ChatSettingsActivity로부터 결과가 돌아왔을 때 처리
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && "leave_chat".equals(data.getStringExtra("action"))) {
                        // ChatSettingsActivity에서 "채팅방 나가기"를 선택했으므로,
                        // 현재 ChatRoomActivity도 종료하여 전전 액티비티로 돌아갑니다.
                        finish();
                    }
//                    finish();
                }
            }
        );
    }

    // 메시지 보내기 기능 (예시)
    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            Toast.makeText(this, "메시지 전송: " + message, Toast.LENGTH_SHORT).show();
            editTextMessage.setText(""); // 메시지 입력창 비우기
            // TODO: 실제 메시지 전송 로직 (DB 저장, 서버 전송 등)
        } else {
            Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);

        layoutMultimediaAttach.setVisibility(View.VISIBLE);
        isMultimediaLayoutVisible = true;

        // 애니메이션: 아래에서 위로 슬라이드
        // 뷰의 높이가 아직 측정되지 않았을 수 있으므로 ViewTreeObserver를 사용하여 안전하게 높이를 가져옵니다.
        layoutMultimediaAttach.post(() -> {
            int targetHeight = layoutMultimediaAttach.getHeight();
            layoutMultimediaAttach.setTranslationY(targetHeight); // 초기 위치를 자신의 높이만큼 아래로 설정

            ObjectAnimator animator = ObjectAnimator.ofFloat(layoutMultimediaAttach, "translationY", 0f);
            animator.setDuration(300); // 0.3초 애니메이션
            animator.start();
        });
    }

    // 멀티미디어 레이아웃 숨기기
    private void hideMultimediaLayout() {
        isMultimediaLayoutVisible = false;

        // 애니메이션: 위에서 아래로 슬라이드
        ObjectAnimator animator = ObjectAnimator.ofFloat(layoutMultimediaAttach, "translationY", layoutMultimediaAttach.getHeight());
        animator.setDuration(300); // 0.3초 애니메이션
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layoutMultimediaAttach.setVisibility(View.GONE); // 애니메이션 종료 후 숨김
                layoutMultimediaAttach.setTranslationY(0); // 위치 초기화 (다음 표시를 위해)
            }
        });
        animator.start();
    }
}