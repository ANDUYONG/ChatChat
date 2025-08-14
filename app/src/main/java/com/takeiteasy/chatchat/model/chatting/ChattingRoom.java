package com.takeiteasy.chatchat.model.chatting;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;
import com.takeiteasy.chatchat.model.profile.ProfileData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChattingRoom implements Parcelable {
    private String uid;
    private List<ChattingUser> users;
    private String lstProfileUrl;
    private String lstMsg;
    private Timestamp lstSendDate;
    private Timestamp createDate;

    public ChattingRoom() {}

    public ChattingRoom(String uid, List<ChattingUser> users, String lstProfileUrl,
                        String lstMsg, Timestamp lstSendDate, Timestamp createDate) {
        this.uid = uid;
        this.users = users;
        this.lstProfileUrl = lstProfileUrl;
        this.lstMsg = lstMsg;
        this.lstSendDate = lstSendDate;
        this.createDate = createDate;
    }

    /**
     * Parcelable 객체를 생성하는 데 필요한 CREATOR 상수입니다.
     * 이 상수는 Parcel에서 데이터를 읽어 새 객체를 만들거나, Parcelable 객체 배열을 생성하는 데 사용됩니다.
     */
    public static final Creator<ChattingRoom> CREATOR = new Creator<ChattingRoom>() {
        /**
         * Parcel에서 데이터를 읽어와 새로운 ProfileData 객체를 생성합니다.
         * @param in 데이터를 포함하는 Parcel 객체
         * @return Parcel에서 생성된 ProfileData 객체
         */
        @Override
        public ChattingRoom createFromParcel(Parcel in) {
            return new ChattingRoom(in);
        }

        /**
         * 지정된 크기의 ProfileData 객체 배열을 생성합니다.
         * @param size 생성할 배열의 크기
         * @return 지정된 크기의 ProfileData 배열
         */
        @Override
        public ChattingRoom[] newArray(int size) {
            return new ChattingRoom[size];
        }
    };

    /**
     * Parcel에서 데이터를 읽어와 ProfileData 객체를 생성하는 생성자입니다.
     * writeToParcel() 메서드에서 데이터를 쓴 순서와 정확히 일치해야 합니다.
     * @param in 데이터를 포함하는 Parcel 객체
     */
    protected ChattingRoom(Parcel in) {
        uid = in.readString();
        users = in.createTypedArrayList(ChattingUser.CREATOR);
        lstProfileUrl = in.readString();
        lstMsg = in.readString();
        long lstSendDateTimestamp = in.readLong();
        if (lstSendDateTimestamp != -1) {
            // 2. 밀리초 값을 사용하여 Timestamp 객체를 새로 생성합니다.
            // Timestamp 생성자는 초 단위와 나노초 단위를 받으므로,
            // 밀리초를 초로 변환하고 나머지는 0으로 설정합니다.
            lstSendDate = new Timestamp(lstSendDateTimestamp, in.readInt());
        } else {
            lstSendDate = null;
        }
        long createDateTimestamp = in.readLong();
        if (createDateTimestamp != -1) {
            // 2. 밀리초 값을 사용하여 Timestamp 객체를 새로 생성합니다.
            // Timestamp 생성자는 초 단위와 나노초 단위를 받으므로,
            // 밀리초를 초로 변환하고 나머지는 0으로 설정합니다.
            createDate = new Timestamp(createDateTimestamp, in.readInt());
        } else {
            createDate = null;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
      // Parcel에 데이터를 쓰는 순서는 읽는 순서와 정확히 일치해야 합니다.
      dest.writeString(uid);
      dest.writeTypedList(users);
      dest.writeString(lstProfileUrl);
      dest.writeString(lstMsg);

      // Timestamp 객체가 null이 아닐 경우 초(seconds)와 나노초(nanoseconds)를 씁니다.
      if (lstSendDate != null) {
        dest.writeLong(lstSendDate.getSeconds());
        dest.writeInt(lstSendDate.getNanoseconds());
      } else {
        // null 값을 나타내기 위해 -1을 씁니다.
        dest.writeLong(-1);
        dest.writeInt(0);
      }

      if (createDate != null) {
        dest.writeLong(createDate.getSeconds());
        dest.writeInt(createDate.getNanoseconds());
      } else {
        // null 값을 나타내기 위해 -1을 씁니다.
        dest.writeLong(-1);
        dest.writeInt(0);
      }
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<ChattingUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChattingUser> users) {
        this.users = users;
    }

    public String getLstProfileUrl() {
        return lstProfileUrl;
    }

    public void setLstProfileUrl(String lstProfileUrl) {
        this.lstProfileUrl = lstProfileUrl;
    }

    public String getLstMsg() {
        return lstMsg;
    }

    public void setLstMsg(String lstMsg) {
        this.lstMsg = lstMsg;
    }

    public Timestamp getLstSendDate() {
        return lstSendDate;
    }

    public void setLstSendDate(Timestamp lstSendDate) {
        this.lstSendDate = lstSendDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    // (선택 사항) 디버깅을 위한 toString()
    @Override
    public String toString() {
        return "ChattingRoom{" +
                "uid='" + uid + '\'' +
                ", users='" + users.toString() + '\'' +
                ", lstProfileUrl='" + lstProfileUrl + '\'' +
                ", lstMsg='" + lstMsg + '\'' +
                ", lstSendDate='" + lstSendDate.toDate().toLocaleString() + '\'' +
                ", createDate='" + createDate.toDate().toLocaleString() +
                '}';
    }
}
