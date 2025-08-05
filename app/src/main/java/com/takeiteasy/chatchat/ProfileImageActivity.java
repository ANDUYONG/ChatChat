package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileImageActivity extends AppCompatActivity {
    private FirebaseStorage storage;

    private ImageView imageViewClose;
    private ImageView imageViewGalleryThumbnail;
    private ImageView imageViewProfileDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewClose = findViewById(R.id.imageViewClose);
        imageViewGalleryThumbnail = findViewById(R.id.imageViewGalleryThumbnail);
        imageViewProfileDetail = findViewById(R.id.imageViewProfileDetail);

        this.loadImage(imageViewGalleryThumbnail);
        this.loadImage(imageViewProfileDetail);

        imageViewClose = findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadImage(ImageView imageView) {
        // Firebase Storage 인스턴스 가져오기
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        String ImageUri = intent.getStringExtra("profileUrl");
        if(ImageUri != null) {
            StorageReference imageRef = storage.getReference().child(ImageUri);

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
        } else {
            imageView.setImageResource(R.drawable.ic_default_profile_filled);
        }
    }
}