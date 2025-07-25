package com.takeiteasy.chatchat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRoomActivity extends AppCompatActivity {

    private ImageView imageViewBack;
    private TextView textViewChatRoomTitle;
    private ImageView imageViewMenu;
    private RecyclerView recyclerViewChatMessages;
    private EditText editTextMessage;
    private ImageView imageViewAttach;
    private ImageView imageViewSend;

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
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewAttach = findViewById(R.id.imageViewAttach);
        imageViewSend = findViewById(R.id.imageViewSend);

        // 뒤로 가기
        imageViewBack.setOnClickListener(v -> finish());
    }
}