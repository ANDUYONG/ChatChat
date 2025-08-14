package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.binder.FriendSearchBinder;
import com.takeiteasy.chatchat.binder.MainBinder;
import com.takeiteasy.chatchat.databinding.ActivityFriendSearchBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.listener.FriendSearchListener;
import com.takeiteasy.chatchat.listener.MainListener;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendSearchActivity extends AppCompatActivity {
  private ActivityFriendSearchBinding binding;
  private MainViewModel viewModel;
  private FriendSearchBinder binder;
  private FriendSearchListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 뷰 바인딩 초기화
        binding = ActivityFriendSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new FriendSearchBinder(binding);
        listener = new FriendSearchListener(binding, viewModel);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        // 4. 컴포넌트 들을 서로 연결
        binder.bind(viewModel, this, userId);
        listener.initListeners(uid -> {
          // Intent에 결과를 담습니다.
          Intent resultIntent = new Intent();
          resultIntent.putExtra("uid", uid);
          // 결과를 설정하고 Activity를 종료합니다.
          setResult(RESULT_OK, resultIntent);
          finish();
        });
    }
}
