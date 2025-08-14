package com.takeiteasy.chatchat.model.chatting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.model.chatting.ChattingMsg;
import com.takeiteasy.chatchat.model.chatting.RecieverType;
import com.takeiteasy.chatchat.model.chatting.RecieverTypeFactory;

import java.util.List;

/**
 * ChattingMessageAdapter는 채팅방 내부의 메시지 목록을 표시하는 RecyclerView의 어댑터입니다.
 * ChattingMsg 객체 리스트를 받아서 item_chat_sent_message.xml 또는 item_chat_received_message.xml에 데이터를 바인딩합니다.
 */
public class ChattingMsgAdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<ChattingMsg> dataList;
  private final String currentUserId; // 현재 로그인된 사용자의 ID
  private Context context;
  private AppCompatActivity activity;

  public ChattingMsgAdapterList(AppCompatActivity activity, Context context, List<ChattingMsg> dataList, String currentUserId) {
    this.activity = activity;
    this.context = context;
    this.dataList = dataList;
    this.currentUserId = currentUserId;
  }

  @Override
  public int getItemViewType(int position) {
    ChattingMsg message = dataList.get(position);
    String type = "me";
    if(message != null){
      if (message.getType().equals("in") || message.getType().equals("out"))
        type = "inout";
      else if (!currentUserId.equals(message.getUserId())) {
        type = "you";
      }
    }
    return RecieverTypeFactory.from(type);
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    if (viewType == RecieverType.ME.ordinal()) {
      // 내가 보낸 메시지 레이아웃 인플레이트
      View view = inflater.inflate(R.layout.item_chat_sent_message, parent, false);
      return new SentMessageViewHolder(view);
    } else if (viewType == RecieverType.YOU.ordinal()) {
      // 상대방이 보낸 메시지 레이아웃 인플레이트
      View view = inflater.inflate(R.layout.item_chat_received_message, parent, false);
      return new ReceivedMessageViewHolder(view);
    } else {
      // 상대방이 보낸 메시지 레이아웃 인플레이트
      View view = inflater.inflate(R.layout.item_chat_inout_message, parent, false);
      return new InOutMessageViewHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    ChattingMsg message = dataList.get(position);
    if (message == null) {
      return;
    }

    if (holder instanceof SentMessageViewHolder) {
      // 내가 보낸 메시지
      SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
      sentHolder.textViewSentMessage.setText(message.getMsg());
      // TODO: 메시지 시간 바인딩 (message.getTimestamp() 등)
    } else if (holder instanceof ReceivedMessageViewHolder) {
      // 상대방이 보낸 메시지
      ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
      receivedHolder.textViewReceivedMessage.setText(message.getMsg());
      // TODO: 메시지 시간 및 프로필 이미지 바인딩
      // TODO: Glide나 Picasso 같은 라이브러리를 사용하여 이미지 로드
      // 이것은 Firebase Storage에서 가져올 원격 이미지입니다.
      String imageUri = message.getProfileUrl();

      // Firebase Storage 인스턴스와 참조를 가져옵니다.
      if(imageUri != null) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageUri);

        // 다운로드 URL을 가져옵니다.
        if (!activity.isFinishing()) {
          imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
            Glide.with(context)
              .load(uri)
              .into(receivedHolder.imageViewFriendProfile);
          }).addOnFailureListener(exception -> {
            // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
            System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
            // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
          });
        }
      }
    } else {
      InOutMessageViewHolder inoutHolder = (InOutMessageViewHolder) holder;
      inoutHolder.textViewInoutMessage.setText(message.getMsg());
    }
  }

  @Override
  public int getItemCount() {
    return dataList != null ? dataList.size() : 0;
  }

  // 데이터 업데이트 메서드
  public void setDataList(List<ChattingMsg> newDataList) {
    this.dataList = newDataList;
    notifyDataSetChanged();
  }

  // 내가 보낸 메시지 ViewHolder
  public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
    TextView textViewSentMessage;

    public SentMessageViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewSentMessage = itemView.findViewById(R.id.textViewSentMessage);
    }
  }

  // 상대방이 보낸 메시지 ViewHolder
  public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
    ImageView imageViewFriendProfile;
    TextView textViewReceivedMessage;

    public ReceivedMessageViewHolder(@NonNull View itemView) {
      super(itemView);
      imageViewFriendProfile = itemView.findViewById(R.id.imageViewFriendProfile);
      textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
    }
  }

  public static class InOutMessageViewHolder extends RecyclerView.ViewHolder {
    TextView textViewInoutMessage;

    public InOutMessageViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewInoutMessage = itemView.findViewById(R.id.textViewInoutMessage);
    }
  }
}
