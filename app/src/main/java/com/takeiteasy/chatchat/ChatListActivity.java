package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatListActivity extends AppCompatActivity {

    private BottomNavigationView chatBottomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_list_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ⭐ BottomNavigationView 설정 ⭐ chatBottomTabLayout
        chatBottomTabLayout = findViewById(R.id.chatBottomTabLayout);
        chatBottomTabLayout.setSelectedItemId(R.id.navigation_chats); // 현재 탭을 채팅 목록으로 설정 (ChatListActivity)

        chatBottomTabLayout.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_friends) {
                // '친구 목록' 탭 클릭 시 MainActivity로 이동
                startActivity(new Intent(ChatListActivity.this, MainActivity.class));
                // ⭐⭐ 이 부분이 중요합니다! finish()를 호출하여 현재 Activity(ChatListActivity)를 스택에서 제거합니다. ⭐⭐
                finish(); // 현재 Activity 종료
                return true;
            }
            // TODO: 다른 탭 아이템에 대한 처리 (예: 설정 탭 등)
            return false;
        });
    }
}