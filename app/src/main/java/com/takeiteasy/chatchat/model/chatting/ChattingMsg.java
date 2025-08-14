package com.takeiteasy.chatchat.model.chatting;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ChattingMsg implements Parcelable {
    private int seq;
    private String userId;
    private String profileUrl;
    private String nickName;
    private String msg;
    private String type;
    private List<String> fileUrls;
    private List<String> unreadUsers;
    private List<String> onUsers;
    private Timestamp sendDate;

    public ChattingMsg() {}

    public ChattingMsg(int seq, String userId, String profileUrl, String nickName, String msg,
    String type, List<String> fileUrls, List<String> unreadUsers, List<String> onUsers, Timestamp sendDate) {
        this.seq = seq;
        this.userId = userId;
        this.profileUrl = profileUrl;
        this.nickName = nickName;
        this.msg = msg;
        this.type = type;
        this.fileUrls = fileUrls;
        this.unreadUsers = unreadUsers;
        this.onUsers = onUsers;
        this.nickName = nickName;
        this.sendDate = sendDate;
    }

    public ChattingMsg(Parcel in) {
        seq = in.readInt();
        userId = in.readString();
        profileUrl = in.readString();
        nickName = in.readString();
        msg = in.readString();
        type = in.readString();
        fileUrls = new ArrayList<>();
        in.readStringList(fileUrls);
        unreadUsers = new ArrayList<>();
        in.readStringList(unreadUsers);
        onUsers = new ArrayList<>();
        in.readStringList(onUsers);
        nickName = in.readString();
        long sendDateTimestamp = in.readLong();
        if (sendDateTimestamp != -1) {
            // 2. 밀리초 값을 사용하여 Timestamp 객체를 새로 생성합니다.
            // Timestamp 생성자는 초 단위와 나노초 단위를 받으므로,
            // 밀리초를 초로 변환하고 나머지는 0으로 설정합니다.
            sendDate = new Timestamp(sendDateTimestamp / 1000, 0);
        } else {
            sendDate = null;
        }
    }

    /**
     * Parcelable 객체를 생성하는 데 필요한 CREATOR 상수입니다.
     * 이 상수는 Parcel에서 데이터를 읽어 새 객체를 만들거나, Parcelable 객체 배열을 생성하는 데 사용됩니다.
     */
    public static final Creator<ChattingMsg> CREATOR = new Creator<ChattingMsg>() {
        /**
         * Parcel에서 데이터를 읽어와 새로운 ProfileData 객체를 생성합니다.
         * @param in 데이터를 포함하는 Parcel 객체
         * @return Parcel에서 생성된 ProfileData 객체
         */
        @Override
        public ChattingMsg createFromParcel(Parcel in) {
            return new ChattingMsg(in);
        }

        /**
         * 지정된 크기의 ProfileData 객체 배열을 생성합니다.
         * @param size 생성할 배열의 크기
         * @return 지정된 크기의 ProfileData 배열
         */
        @Override
        public ChattingMsg[] newArray(int size) {
            return new ChattingMsg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
      dest.writeInt(seq);
      dest.writeString(userId);
      dest.writeString(profileUrl);
      dest.writeString(nickName);
      dest.writeString(msg);
      dest.writeString(type);
      dest.writeStringList(fileUrls);
      dest.writeStringList(unreadUsers);
      dest.writeStringList(onUsers);
      dest.writeString(nickName);
      dest.writeLong(sendDate.getNanoseconds());
    }

    @Override
    public String toString() {
        return "ChattingMsg{" +
                "seq='" + seq + '\'' +
                ", userId='" + userId + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                ", fileUrls='" + fileUrls.toString() + '\'' +
                ", unreadUsers='" + unreadUsers.toString() + '\'' +
                ", onUsers='" + onUsers.toString() + '\'' +
                ", sendDate='" + sendDate.toDate().toLocaleString() +
                '}';
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public List<String> getUnreadUsers() {
        return unreadUsers;
    }

    public void setUnreadUsers(List<String> unreadUsers) {
        this.unreadUsers = unreadUsers;
    }

    public List<String> getOnUsers() {
        return onUsers;
    }

    public void setOnUsers(List<String> onUsers) {
        this.onUsers = onUsers;
    }

    public Timestamp getSendDate() {
        return sendDate;
    }

    public void setSendDate(Timestamp sendDate) {
        this.sendDate = sendDate;
    }
}
