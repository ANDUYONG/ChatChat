package com.takeiteasy.chatchat.model.chatting.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.ChatRoomActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.model.chatting.ChattingRoom;
import com.takeiteasy.chatchat.model.chatting.ChattingUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChattingRoomAdapterList extends RecyclerView.Adapter<ChattingRoomAdapterList.ChattingRoomViewHolder> {
    private final Context context;
    private List<ChattingRoom> dataList; // ProfileData 객체 리스트
    private ChattingRoomAdapterList.OnItemClickListener listener = null; // 새롭게 추가된 클릭 리스너 인터페이스
    private String currentUserId;

    public interface OnItemClickListener {
        void onItemClick(ChattingRoom profile, int position);
    }

    public ChattingRoomAdapterList(Context context, List<ChattingRoom> dataList, String currentUserId, ChattingRoomAdapterList.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    // 각 아이템 뷰의 UI 요소를 담는 ViewHolder (이름도 ProfileDataViewHolder로 변경)
    public static class ChattingRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView chatRoomImageView;
        TextView chatRoomNameTextView; // 닉네임을 표시할 TextView
        TextView lastMessageTextView; // 상태 메시지를 표시할 TextView
        TextView lastMessageTimeTextView; // 상태 메시지를 표시할 TextView
        TextView unreadCountTextView; // 상태 메시지를 표시할 TextView
        Button muteButton;
        Button leaveButton;
        private ChattingRoomAdapterList.OnItemClickListener listener;

        public ChattingRoomViewHolder(View itemView) {
            super(itemView);
            chatRoomImageView = itemView.findViewById(R.id.chatRoomImageView);
            chatRoomNameTextView = itemView.findViewById(R.id.chatRoomNameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            lastMessageTimeTextView = itemView.findViewById(R.id.lastMessageTimeTextView);
            unreadCountTextView = itemView.findViewById(R.id.unreadCountTextView);
            muteButton = itemView.findViewById(R.id.muteButton);
            leaveButton = itemView.findViewById(R.id.leaveButton);
        }
    }

    @NonNull
    @Override
    public ChattingRoomAdapterList.ChattingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_room, parent, false);
        return new ChattingRoomAdapterList.ChattingRoomViewHolder(view);
    }

    // ViewHolder에 데이터를 바인딩 (실제 데이터 표시)
    @Override
    public void onBindViewHolder(@NonNull ChattingRoomAdapterList.ChattingRoomViewHolder holder, int position) {
        if(dataList.size() == 0) return;

        ChattingRoom data = dataList.get(position); // Friend 대신 ProfileData 객체 사용
        if(data == null) return;

        // ProfileData 객체의 getter를 사용하여 데이터 설정
        Stream<ChattingUser> chattingUsers = data.getUsers().stream();
        if(chattingUsers != null) {
            String userNames = chattingUsers
                .filter(x -> x.isExist())
                .map(x -> x.getNickName())
                .collect(Collectors.joining(","));
            holder.chatRoomNameTextView.setText(userNames); // 닉네임 표시
            holder.lastMessageTextView.setText(data.getLstMsg()); // 상태 메시지 표시

            String lstSentDate = this.formatTimestamp(data.getLstSendDate());
            holder.lastMessageTimeTextView.setText(lstSentDate); // 상태 메시지 표시

            Stream<ChattingUser> unreadUsers = data.getUsers().stream();
            ChattingUser user = unreadUsers.filter(x -> x.getUserId().equals(currentUserId)).findAny().get();
            holder.unreadCountTextView.setText(String.valueOf(user.getUnreadCnt()));
            if(user.getUnreadCnt() == 0)
              holder.unreadCountTextView.setVisibility(View.GONE);
//        holder.lastMessageTextView.setText(data.getStatusMsg()); // 상태 메시지 표시
//        holder.lastMessageTextView.setText(data.getStatusMsg()); // 상태 메시지 표시

            // 프로필 이미지 설정 (Glide, Picasso 등 이미지 로딩 라이브러리 사용 권장)
            // 여기서는 예시로 기본 이미지를 사용하거나, URL이 있다면 로딩 라이브러리를 통해 로드합니다.
            if (data.getLstProfileUrl() != null && !data.getLstProfileUrl().isEmpty()) {
                // TODO: Glide나 Picasso 같은 라이브러리를 사용하여 이미지 로드
                // 이것은 Firebase Storage에서 가져올 원격 이미지입니다.
                String imageUri = data.getLstProfileUrl();

                // Firebase Storage 인스턴스와 참조를 가져옵니다.
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageUri);

                // 다운로드 URL을 가져옵니다.
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
                    Glide.with(context)
                            .load(uri)
                            .into(holder.chatRoomImageView);
                }).addOnFailureListener(exception -> {
                    // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
                    System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
                    // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
                });

                // 예: Glide.with(holder.ProfileDataImageView.getContext()).load(ProfileData.getProfileDataUrl()).into(holder.ProfileDataImageView);
            } else {
                holder.chatRoomImageView.setImageResource(R.drawable.ic_default_profile); // 기본 이미지
            }
        }

        // --- 끝 ---
        holder.itemView.setOnClickListener(v -> {
            Intent activity = new Intent(v.getContext(), ChatRoomActivity.class);
            activity.putExtra("userId", currentUserId);
            activity.putExtra("id", data.getUid()); // ProfileData 객체를 Intent에 담아 전달
            activity.putExtra("chattingRoom", data);
            v.getContext().startActivity(activity);
        });
    }

    // 데이터 세트의 총 아이템 수 반환
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // (선택 사항) 어댑터 데이터 업데이트 메서드
    public void setDataList(List<ChattingRoom> newDataList) { // 메서드 이름 변경
        this.dataList = newDataList;
        notifyDataSetChanged(); // 데이터가 변경되었음을 어댑터에 알림 (효율성을 위해 DiffUtil 권장)
    }

    private static String formatTimestamp(Timestamp timestamp) {
      if (timestamp == null) {
        return "";
      }
      // Timestamp를 Date 객체로 변환합니다.
      Date date = timestamp.toDate();

      // SimpleDateFormat 객체를 "yyyy년 MM월 dd일" 형식으로 초기화합니다.
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());

      // 포맷을 적용하여 문자열을 반환합니다.
      return formatter.format(date);
    }
}
