package com.takeiteasy.chatchat.model.image.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.ProfileBackgroundActivity;
import com.takeiteasy.chatchat.R;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imageUrls; // Firebase Storage 이미지 경로 (예: "chatchat/default/default_profile.png")
    private final FirebaseStorage storage;

    public ImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.storage = FirebaseStorage.getInstance(); // Firebase Storage 인스턴스 초기화
    }

    @NonNull
    @Override
    public ImagePagerAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_image_page.xml (또는 ViewPager에 사용할 이미지 레이아웃 파일)을 인플레이트합니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_page, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePagerAdapter.ImageViewHolder holder, int position) {
        String imagePath = imageUrls.get(position);

        // Firebase Storage 참조 생성
        StorageReference imageRef = storage.getReference().child(imagePath);

        // ✨ Glide 로드 성공/실패 확인을 위한 리스너 추가 (선택 사항)
        // 다운로드 URL 가져오기
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // URL을 성공적으로 가져온 경우
            // Glide를 사용하여 이미지를 ImageView에 로드
            Glide.with(context)
                    .load(uri) // ✨ HTTPS URL (Uri 객체)을 전달
                    .into(holder.imageView);
        }).addOnFailureListener(exception -> {
            // URL 가져오기 실패 (예: 파일이 없거나, 권한 문제)
            System.err.println("이미지 다운로드 URL 가져오기 실패: " + exception.getMessage());
            // 오류 이미지를 표시하거나 사용자에게 알림
        });
//        Glide.with(context)
//                .load(imageRef)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        Log.e("Glide", "Image load failed for: " + imagePath, e);
//                        return false; // 오류 처리하지 않고 기본 동작 따름
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        Log.d("Glide", "Image loaded successfully for: " + imagePath);
//                        return false; // 기본 동작 따름
//                    }
//                })
//                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    // ViewHolder 클래스 정의
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.backgroundViewPagerItem); // 레이아웃 파일의 ImageView ID
        }
    }
}
