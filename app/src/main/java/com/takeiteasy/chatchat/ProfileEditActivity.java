package com.takeiteasy.chatchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.util.List;

public class ProfileEditActivity extends AppCompatActivity {

    // 메인 화면 UI 요소들
    private ImageView profileViewBackground;
    private ImageView imageViewProfilePicture; // 프로필 사진 ImageView (XML ID와 일치)
    private ImageView imageViewCameraIcon; // 프로필 사진 변경 카메라 아이콘
    private TextView textViewUserName; // 사용자 이름 TextView
    private ImageView imageViewEditName; // 이름 편집 연필 아이콘
    private TextView textViewStatusMessage; // 상태 메시지 TextView
    private ImageView imageViewEditStatus; // 상태 메시지 편집 연필 아이콘
    private ImageView imageViewClose; // 닫기 버튼
    private LinearLayout layoutBackgroundChange; // 배경화면 변경 버튼 추가

    // 오버레이 화면 UI 요소들
    private FrameLayout frameLayoutOverlay;
    private ImageView imageViewOverlayClose;
    private ImageView imageViewOverlaySave;
    private EditText editTextOverlayInput;

    // 현재 편집 중인 필드를 추적하는 플래그
    private static final int EDIT_NONE = 0;
    private static final int EDIT_NAME = 1;
    private static final int EDIT_STATUS_MESSAGE = 2;
    private int currentEditingField = EDIT_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- 메인 화면 UI 요소 초기화 ---
        profileViewBackground = findViewById(R.id.profileViewBackground);
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        imageViewCameraIcon = findViewById(R.id.imageViewCameraIcon);
        textViewUserName = findViewById(R.id.textViewUserName);
        imageViewEditName = findViewById(R.id.imageViewEditName);
        textViewStatusMessage = findViewById(R.id.textViewStatusMessage);
        imageViewEditStatus = findViewById(R.id.imageViewEditStatus);
        imageViewClose = findViewById(R.id.imageViewClose);
        layoutBackgroundChange = findViewById(R.id.layoutBackgroundChange); // 배경화면 변경 버튼 초기화

        // --- 오버레이 화면 UI 요소 초기화 ---
        frameLayoutOverlay = findViewById(R.id.frameLayoutOverlay);
        imageViewOverlayClose = findViewById(R.id.imageViewOverlayClose);
        imageViewOverlaySave = findViewById(R.id.imageViewOverlaySave);
        editTextOverlayInput = findViewById(R.id.editTextOverlayInput);

        // --- Intent에서 ProfileData 가져와 UI에 바인딩 ---
        Intent intent = getIntent();
        ProfileData profile = IntentHelper.getExtra(intent, "profile", ProfileData.class);
        if (profile != null) {
            // 사용자 이름 바인딩
            textViewUserName.setText(profile.getNickName() != null ? profile.getNickName() : "");
            // 사용자 상태 메시지 바인딩
            textViewStatusMessage.setText(profile.getStatusMsg() != null ? profile.getStatusMsg() : "");

            // 프로필 이미지 URL 바인딩 (Glide 사용)
            String profileImageUrl = profile.getProfileUrl();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_default_profile_filled)
                        .error(R.drawable.ic_default_profile_filled)
                        .into(imageViewProfilePicture);
            } else {
                imageViewProfilePicture.setImageResource(R.drawable.ic_default_profile_filled);
            }

            // 배경화면 URL 바인딩 (Glide 사용)
            List<String> backgroundUrls = profile.getBackgroundUrls();
            String backgroundUrl = null;
            if (backgroundUrls != null && !backgroundUrls.isEmpty()) {
                backgroundUrl = backgroundUrls.get(0); // 첫 번째 배경 이미지를 사용
            }

            if (backgroundUrl != null && !backgroundUrl.isEmpty()) {
                Glide.with(this)
                        .load(backgroundUrl)
                        .placeholder(R.drawable.default_profile_background)
                        .error(R.drawable.default_profile_background)
                        .into(profileViewBackground);
            } else {
                profileViewBackground.setImageResource(R.drawable.default_profile_background);
            }

        } else {
            Toast.makeText(this, "프로필 데이터를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
            // 데이터가 없으면 기본값 설정 또는 액티비티 종료
            textViewUserName.setText("이름 없음");
            textViewStatusMessage.setText("상태 메시지 없음");
            imageViewProfilePicture.setImageResource(R.drawable.ic_default_profile_filled);
            profileViewBackground.setImageResource(R.drawable.default_profile_background);
        }

        // --- 클릭 리스너 설정 ---
        // 1. 편집화면 닫기
        imageViewClose.setOnClickListener(v -> {
            finish();
        });

        // 이름 편집 연필 아이콘 클릭 시
        imageViewEditName.setOnClickListener(v -> {
            frameLayoutOverlay.setVisibility(View.VISIBLE); // 오버레이 표시
            editTextOverlayInput.setText(textViewUserName.getText()); // 현재 이름을 EditText에 설정
            editTextOverlayInput.setHint("이름을 입력하세요"); // 힌트 설정
            currentEditingField = EDIT_NAME; // 이름 편집 모드 설정
            editTextOverlayInput.requestFocus(); // EditText에 포커스 요청
            // 키보드를 자동으로 올리려면 InputMethodManager를 사용해야 합니다.
             InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.showSoftInput(editTextOverlayInput, InputMethodManager.SHOW_IMPLICIT);
        });

        // 상태 메시지 편집 연필 아이콘 클릭 시
        imageViewEditStatus.setOnClickListener(v -> {
            frameLayoutOverlay.setVisibility(View.VISIBLE); // 오버레이 표시
            editTextOverlayInput.setText(textViewStatusMessage.getText()); // 현재 상태 메시지를 EditText에 설정
            editTextOverlayInput.setHint("상태 메시지를 입력하세요"); // 힌트 설정
            currentEditingField = EDIT_STATUS_MESSAGE; // 상태 메시지 편집 모드 설정
            editTextOverlayInput.requestFocus(); // EditText에 포커스 요청
            // 키보드를 자동으로 올리려면 InputMethodManager를 사용해야 합니다.
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextOverlayInput, InputMethodManager.SHOW_IMPLICIT);
        });

        // 오버레이 닫기 버튼 클릭 시
        imageViewOverlayClose.setOnClickListener(v -> {
            frameLayoutOverlay.setVisibility(View.GONE); // 오버레이 숨김
            currentEditingField = EDIT_NONE; // 편집 모드 초기화
            editTextOverlayInput.setText(""); // EditText 내용 초기화
            // 키보드를 숨기려면 InputMethodManager를 사용해야 합니다.
             InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(editTextOverlayInput.getWindowToken(), 0);
        });

        // 오버레이 저장 버튼 클릭 시
        imageViewOverlaySave.setOnClickListener(v -> {
            String newText = editTextOverlayInput.getText().toString(); // EditText에서 새 텍스트 가져오기

            if (currentEditingField == EDIT_NAME) {
                textViewUserName.setText(newText); // 이름 업데이트
            } else if (currentEditingField == EDIT_STATUS_MESSAGE) {
                textViewStatusMessage.setText(newText); // 상태 메시지 업데이트
            }

            frameLayoutOverlay.setVisibility(View.GONE); // 오버레이 숨김
            currentEditingField = EDIT_NONE; // 편집 모드 초기화
            editTextOverlayInput.setText(""); // EditText 내용 초기화

            // TODO: 여기서 변경된 프로필 데이터를 서버에 저장하거나 로컬 데이터베이스에 업데이트하는 로직을 추가해야 합니다.
            // 예: updateProfileOnServer(newText, currentEditingField);
            Toast.makeText(this, "변경사항이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 프로필 사진 변경 아이콘 클릭 시 (카메라 아이콘)
        imageViewCameraIcon.setOnClickListener(v -> {
            Toast.makeText(this, "프로필 사진 변경 기능 (구현 예정)", Toast.LENGTH_SHORT).show();
            // TODO: 갤러리 또는 카메라를 열어 사진을 선택하는 로직을 추가합니다.
        });

        // 배경화면 변경 버튼 클릭 시
        layoutBackgroundChange.setOnClickListener(v -> {
//            currentImageTarget = EDIT_BACKGROUND_IMAGE; // 배경 이미지 편집 모드 설정
//            showImageSourceDialog(currentImageTarget); // 이미지 소스 선택 다이얼로그 표시
            Toast.makeText(this, "배경사진 변경 기능 (구현 예정)", Toast.LENGTH_SHORT).show();
        });
    }
}