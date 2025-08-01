package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.FriendData;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendAddActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private String loginEmail;
    private ProfileData selectedProfile = null;

    private MaterialButton buttonAddFriend;
    private EditText findEditText;
    private RecyclerView profileView;
    private ImageView imageViewBack;
    private ProfileDataListAdapter profileListAdapter;
    private List<Parcelable> profileList; // Profile 객체 리스트
    private List<Parcelable> fullProfileList; // ⭐ 원본 전체 친구 목록 ⭐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonAddFriend = findViewById(R.id.buttonAddFriend);
        findEditText = findViewById(R.id.findEditText);
        profileView = findViewById(R.id.profileView);
        imageViewBack = findViewById(R.id.imageViewBack);

        Intent intent = getIntent();
        loginEmail = intent.getExtras().getString("email");

        // 2. LayoutManager 설정 (수직 스크롤 목록)
        profileView.setLayoutManager(new LinearLayoutManager(FriendAddActivity.this));

        // 친구 목록 받아오기
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfiles().observe(this, profileList -> {
            // 또는 기존 어댑터의 데이터를 업데이트:
            if (profileListAdapter != null) {
                profileListAdapter.setProfileDatas(profileList);
            } else {
                profileListAdapter = new ProfileDataListAdapter(profileList, (profile, position) -> {
                    selectedProfile = profile;
                });
                profileView.setAdapter(profileListAdapter);
            }
        });

        viewModel.getStatus().observe(this, reponseStatus -> {
            switch(reponseStatus) {
                case SUCCESS:
                    Toast.makeText(FriendAddActivity.this, "친구 추가!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case FAILURE:
                    Toast.makeText(FriendAddActivity.this, "친구 추가 실패!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        });

        // 닫기 > 친구목록으로 이동
        imageViewBack.setOnClickListener(v -> {
            finish();
        });

        // 친구 추가 버튼 클릭
        buttonAddFriend.setOnClickListener(v -> {
            if(selectedProfile != null) {
                String userId = selectedProfile.getUserId();
                String email = selectedProfile.getEmail();
                viewModel.addFriend(loginEmail, new FriendData(userId, email, false, false));
            }
            else
                Toast.makeText(FriendAddActivity.this, "선택된 프로필이 없습니다.", Toast.LENGTH_SHORT).show();
        });

        findEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (findEditText.getRight() - findEditText.getCompoundDrawables()[2].getBounds().width())) {
                    viewModel.searchProfiles(findEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        findEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchProfiles(findEditText.getText().toString());
                return true;
            }
            return false;
        });
    }
}