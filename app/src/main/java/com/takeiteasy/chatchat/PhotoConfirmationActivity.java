package com.takeiteasy.chatchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class PhotoConfirmationActivity extends AppCompatActivity {

    private static final int RESULT_SAVE = 3;
    private static final int RESULT_CANCLE = 4;

    private ImageView imageViewSelectedPhoto;
    private Button buttonConfirm, buttonCancel;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewSelectedPhoto = findViewById(R.id.imageViewSelectedPhoto);

        imageViewSelectedPhoto = findViewById(R.id.imageViewSelectedPhoto);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonCancel = findViewById(R.id.buttonCancel);

        // 1. 이전 액티비티(ProfileEditActivity)에서 전달받은 사진의 Uri를 가져옵니다.
        photoUri = getIntent().getData();

        if (photoUri != null) {
            // 2. Glide를 사용하여 ImageView에 사진을 로드합니다.
            Glide.with(this).load(photoUri).into(imageViewSelectedPhoto);
        } else {
            // Uri가 null인 경우, 오류를 처리하고 액티비티를 종료합니다.
            finish();
        }

        // 3. '확인' 버튼 클릭 시, 결과를 ProfileEditActivity로 돌려줍니다.
        buttonConfirm.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.setData(photoUri);
            setResult(RESULT_SAVE, resultIntent);
            finish(); // 현재 액티비티 종료
        });

        // 4. '취소' 버튼 클릭 시, 취소 결과를 돌려줍니다.
        buttonCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCLE);
            finish(); // 현재 액티비티 종료
        });
    }
}