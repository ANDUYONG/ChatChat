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
import com.takeiteasy.chatchat.binder.MainBinder;
import com.takeiteasy.chatchat.binder.profile.ProfileEditBinder;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.databinding.ActivityProfileEditBinding;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.listener.MainListener;
import com.takeiteasy.chatchat.listener.profile.ProfileEditListener;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private MainViewModel viewModel;
    private ProfileEditBinder binder;
    private ProfileEditListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 뷰 바인딩 초기화
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new ProfileEditBinder(binding);
        listener = new ProfileEditListener(binding, viewModel, this);

        // 4. 컴포넌트 들을 서로 연결
        listener.initListeners();
        binder.bind(viewModel, this, listener::setProfile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // <--- data != null 조건을 여기서 제거합니다.
            listener.goConfirmation(requestCode, data);
        } else if (resultCode == ProfileEditListener.RESULT_SAVE && data != null) { // <--- RESULT_SAVE도 data != null을 확인해야 합니다.
            listener.saveImage(resultCode, data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ProfileEditListener.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되면 카메라를 엽니다.
                listener.openCamera();
            } else {
                // 권한이 거부되면 사용자에게 메시지를 보여줍니다.
                Toast.makeText(this, "카메라 및 저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        ProfileData profileData = intent.getParcelableExtra("profileData");
        String loginUserId = profileData.getUserId();
        viewModel.loadProfile(loginUserId);
    }
}