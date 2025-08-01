package com.takeiteasy.chatchat.model.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FriendData implements Parcelable {
    private String userId;
    private String email;
    private boolean isBlack;
    private boolean isDeleted;


    // 1. 모든 필드 값을 받아 객체를 초기화하는 생성자입니다.
    public FriendData(String userId, String email, boolean isBlack, boolean isDeleted) {

        this.userId = userId;
        this.email = email;
        this.isBlack = isBlack;
        this.isDeleted = isDeleted;
    }

    public FriendData () {}

    public String getUserId() {
        return userId;
    }

    // 2. 게터(Getters) 메서드: 객체에 저장된 값을 외부에서 읽을 수 있도록 합니다.
    public String getEmail() {
        return email;
    }

    // --- Parcelable 구현 시작 ---

    /**
     * Parcel에서 데이터를 읽어와 ProfileData 객체를 생성하는 생성자입니다.
     * writeToParcel() 메서드에서 데이터를 쓴 순서와 정확히 일치해야 합니다.
     * @param in 데이터를 포함하는 Parcel 객체
     */
    protected FriendData(Parcel in) {
        userId = in.readString();
        email = in.readString();
        isBlack = in.readByte() != 0;
        isDeleted = in.readByte() != 0;

    }

    /**
     * Parcelable 객체를 생성하는 데 필요한 CREATOR 상수입니다.
     * 이 상수는 Parcel에서 데이터를 읽어 새 객체를 만들거나, Parcelable 객체 배열을 생성하는 데 사용됩니다.
     */
    public static final Parcelable.Creator<ProfileData> CREATOR = new Parcelable.Creator<ProfileData>() {
        /**
         * Parcel에서 데이터를 읽어와 새로운 ProfileData 객체를 생성합니다.
         * @param in 데이터를 포함하는 Parcel 객체
         * @return Parcel에서 생성된 ProfileData 객체
         */
        @Override
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        /**
         * 지정된 크기의 ProfileData 객체 배열을 생성합니다.
         * @param size 생성할 배열의 크기
         * @return 지정된 크기의 ProfileData 배열
         */
        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

    /**
     * 현재 객체에 파일 디스크립터와 같은 특별한 데이터가 포함되어 있는지 여부를 나타냅니다.
     * 대부분의 경우 0을 반환하며, 이는 특별한 콘텐츠가 없음을 의미합니다.
     * @return 0 (특별한 콘텐츠 없음)
     */
    @Override
    public int describeContents() {
        return 0; // 특별한 종류의 파일 디스크립터를 포함하지 않으므로 0을 반환
    }

    /**
     * 현재 객체의 데이터를 Parcel에 씁니다.
     * 이 메서드에 데이터를 쓰는 순서는 protected ProfileData(Parcel in) 생성자에서 데이터를 읽는 순서와 일치해야 합니다.
     * @param dest 데이터를 쓸 Parcel 객체
     * @param flags 객체가 어떻게 작성되었는지에 대한 추가 정보 플래그 (일반적으로 0)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeByte((byte) (isBlack ? 1 : 0));
        dest.writeByte((byte) (isDeleted ? 1 : 0));
    }

    // --- Parcelable 구현 끝 ---

    // (선택 사항) 디버깅을 위한 toString()
    @Override
    public String toString() {
        return "Profile{" +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", isBlack='" + isBlack + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
