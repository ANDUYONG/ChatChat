package com.takeiteasy.chatchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;

public class ProfileDetailActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private SharedPreferences preferenceManager;
    private String loginUserId; // = preferenceManager.getString("userId", null);
    private MainViewModel viewModel;
    private ProfileData profile;
    private ImageView profileView;
    private ImageView profileViewClose;
    private ImageView profileViewBackground;

    private TextView textViewUserName;
    private TextView textViewStatusMessage;

    private LinearLayout layoutChat;
    private LinearLayout layoutEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileViewClose = findViewById(R.id.profileViewClose);
        profileView = findViewById(R.id.profileView);
        profileViewBackground = findViewById(R.id.profileViewBackground);
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewStatusMessage = findViewById(R.id.textViewStatusMessage);

        // 친구 목록 받아오기
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfile().observe(this, response -> {
            profile = response;
            if (profile != null) {
                if(!loginUserId.equals(profile.getUserId())) {
                    // LinearLayout을 보이지 않게 하지만, 공간은 그대로 유지합니다.
                    layoutEditProfile.setVisibility(View.INVISIBLE);
                }

                // 받은 데이터를 UI에 표시하거나 추가 처리 (예: 서버로 전송)
                // 1. 조회된 사용자 이름
                textViewUserName.setText(profile.getNickName());
                // 2. 사용자 상메
                textViewStatusMessage.setText(profile.getStatusMsg());
                // 3. 이미지 url 바인딩
                this.loadImage(profileView);
                this.loadImage(profileViewBackground);

            }
        });

        // 닫기 > 친구목록으로 이동
        profileViewClose.setOnClickListener(v -> {
            finish();
        });

        // 1. 프로필 상세로 이동
        profileView.setOnClickListener(v -> {
            Intent profileViewIntent = new Intent(ProfileDetailActivity.this, ProfileImageActivity.class);
            profileViewIntent.putExtra("profileUrl", profile.getProfileUrl());
            startActivity(profileViewIntent);
        });

        // 2. 프로필 배경 상세로 이동
        profileViewBackground.setOnClickListener(v -> {
            Intent profileViewBackgroundIntent = new Intent(ProfileDetailActivity.this, ProfileBackgroundActivity.class);
            profileViewBackgroundIntent.putStringArrayListExtra("backgroundUrls", (ArrayList<String>) profile.getBackgroundUrls());
            startActivity(profileViewBackgroundIntent);
        });

        // XML 레이아웃에서 해당 뷰들을 ID로 찾습니다.
        layoutChat = findViewById(R.id.layoutChat);
        layoutEditProfile = findViewById(R.id.layoutEditProfile);

        // layoutChat에 클릭 리스너 설정
        // 1:1 대화  하기 버튼 클릭시
        layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1:1 채팅 버튼이 클릭되었을 때 실행할 코드
                // 예: 채팅 화면으로 이동
                 Intent chatIntent = new Intent(ProfileDetailActivity.this,  ChatRoomActivity.class);
                 startActivity(chatIntent);
                // Log.d("ProfileDetailActivity", "1:1 채팅 버튼 클릭됨");
            }
        });

        // layoutEditProfile에 클릭 리스너 설정
        // 프로필 편집 클릭시
        layoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileEditIntent = new Intent(ProfileDetailActivity.this,  ProfileEditActivity.class);
                profileEditIntent.putExtra("profile", profile);
                startActivity(profileEditIntent);
                // 프로필 편집 버튼이 클릭되었을 때 실행할 코드
                // 예: 프로필 편집 화면으로 이동
                // Intent editProfileIntent = new Intent(ProfileDetailActivity.this, EditProfileActivity.class);
                // startActivity(editProfileIntent);
                // Log.d("ProfileDetailActivity", "프로필 편집 버튼 클릭됨");
            }
        });
    }

    private void loadImage(ImageView imageView) {
        // 이것은 로컬 벡터 드로어블입니다.
        // 안드로이드 내장 기능을 사용해 이미지를 직접 설정합니다.
        // 이것은 Firebase Storage에서 가져올 원격 이미지입니다.
        if(profile.getProfileUrl() != null && !profile.getProfileUrl().isEmpty()) {
            String imageUri = "default_profile_background.png";
            if (profileView.getId() == imageView.getId()) {
                imageUri = profile.getProfileUrl();
            } else {
                int size = profile.getBackgroundUrls().size();
                if(profile.getBackgroundUrls() != null && size > 0) {
                    imageUri = profile.getBackgroundUrls().get(size-1);
                }
            }
            // Firebase Storage 인스턴스와 참조를 가져옵니다.
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageUri);

            // 다운로드 URL을 가져옵니다.
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
                Glide.with(this)
                        .load(uri)
                        .into(imageView);
            }).addOnFailureListener(exception -> {
                // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
                System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
                // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
            });
        } else {
            if (profileView.getId() == imageView.getId()) {
                imageView.setImageResource(R.drawable.ic_default_profile_filled);
            } else {
                imageView.setImageResource(R.drawable.default_profile_background);
            }
        }
    }

    private void loadBackground(ImageView imageView) {
        // Firebase Storage 인스턴스 가져오기
        storage = FirebaseStorage.getInstance();
        // 이미지를 로드할 Storage 참조 생성
        // "images/my_profile_pic.jpg"는 Firebase Storage에 업로드된 이미지의 경로입니다.
//        ic_default_profile_filled
        String imageUri = "chatchat/default/default_profile.png";
//        if(profileView.getId() == imageView.getId()) {
//            imageUri = "";
//        }
        StorageReference imageRef = storage.getReference().child(imageUri);


        // 다운로드 URL 가져오기
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // URL을 성공적으로 가져온 경우
            // Glide를 사용하여 이미지를 ImageView에 로드
            Glide.with(this)
                    .load(uri) // ✨ HTTPS URL (Uri 객체)을 전달
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // URL 가져오기 실패 (예: 파일이 없거나, 권한 문제)
            System.err.println("이미지 다운로드 URL 가져오기 실패: " + exception.getMessage());
            // 오류 이미지를 표시하거나 사용자에게 알림
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(ProfileDetailActivity.this);
        Intent intent = getIntent();
        profile = intent.getParcelableExtra("profileData");
        loginUserId = profile.getUserId();
        viewModel.loadProfile(loginUserId);
    }
}