package com.takeiteasy.chatchat.model.chatting;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class ChattingUser implements Parcelable {
  private int seq;
  private String userId;
  private String profileUrl;
  private String nickName;
  private long unreadCnt;
  private boolean isExist;
  private boolean isPush;
  private Timestamp addedDate;
  private Timestamp deletedDate;

  public ChattingUser() {};

  public ChattingUser(int seq, String userId, String profileUrl, String nickName,
                      long unreadCnt, boolean isExist, boolean isPush, Timestamp addedDate, Timestamp deletedDate) {
    this.seq = seq;
    this.userId = userId;
    this.profileUrl = profileUrl;
    this.nickName = nickName;
    this.unreadCnt = unreadCnt;
    this.isExist = isExist;
    this.isPush = isPush;
    this.addedDate = addedDate;
    this.deletedDate = deletedDate;
  }

  protected ChattingUser(Parcel in) {
    seq = in.readInt();
    userId = in.readString();
    profileUrl = in.readString();
    nickName = in.readString();
    unreadCnt = in.readLong();
    isExist = in.readByte() != 0;
    isPush = in.readByte() != 0;

    // addedDate (Timestamp) 읽기
    long addedDateSeconds = in.readLong();
    int addedDateNanos = in.readInt();
    if (addedDateSeconds != -1) {
      addedDate = new Timestamp(addedDateSeconds, addedDateNanos);
    } else {
      addedDate = null;
    }

    // deletedDate (Timestamp) 읽기
    long deletedDateSeconds = in.readLong();
    int deletedDateNanos = in.readInt();
    if (deletedDateSeconds != -1) {
      deletedDate = new Timestamp(deletedDateSeconds, deletedDateNanos);
    } else {
      deletedDate = null;
    }
  }

  public static final Creator<ChattingUser> CREATOR = new Creator<ChattingUser>() {
    @Override
    public ChattingUser createFromParcel(Parcel in) {
      return new ChattingUser(in);
    }

    @Override
    public ChattingUser[] newArray(int size) {
      return new ChattingUser[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    // Parcel에 쓰는 순서가 Parcel에서 읽는 순서와 정확히 일치해야 합니다.
    dest.writeInt(seq);
    dest.writeString(userId);
    dest.writeString(profileUrl);
    dest.writeString(nickName);
    dest.writeLong(unreadCnt);
    dest.writeByte((byte) (isExist ? 1 : 0));
    dest.writeByte((byte) (isPush ? 1 : 0));

    // addedDate (Timestamp) 쓰기
    if (addedDate != null) {
      dest.writeLong(addedDate.getSeconds());
      dest.writeInt(addedDate.getNanoseconds());
    } else {
      dest.writeLong(-1);
      dest.writeInt(0);
    }

    // deletedDate (Timestamp) 쓰기
    if (deletedDate != null) {
      dest.writeLong(deletedDate.getSeconds());
      dest.writeInt(deletedDate.getNanoseconds());
    } else {
      dest.writeLong(-1);
      dest.writeInt(0);
    }
  }

  // Getters and setters... (수정 없음)
  public int getSeq() { return seq; }
  public void setSeq(int seq) { this.seq = seq; }
  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
  public String getProfileUrl() { return profileUrl; }
  public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
  public String getNickName() { return nickName; }
  public void setNickName(String nickName) { this.nickName = nickName; }
  public long getUnreadCnt() { return unreadCnt; }
  public void setUnreadCnt(long unreadCnt) { this.unreadCnt = unreadCnt; }
  public boolean isExist() { return isExist; }
  public void setExist(boolean exist) { isExist = exist; }
  public boolean isPush() { return isPush; }
  public void setPush(boolean push) { isPush = push; }
  public Timestamp getAddedDate() { return addedDate; }
  public void setAddedDate(Timestamp addedDate) { this.addedDate = addedDate; }
  public Timestamp getDeletedDate() { return deletedDate; }
  public void setDeletedDate(Timestamp deletedDate) { this.deletedDate = deletedDate; }
}
