package com.takeiteasy.chatchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {

    // REQUEST_IMAGE_CAPTURE는 카메라 실행을 식별하기 위한 요청 코드입니다.
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2; // 갤러리를 위한 다른 요청 코드

    private static final int RESULT_SAVE = 3;
    private static final int BACKGROUND_SAVE = 4;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_PHOTO_CONFIRMATION = 101; // PhotoConfirmationActivity를 위한 요청 코드

    private MainViewModel viewModel;

    // ProfileEditActivity 클래스 멤버 변수로 선언
    private Uri cameraImageUri;

    private SharedPreferences preferenceManager;// = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    private String loginUserId; // = preferenceManager.getString("userId", null);

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
    private static final int SAVE_TYPE_PROFILE = 1;
    private static final int SAVE_TYPE_BACKGROUND = 2;
    int currentType = 0;
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

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(ProfileEditActivity.this);
        loginUserId = preferenceManager.getString("userId", null);

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
        // 친구 목록 받아오기
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfile().observe(this, profile -> this.setProfile(profile));

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

            String key = null, val = newText;;
            if (currentEditingField == EDIT_NAME) {
                textViewUserName.setText(newText); // 이름 업데이트
                key = "nickName";
            } else if (currentEditingField == EDIT_STATUS_MESSAGE) {
                textViewStatusMessage.setText(newText); // 상태 메시지 업데이트
                key = "statusMsg";
            }

            Map<String, Object> updates = new HashMap();
            updates.put(key, val);

            viewModel.setProfile(loginUserId, updates);
        });

        // 프로필 사진 변경 아이콘 클릭 시 (카메라 아이콘)
        imageViewCameraIcon.setOnClickListener(v -> {
            String[] options = {"카메라", "갤러리"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("프로필 사진 변경");

            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // "카메라" 선택 시, 권한을 확인하고 카메라를 엽니다.
                    checkPermissionsAndOpenCamera();
                } else if (which == 1) {
                    // "갤러리" 선택 시, 갤러리 앱을 엽니다.
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhotoIntent.setType("image/*");
                    currentType = SAVE_TYPE_PROFILE;
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // 배경화면 변경 버튼 클릭 시
        layoutBackgroundChange.setOnClickListener(v -> {
            String[] options = {"카메라", "갤러리"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("배경화면 바꾸기");

            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // "카메라" 선택 시, 권한을 확인하고 카메라를 엽니다.
                    checkPermissionsAndOpenCamera();
                } else if (which == 1) {
                    // "갤러리" 선택 시, 갤러리 앱을 엽니다.
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhotoIntent.setType("image/*");
                    currentType = SAVE_TYPE_BACKGROUND;
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // <--- data != null 조건을 여기서 제거합니다.
            Uri selectedImageUri = null;

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // 카메라로 찍은 사진은 data가 null일 수 있으므로,
                // 미리 지정해둔 cameraImageUri를 사용합니다.
                selectedImageUri = cameraImageUri;
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // 갤러리에서 선택한 사진은 data에 Uri가 담겨 있습니다.
                selectedImageUri = data.getData();
            }

            // PhotoConfirmationActivity로 사진 URI를 넘겨줍니다.
            if (selectedImageUri != null) {
                Intent intent = new Intent(this, PhotoConfirmationActivity.class);
                intent.setData(selectedImageUri);

                startActivityForResult(intent, RESULT_SAVE);
            }
        } else if (resultCode == RESULT_SAVE && data != null) { // <--- RESULT_SAVE도 data != null을 확인해야 합니다.
            this.saveImage(resultCode, data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되면 카메라를 엽니다.
                openCamera();
            } else {
                // 권한이 거부되면 사용자에게 메시지를 보여줍니다.
                Toast.makeText(this, "카메라 및 저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setProfile(ProfileData profile) {
        if (profile != null) {
            // 사용자 이름 바인딩
            textViewUserName.setText(profile.getNickName() != null ? profile.getNickName() : "");
            // 사용자 상태 메시지 바인딩
            textViewStatusMessage.setText(profile.getStatusMsg() != null ? profile.getStatusMsg() : "");

            // 프로필 이미지 URL 바인딩 (Glide 사용)
            String profileImageUrl = profile.getProfileUrl();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                // Firebase Storage 인스턴스와 참조를 가져옵니다.
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(profileImageUrl);
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
                    Glide.with(this)
                            .load(uri)
                            .into(imageViewProfilePicture);
                }).addOnFailureListener(exception -> {
                    // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
                    System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
                    // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
                });
            } else {
                imageViewProfilePicture.setImageResource(R.drawable.ic_default_profile_filled);
            }

            // 배경화면 URL 바인딩 (Glide 사용)
            List<String> backgroundUrls = profile.getBackgroundUrls();
            String backgroundUrl = null;
            if (backgroundUrls != null && !backgroundUrls.isEmpty()) {
                backgroundUrl = backgroundUrls.get(backgroundUrls.size()-1); // 첫 번째 배경 이미지를 사용
                // Firebase Storage 인스턴스와 참조를 가져옵니다.
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(backgroundUrl);

                // 다운로드 URL을 가져옵니다.
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
                    Glide.with(this)
                            .load(uri)
                            .into(profileViewBackground);
                }).addOnFailureListener(exception -> {
                    // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
                    System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
                    // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
                });
            } else {
                profileViewBackground.setImageResource(R.drawable.default_profile_background);
            }

            frameLayoutOverlay.setVisibility(View.GONE); // 오버레이 숨김
            currentEditingField = EDIT_NONE; // 편집 모드 초기화
            editTextOverlayInput.setText(""); // EditText 내용 초기화
        } else {
            Toast.makeText(this, "프로필 데이터를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
            // 데이터가 없으면 기본값 설정 또는 액티비티 종료
            textViewUserName.setText("이름 없음");
            textViewStatusMessage.setText("상태 메시지 없음");
            imageViewProfilePicture.setImageResource(R.drawable.ic_default_profile_filled);
            profileViewBackground.setImageResource(R.drawable.default_profile_background);
        }
    }

    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 권한이 없으면 요청합니다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // 권한이 이미 있다면 카메라를 엽니다.
            openCamera();
        }
    }

    private void saveImage(int resultCode, Uri selectedImageUri) {
        // PhotoConfirmationActivity로 사진 URI를 넘겨주는 로직 대신,
        // 바로 Firebase Storage에 업로드하는 로직을 추가합니다.
        if (selectedImageUri != null) {
            // 1. Firebase Storage 인스턴스 가져오기
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // 2. 파일 이름 생성
            // 이미지의 확장자를 가져와서 파일 이름에 추가합니다.
            String fileExtension = getContentResolver().getType(selectedImageUri);
            String fileName = UUID.randomUUID().toString();
            String fullFileName = fileName + "." + fileExtension.substring(fileExtension.lastIndexOf("/") + 1);
            String folderName = currentType == SAVE_TYPE_PROFILE ? "chatchat/profiles/" : "chatchat/background/";
            String fullPath = folderName + loginUserId + "/" + fullFileName;
            // 3. 업로드 경로 지정: "chatchat/profiiles/{userId}/{fileName}" 형식으로 참조 생성
            // {userId} 부분은 현재 로그인된 사용자의 ID로 대체해야 합니다.
            StorageReference storageRef = storage.getReference()
                    .child(fullPath);

            // 4. db에 경로 저장
            if (loginUserId == null) {
                // userId가 없을 경우 오류 처리
                Toast.makeText(this, "로그인된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore 인스턴스 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // "users" 컬렉션에서 특정 userId를 가진 문서 참조
            DocumentReference userRef = db.collection("users").document(loginUserId);

            // 업데이트할 필드와 값을 Map으로 준비
            Map<String, Object> updates = new HashMap<>();
            if(currentType == SAVE_TYPE_PROFILE) {
                updates.put("profileUrl", fullPath);
            } else {
                ProfileData profile = viewModel.getProfile().getValue();
                List<String> backgroundUrls = profile.getBackgroundUrls();
                if(backgroundUrls == null) backgroundUrls = new ArrayList<>();

                backgroundUrls.add(fullPath);
                if(backgroundUrls != null && backgroundUrls.size() > 0)
                    updates.put("backgroundUrls", backgroundUrls);
            }

            // update() 메서드를 사용하여 필드 덮어쓰기
            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // 성공적으로 업데이트되었을 때의 로직
                        // 4. 업로드 시작
                        storageRef.putFile(selectedImageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // 5. 업로드 성공 시
                                    // 업로드된 파일의 다운로드 URL 가져오기
                                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        // 이제 이 uri를 사용하여 프로필 사진을 변경하거나,
                                        // Realtime Database 또는 Firestore에 저장하는 등의 후속 처리를 합니다.
                                        ImageView resultView = currentType == SAVE_TYPE_PROFILE ? imageViewProfilePicture : profileViewBackground;
                                        Glide.with(this).load(uri).into(resultView);


                                        String path = uri.getPath();
                                        Toast.makeText(this, "프로필 사진이 성공적으로 업로드되었습니다!", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(exception -> {
                                    // 6. 업로드 실패 시
                                    Toast.makeText(this, "사진 업로드 실패: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    // 오류 처리를 여기에 추가합니다.
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "사진 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * 카메라 앱을 열고 사진을 촬영할 준비를 하는 메서드.
     * 이 메서드는 권한 확인이 완료된 후 호출되어야 합니다.
     */
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 기기에 카메라 앱이 있는지 확인
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 2. 사진을 저장할 임시 파일 Uri를 생성합니다.
            cameraImageUri = createImageUri();

            // Uri가 정상적으로 생성되었는지 확인
            if (cameraImageUri != null) {
                // 3. 카메라 앱에 이미지를 저장할 위치를 알려줍니다.
                // 이 Uri는 onActivityResult에서 결과를 받을 때 사용됩니다.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

                // 4. 카메라 앱을 실행하고 결과를 기다립니다.
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "이미지 파일을 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 클래스 내부에 임시 파일 Uri를 생성하는 메서드 추가
    private Uri createImageUri() {
        File imageFile = new File(getExternalCacheDir(), "temp_camera_image.jpg");
        try {
            String packageName = getPackageName();
            return FileProvider.getUriForFile(this, packageName + ".fileprovider", imageFile);
        } catch (IllegalArgumentException e) {
            Log.e("FileProvider", "The selected file can't be shared: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(ProfileEditActivity.this);
        loginUserId = preferenceManager.getString("userId", null);
        viewModel.loadProfile(loginUserId);
    }
}