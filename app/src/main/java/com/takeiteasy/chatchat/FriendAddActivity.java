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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendAddActivity extends AppCompatActivity {

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

        findEditText = findViewById(R.id.findEditText);
        profileView = findViewById(R.id.profileView);
        imageViewBack = findViewById(R.id.imageViewBack);

        Intent intent = getIntent();
        fullProfileList = intent.getParcelableArrayListExtra("fullProfileList");

        profileView.setLayoutManager(new LinearLayoutManager(FriendAddActivity.this));
        ArrayList<Parcelable> defaultList = new ArrayList<>();
        profileListAdapter = new ProfileDataListAdapter(defaultList);
        profileView.setAdapter(profileListAdapter);
//        if(fullProfileList != null && !fullProfileList.isEmpty()) {
//            profileListAdapter = new ProfileDataListAdapter(fullProfileList);
//            profileView.setAdapter(profileListAdapter);
//        }

        // 닫기 > 친구목록으로 이동
        imageViewBack.setOnClickListener(v -> {
            finish();
        });

        findEditText.setOnTouchListener((v, event) -> {
            // 돋보기 아이콘(drawableEnd)이 클릭되었는지 확인
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // getCompoundDrawables()[2]는 drawableEnd를 의미합니다.
                if (event.getRawX() >= (findEditText.getRight() - findEditText.getCompoundDrawables()[2].getBounds().width())) {
                    // 돋보기 버튼 클릭 시 검색 수행
                    performSearch(findEditText.getText().toString());
                    return true;
                }
            }
            return false;
        });

        // (선택 사항) 키보드의 검색(돋보기) 버튼 처리
        findEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(findEditText.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        // performSearch 진입 시 profileListAdapter의 상태 로그
        if (profileListAdapter == null) {
            Log.e("FriendAddActivity", "performSearch: profileListAdapter is NULL at method entry! Cannot update RecyclerView.");
            Toast.makeText(this, "친구 목록을 로드할 수 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            return; // 어댑터가 없으면 더 이상 진행하지 않습니다.
        } else {
            Log.d("FriendAddActivity", "performSearch: profileListAdapter is NOT NULL. Adapter Hash: " + System.identityHashCode(profileListAdapter));
        }

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
                if (profile.getEmail().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(profile);
                }
            }
        }

        // 어댑터의 데이터를 필터링된 목록으로 업데이트하고 RecyclerView 갱신
        profileListAdapter.setProfileDatas(filteredList); // ProfileDataListAdapter에 setProfileDatas 메서드가 있어야 합니다.
    }
}