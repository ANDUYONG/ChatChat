package com.takeiteasy.chatchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.model.signup.SignUpData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton addButton;
    private EditText searchEditText;
    private RecyclerView profileView;
    private BottomNavigationView bottomTabLayout;
    private ProfileDataListAdapter profileListAdapter;
    private List<Parcelable> profileList; // Profile 객체 리스트
    private List<Parcelable> fullProfileList; // ⭐ 원본 전체 친구 목록 ⭐

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

        // TODO: 친구 목록 Firebase에서 조회 해오기

        searchEditText = findViewById(R.id.searchEditText);
        profileView = findViewById(R.id.profileView);
        bottomTabLayout = findViewById(R.id.bottomTabLayout);


        // 2. LayoutManager 설정 (수직 스크롤 목록)
        profileView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 3. ⭐⭐ 친구 데이터 생성 (Profile 객체로 5개 임의 생성하는 부분) ⭐⭐
        profileList = new ArrayList<Parcelable>();
        profileList.add(new ProfileData(
                "user1@example.com", "010-1234-5678", "김지원", "1990-01-01",
                "https://example.com/profile_jimin.jpg", // 예시 이미지 URL
                Arrays.asList("https://example.com/bg_sky.jpg", "https://example.com/bg_city.jpg"), // 예시 배경 URL 목록
                "지원이", "안녕하세요! 잘 부탁드립니다 :)"
        ));
        profileList.add(new ProfileData(
                "user2@example.com", "010-9876-5432", "박준영", "1992-03-15",
                "https://example.com/profile_suga.jpg",
                Arrays.asList("https://example.com/bg_nature.jpg"),
                "준영쓰", "오늘도 코딩 화이팅입니다!"
        ));
        profileList.add(new ProfileData(
                "user3@example.com", "010-2468-1357", "최유진", "1988-07-20",
                "https://example.com/profile_jennie.jpg",
                new ArrayList<>(), // 배경사진이 없을 수도 있습니다.
                "유진님", "새로운 커피 맛집을 찾아서..."
        ));
        profileList.add(new ProfileData(
                "user4@example.com", "010-1122-3344", "정하민", "1995-11-10",
                "https://example.com/profile_rm.jpg",
                Arrays.asList("https://example.com/bg_mountain.jpg", "https://example.com/bg_forest.jpg", "https://example.com/bg_river.jpg"),
                "하민이", "음악 듣는 중 🎧"
        ));
        profileList.add(new ProfileData(
                "user5@example.com", "010-5566-7788", "강서현", "1987-04-25",
                "https://example.com/profile_jisoo.jpg",
                Arrays.asList("https://example.com/bg_beach.jpg"),
                "서현쓰", "배고프다 🍕"
        ));


        fullProfileList = new ArrayList<Parcelable>(profileList);
        profileListAdapter = new ProfileDataListAdapter(profileList);
        profileView.setAdapter(profileListAdapter);
        // 실제 앱에서는 이 데이터를 서버 API 호출을 통해 받아오거나 로컬 데이터베이스에서 로드하게 됩니다.


        // Intent로부터 SignUpData 객체 받기
        SignUpData signUpData = IntentHelper.getExtra(getIntent(), "signUpData", SignUpData.class);
        if (signUpData != null) {
            // 받은 데이터를 UI에 표시하거나 추가 처리 (예: 서버로 전송)
            //                receivedEmailTextView.setText("이메일: " + signUpData.getEmail());
            //                receivedBirthdayTextView.setText("생년월일: " + signUpData.getBirthday());
            //                receivedPhoneTextView.setText("전화번호: " +
            //                        signUpData.getPhone1() + "-" +
            //                        signUpData.getPhone2() + "-" +
            //                        signUpData.getPhone3());

            // 참고: 비밀번호는 민감한 정보이므로, UI에 직접 표시하거나 로그에 남기지 않도록 주의하세요.
            // 여기서는 예시로만 보여드립니다.
            // String receivedPassword = signUpData.getPassword();
            // Toast.makeText(this, "받은 비밀번호: " + receivedPassword, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "회원가입 데이터를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

        searchEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                    // 돋보기 버튼 클릭 시 검색 수행
                    performSearch(searchEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.getText().toString());
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
            friendAddIntent.putParcelableArrayListExtra("fullProfileList", new ArrayList<>(fullProfileList));
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 모든 키-값 쌍 삭제
        editor.apply();
    }

    private void performSearch(String query) {
        // 검색어를 소문자로 변환하여 대소문자 구분 없이 검색
        String lowerCaseQuery = query.toLowerCase();

        // 검색 결과 저장할 리스트
//        List<? super Parcelable> orginalList = new ArrayList<>(fullProfileList);
        List<ProfileData> filteredList = new ArrayList<>();

        // 검색어가 비어있거나 공백이라면 전체 목록을 표시
        if (lowerCaseQuery.isEmpty()) {
            if(fullProfileList.size() > 0 && !fullProfileList.isEmpty()) {
                List<ProfileData> list = new ArrayList<>();
                for(Parcelable data : fullProfileList) {
                    list.add((ProfileData) data);
                }
                filteredList.addAll(list);
            }
        } else {
            // 원본 전체 목록(fullProfileList)에서 검색
            for (Parcelable parcel : fullProfileList) {
                ProfileData profile = (ProfileData) parcel;
                // 닉네임(getNickName())이 검색어를 포함하는지 확인
                if (profile.getNickName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(profile);
                }
            }
        }

        // 어댑터의 데이터를 필터링된 목록으로 업데이트하고 RecyclerView 갱신
        profileListAdapter.setProfileDatas(filteredList); // ProfileDataListAdapter에 setProfileDatas 메서드가 있어야 합니다.
    }

}