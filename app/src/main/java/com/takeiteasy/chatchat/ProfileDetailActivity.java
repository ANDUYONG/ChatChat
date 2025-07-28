package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.signup.SignUpData;

public class ProfileDetailActivity extends AppCompatActivity {
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

        Intent intent = getIntent();
        ProfileData profile = IntentHelper.getExtra(getIntent(), "profileData", ProfileData.class);
        if (profile != null) {

           // 받은 데이터를 UI에 표시하거나 추가 처리 (예: 서버로 전송)
            // 1. 조회된 사용자 이름
            textViewUserName.setText(profile.getNickName());
            // 2. 사용자 상메
            textViewStatusMessage.setText(profile.getStatusMsg());
            // 3. 이미지 url 바인딩
            // Glide를 사용하여 이미지 URL을 profileView에 로드합니다.
            // with(context): 이미지를 로드할 컨텍스트를 제공합니다. (Activity, Fragment, Context 등)
            // load(imageUrl): 로드할 이미지의 URL을 지정합니다.
            // placeholder(drawableResId): 이미지가 로드되는 동안 표시될 기본 이미지(로딩 이미지)를 설정합니다.
            // error(drawableResId): 이미지 로드 실패 시 표시될 이미지를 설정합니다.
            // into(imageView): 이미지를 표시할 ImageView를 지정합니다.
            String imageUrl = null; // 예시 이미지 URL
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_profile_filled) // 로딩 중 표시될 이미지 (기존 기본 프로필 이미지)
                    .error(R.drawable.ic_default_profile_filled) // 로드 실패 시 표시될 이미지 (예시: 오류 아이콘)
                    .into(profileView);

            // 만약 이미지 URL이 null이거나 비어있을 경우, 기본 이미지를 설정할 수 있습니다.
            if (imageUrl == null || imageUrl.isEmpty()) {
                profileView.setImageResource(R.drawable.ic_default_profile_filled);
            }

            // 4. 배경화면 urls 바인딩
            String backgroundUrl = null; // 예시 이미지 URL
            Glide.with(this)
                    .load(backgroundUrl)
                    .placeholder(R.drawable.default_profile_background) // 로딩 중 표시될 이미지 (기존 기본 프로필 이미지)
                    .error(R.drawable.default_profile_background) // 로드 실패 시 표시될 이미지 (예시: 오류 아이콘)
                    .into(profileViewBackground);

            // 만약 이미지 URL이 null이거나 비어있을 경우, 기본 이미지를 설정할 수 있습니다.
            if (backgroundUrl == null || backgroundUrl.isEmpty()) {
                profileViewBackground.setImageResource(R.drawable.default_profile_background);
            }

        } else {
            Toast.makeText(this, "회원가입 데이터를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

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
            profileViewBackgroundIntent.putExtra("backgroundUrls", profile.getBackgroundUrls().toArray());
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
}