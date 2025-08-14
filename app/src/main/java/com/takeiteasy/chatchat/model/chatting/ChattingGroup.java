package com.takeiteasy.chatchat.model.chatting;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChattingGroup implements Parcelable {
    private String uid;
    private List<String> onUsers;
    private List<ChattingMsg> chattings;

  // Firebase Firestore가 필요로 하는 매개변수 없는 기본 생성자 추가 (핵심!)
  public ChattingGroup() {
    // Firebase Firestore가 데이터를 채워 넣기 위해 필요합니다.
  }

    public ChattingGroup(String uid, List<String> onUsers, List<ChattingMsg> chattings) {
        this.uid = uid;
        this.chattings = chattings;
        this.onUsers = onUsers;
    }

    public ChattingGroup(Parcel in) {
        uid = in.readString();
        onUsers = new ArrayList<>();
        in.readStringList(onUsers);
        chattings = in.createTypedArrayList(ChattingMsg.CREATOR);
    }

    /**
     * Parcelable 객체를 생성하는 데 필요한 CREATOR 상수입니다.
     * 이 상수는 Parcel에서 데이터를 읽어 새 객체를 만들거나, Parcelable 객체 배열을 생성하는 데 사용됩니다.
     */
    public static final Creator<ChattingGroup> CREATOR = new Creator<ChattingGroup>() {
        /**
         * Parcel에서 데이터를 읽어와 새로운 ProfileData 객체를 생성합니다.
         * @param in 데이터를 포함하는 Parcel 객체
         * @return Parcel에서 생성된 ProfileData 객체
         */
        @Override
        public ChattingGroup createFromParcel(Parcel in) {
            return new ChattingGroup(in);
        }

        /**
         * 지정된 크기의 ProfileData 객체 배열을 생성합니다.
         * @param size 생성할 배열의 크기
         * @return 지정된 크기의 ProfileData 배열
         */
        @Override
        public ChattingGroup[] newArray(int size) {
            return new ChattingGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
      dest.writeString(uid);
      dest.writeStringList(onUsers);
      dest.writeParcelableArray((ChattingMsg[]) chattings.toArray(), flags);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<ChattingMsg> getChattings() {
        return chattings;
    }

    public void setChattings(List<ChattingMsg> chattings) {
        this.chattings = chattings;
    }

  public List<String> getOnUsers() {
    return onUsers;
  }

  public void setOnUsers(List<String> onUsers) {
    this.onUsers = onUsers;
  }
}
