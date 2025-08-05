package com.takeiteasy.chatchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    private boolean isFirstLaunch = true;
    private SharedPreferences preferenceManager;// = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    private String loginEmail;
    private String loginUserId; // = preferenceManager.getString("userId", null);
    private MainViewModel viewModel;

    private ImageButton addButton;
    private EditText searchEditText;
    private RecyclerView profileView;
    private BottomNavigationView bottomTabLayout;
    private ProfileDataListAdapter profileListAdapter;
    private List<Parcelable> profileList; // Profile 객체 리스트
    private List<Parcelable> fullProfileList; // ⭐ 원본 전체 친구 목록 ⭐


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        loginUserId = preferenceManager.getString("userId", null);

        // TODO: 친구 목록 Firebase에서 조회 해오기

        searchEditText = findViewById(R.id.searchEditText);
        profileView = findViewById(R.id.profileView);
        bottomTabLayout = findViewById(R.id.bottomTabLayout);

        // Firebase 앱 초기화
        // 이 코드를 추가하면, google-services.json 파일의 설정을 바탕으로 Firebase가 초기화됩니다.
        FirebaseApp.initializeApp(this);

        // 이제부터 Firebase 서비스(예: Firestore, Analytics, Auth)를 사용할 수 있습니다.

        // 2. LayoutManager 설정 (수직 스크롤 목록)
        profileView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 친구 목록 받아오기
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfiles().observe(this, profileList -> {
            Toast.makeText(MainActivity.this, "목록 조회!", Toast.LENGTH_SHORT).show();

            // 또는 기존 어댑터의 데이터를 업데이트:
            if (profileListAdapter != null) {
                profileListAdapter.setProfileDatas(profileList);
            } else {
                profileListAdapter = new ProfileDataListAdapter(this, profileList);
                profileView.setAdapter(profileListAdapter);
            }
        });

        searchEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                    // 돋보기 버튼 클릭 시 검색 수행
                    viewModel.filterProfiles(searchEditText.getText().toString());
//                    performSearch(searchEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.filterProfiles(searchEditText.getText().toString());
                return true;
            }
            return false;
        });

        // 프로필 상세 화면 이동

        // 친구 추가 화면 이동
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            // 친구 추가 화면으로 이동하는 Intent
            Intent friendAddIntent = new Intent(MainActivity.this, FriendAddActivity.class); // AddFriendActivity는 실제 파일명으로 변경해야 합니다.
            friendAddIntent.putExtra("email", loginEmail);
            startActivity(friendAddIntent);
            Toast.makeText(MainActivity.this, "친구 추가 화면으로 이동", Toast.LENGTH_SHORT).show();
        });

        // 채팅목록 화면 이동
        bottomTabLayout.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_chats) { // @menu/bottom_navigation_menu에 정의된 채팅 목록 탭 ID
                // '채팅 목록' 탭 클릭 시 ChatListActivity로 이동
                Intent chatListIntent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(chatListIntent);
                // MainActivity를 종료하여 뒤로가기 버튼 시 채팅 목록 -> 홈 화면으로 가게 할 수 있습니다. (선택 사항)
//                finish();
                return true; // 이벤트를 소비했음을 알림
            }
            // TODO: 다른 탭 아이템에 대한 처리 (예: 설정 탭 등)
            return false; // 이벤트를 소비하지 않았음을 알림
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

//        SharedPreferences.Editor editor = preferenceManager.edit();
//        editor.clear(); // 모든 키-값 쌍 삭제
//        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadProfiles(loginUserId);
    }

    // 화면 회전 시 isFirstLaunch가 재설정되는 것을 방지하려면
    // onSaveInstanceState와 onRestoreInstanceState를 사용할 수 있습니다.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirstLaunch", isFirstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFirstLaunch = savedInstanceState.getBoolean("isFirstLaunch");
    }
}