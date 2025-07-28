package com.takeiteasy.chatchat.model.profile.adapter;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.takeiteasy.chatchat.ProfileDetailActivity;
import com.takeiteasy.chatchat.R; // R.layout.item_friend를 사용하기 위해 임포트
import com.takeiteasy.chatchat.model.profile.ProfileData; // Profile 데이터 클래스 임포트

// 어댑터 이름도 ProfileDataListAdapter로 변경
public class ProfileDataListAdapter extends RecyclerView.Adapter<ProfileDataListAdapter.ProfileDataViewHolder> {

    private List<Parcelable> ProfileDataList; // ProfileData 객체 리스트

    public ProfileDataListAdapter(List<Parcelable> ProfileDataList) {
        this.ProfileDataList = ProfileDataList;
    }

    // 각 아이템 뷰의 UI 요소를 담는 ViewHolder (이름도 ProfileDataViewHolder로 변경)
    public static class ProfileDataViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView; // 닉네임을 표시할 TextView
        TextView statusMessageTextView; // 상태 메시지를 표시할 TextView

        public ProfileDataViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusMessageTextView = itemView.findViewById(R.id.statusMessageTextView);
        }
    }

    // ViewHolder 객체를 생성하고 XML 레이아웃을 인플레이트 (초기화)
    @NonNull
    @Override
    public ProfileDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ProfileDataViewHolder(view);
    }

    // ViewHolder에 데이터를 바인딩 (실제 데이터 표시)
    @Override
    public void onBindViewHolder(@NonNull ProfileDataViewHolder holder, int position) {
        ProfileData ProfileData = (ProfileData) ProfileDataList.get(position); // Friend 대신 ProfileData 객체 사용

        // ProfileData 객체의 getter를 사용하여 데이터 설정
        holder.nameTextView.setText(ProfileData.getNickName()); // 닉네임 표시
        holder.statusMessageTextView.setText(ProfileData.getStatusMsg()); // 상태 메시지 표시

        // 프로필 이미지 설정 (Glide, Picasso 등 이미지 로딩 라이브러리 사용 권장)
        // 여기서는 예시로 기본 이미지를 사용하거나, URL이 있다면 로딩 라이브러리를 통해 로드합니다.
        if (ProfileData.getProfileUrl() != null && !ProfileData.getProfileUrl().isEmpty()) {
            // TODO: Glide나 Picasso 같은 라이브러리를 사용하여 이미지 로드

            // 예: Glide.with(holder.ProfileDataImageView.getContext()).load(ProfileData.getProfileDataUrl()).into(holder.ProfileDataImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_default_profile); // 기본 이미지
        }

        // 아이템 클릭 리스너 (선택 사항)
        holder.itemView.setOnClickListener(v -> {
            // TODO: 친구 항목 클릭 시 동작 정의 (예: 친구 프로필 화면으로 이동)
            // 현재 클릭된 친구의 ProfileData 객체를 다음 화면으로 전달
            Intent profileDetailActivity = new Intent(v.getContext(), ProfileDetailActivity.class); // ProfileDetailActivity는 실제 파일명으로 변경
            profileDetailActivity.putExtra("profileData", ProfileData); // ProfileData 객체를 Intent에 담아 전달
            v.getContext().startActivity(profileDetailActivity);
//            Toast.makeText(v.getContext(), ProfileData.getNickName() + " 클릭됨!", Toast.LENGTH_SHORT).show();
            // 여기서는 ProfileData 객체의 다른 정보들도 사용 가능합니다:
            // Log.d("ProfileDataClick", "Email: " + ProfileData.getEmail() + ", Tel: " + ProfileData.getTel());
        });
    }

    // 데이터 세트의 총 아이템 수 반환
    @Override
    public int getItemCount() {
        return ProfileDataList.size();
    }

    // (선택 사항) 어댑터 데이터 업데이트 메서드
    public void setProfileDatas(List<ProfileData> newProfileDataList) { // 메서드 이름 변경
        List<Parcelable> list = new ArrayList<>();
        for(ProfileData data : newProfileDataList) {
            list.add(data);
        }
        this.ProfileDataList = list;
        notifyDataSetChanged(); // 데이터가 변경되었음을 어댑터에 알림 (효율성을 위해 DiffUtil 권장)
    }
}