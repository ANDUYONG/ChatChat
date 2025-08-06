package com.takeiteasy.chatchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.model.image.adapter.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileBackgroundActivity extends AppCompatActivity {

    private ImageView imageViewClose;
    private ImageView imageViewGalleryThumbnail;
    private ViewPager2 viewPagerProfileImages;
    private ImagePagerAdapter adapter;
    private List<String> imageUrls; // 이미지 경로들을 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_background);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageViewClose = findViewById(R.id.imageViewClose);
        imageViewGalleryThumbnail = findViewById(R.id.imageViewGalleryThumbnail);
        viewPagerProfileImages = findViewById(R.id.viewPagerProfileImages);

        imageViewClose = findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();

        // 1. 이미지 경로 리스트 준비 (Firebase Storage의 실제 경로)
        imageUrls = intent.getStringArrayListExtra("backgroundUrls");
        if(imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = new ArrayList<String>();
            imageUrls.add("chatchat/default/default_profile_background.png"); // 예시 이미지 1
        }

        adapter = new ImagePagerAdapter(this, imageUrls);
        viewPagerProfileImages.setAdapter(adapter);
        // (선택 사항) 페이지 변경 리스너 추가
        viewPagerProfileImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 페이지가 변경될 때마다 실행될 로직
                System.out.println("페이지 변경: " + position);
            }
        });

        this.loadThumbNail(imageViewGalleryThumbnail, imageUrls.get(0));
    }

    private void loadThumbNail(ImageView imageView, String imgUri) {
        // Firebase Storage 인스턴스 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // 이미지를 로드할 Storage 참조 생성
        // "images/my_profile_pic.jpg"는 Firebase Storage에 업로드된 이미지의 경로입니다.
        StorageReference imageRef = storage.getReference().child(imgUri);

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
}