package com.takeiteasy.chatchat.model.profile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.takeiteasy.chatchat.R; // R.layout.item_image_detail을 사용하기 위해 필요합니다.
// 이미지 로딩 라이브러리 (예: Glide, Picasso)를 사용하려면 여기에 import 합니다.
// import com.bumptech.glide.Glide; // 예시

import java.util.List;

/**
 * ViewPager2에서 프로필 상세 이미지를 표시하기 위한 RecyclerView.Adapter 입니다.
 * 이 어댑터는 이미지 URL(String) 리스트를 받아 각 페이지에 이미지를 로드합니다.
 */
public class BackgroundUrlDataListAdaper extends RecyclerView.Adapter<BackgroundUrlDataListAdaper.ImageViewHolder> {

    private List<String> imageUrls; // 이미지 URL 목록

    /**
     * 어댑터 생성자입니다.
     * @param imageUrls 표시할 이미지 URL들의 리스트
     */
    public BackgroundUrlDataListAdaper(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * ViewHolder를 생성하고 레이아웃을 인플레이트합니다.
     * @param parent ViewHolder가 속할 ViewGroup (RecyclerView)
     * @param viewType 뷰 타입 (여러 뷰 타입이 있을 경우 사용)
     * @return 새로 생성된 ImageViewHolder
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_image_detail.xml 레이아웃을 인플레이트하여 ViewHolder에 전달합니다.
        // 이 레이아웃 파일은 단일 ImageView를 포함해야 합니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_detail, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * ViewHolder의 뷰에 데이터를 바인딩합니다.
     * @param holder 데이터를 바인딩할 ImageViewHolder
     * @param position 현재 아이템의 위치 (인덱스)
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // TODO: 여기에 이미지 로딩 라이브러리를 사용하여 이미지를 로드하는 코드를 추가합니다.
        // 예시 (Glide 사용 시):
        // Glide.with(holder.imageView.getContext())
        //      .load(imageUrl)
        //      .placeholder(R.drawable.ic_default_image_placeholder) // 로딩 중 표시될 이미지
        //      .error(R.drawable.ic_error_image) // 로드 실패 시 표시될 이미지
        //      .into(holder.imageView);

        // 현재는 단순히 이미지 뷰의 배경색을 변경하는 예시입니다. 실제 이미지 로딩 코드로 교체해야 합니다.
        // holder.imageView.setBackgroundColor(android.graphics.Color.GRAY); // 임시
        // holder.imageView.setImageResource(R.drawable.ic_default_image_placeholder); // 임시
    }

    /**
     * 어댑터가 표시할 전체 아이템의 개수를 반환합니다.
     * @return 이미지 URL 리스트의 크기
     */
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * 이미지 뷰를 포함하는 ViewHolder 클래스입니다.
     * 각 리스트 아이템의 뷰를 보유합니다.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // 이미지를 표시할 ImageView

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // 레이아웃에서 ImageView를 찾아서 연결합니다.
            // item_image_detail.xml에 ImageView가 'imageViewDetail' 이라는 ID로 존재한다고 가정합니다.
            imageView = itemView.findViewById(R.id.imageViewDetail);
        }
    }

    /**
     * 이미지 URL 리스트를 업데이트하는 메서드입니다.
     * @param newImageUrls 새로 설정할 이미지 URL 리스트
     */
    public void setImageUrls(List<String> newImageUrls) {
        this.imageUrls = newImageUrls;
        notifyDataSetChanged(); // 데이터 변경을 어댑터에 알립니다.
    }
}

