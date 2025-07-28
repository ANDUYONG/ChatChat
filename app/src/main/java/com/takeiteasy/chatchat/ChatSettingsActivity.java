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

public class ChatSettingsActivity extends AppCompatActivity {
    // 이 상수는 ChatSettingsActivity와 ChatRoomActivity 모두에서 접근 가능해야 합니다.
    public static final int REQUEST_CODE_LEAVE_CHAT = 1001; // 여기에 값을 바인딩합니다.
    private TextView textViewInviteFriend;
    private ImageView imageViewBack;
    private Button buttonLeaveChatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewInviteFriend = findViewById(R.id.textViewInviteFriend);
        buttonLeaveChatRoom = findViewById(R.id.buttonLeaveChatRoom);

        imageViewBack.setOnClickListener(v -> finish());

        // 친구 초대 화면 이동
        textViewInviteFriend.setOnClickListener(v -> {
            Intent friendAddIntent = new Intent(ChatSettingsActivity.this, FriendAddActivity.class);
            startActivity(friendAddIntent);
        });

        // '채팅방 나가기' 버튼 클릭 리스너 설정
        buttonLeaveChatRoom.setOnClickListener(v -> {
            // ChatRoomActivity로 전달할 Intent를 생성합니다.
            // 이 Intent에 "action"이라는 키로 "leave_chat"이라는 값을 넣어,
            // ChatRoomActivity가 이 버튼이 눌렸음을 알 수 있도록 합니다.
            Intent resultIntent = new Intent();
            resultIntent.putExtra("action", "leave_chat");

            // 결과 코드를 RESULT_OK로 설정하고, 위에서 생성한 Intent를 함께 전달합니다.
            setResult(RESULT_OK, resultIntent);

            // 현재 ChatSettingsActivity를 종료합니다.
            finish();
        });
    }
}