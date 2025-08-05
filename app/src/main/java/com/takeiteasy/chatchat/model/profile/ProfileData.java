package com.takeiteasy.chatchat.model.profile;

import android.os.Parcel;
import android.os.Parcelable; // Parcelable 인터페이스를 사용하기 위해 import 합니다.
import java.util.ArrayList; // List를 읽을 때 필요할 수 있습니다.
import java.util.List;

// Parcelable 인터페이스를 상속받도록 클래스 선언을 수정합니다.
public class ProfileData implements Parcelable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    private String email;
    private String tel1; // 전화번호 (SignUpData의 phone1-phone2-phone3이 합쳐진 형태일 수 있음)
    private String tel2;
    private String tel3;

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    private String name; // 본명
    private String birth;
    private String profileUrl; // 프로필 사진 URL
    private List<String> backgroundUrls; // 배경사진 URL 목록
    private String nickName; // 화면상에 표시될 이름 (사용자 닉네임)
    private String statusMsg; // 화면상에 표시될 상태 메시지
    public boolean isSelected = false; // 이 필드를 사용하여 배경색을 변경할 겁니다.

    private List<FriendData> friends;

    public List<FriendData> getFriends() {
        return friends;
    }

    // 1. 모든 필드 값을 받아 객체를 초기화하는 생성자입니다.
    public ProfileData(String userId, String email, String tel1, String tel2, String tel3, String name, String birth,
                       String profileUrl, List<String> backgroundUrls,
                       String nickName, String statusMsg, List<FriendData> friends) {

        this.userId = userId;
        this.email = email;
        this.tel1 = tel1;
        this.tel2 = tel2;
        this.tel3 = tel3;
        this.name = name;
        this.birth = birth;
        this.profileUrl = profileUrl;
        this.backgroundUrls = backgroundUrls;
        this.nickName = nickName;
        this.statusMsg = statusMsg;
        this.friends = friends;
    }

    public ProfileData () {}

    // 2. 게터(Getters) 메서드: 객체에 저장된 값을 외부에서 읽을 수 있도록 합니다.
    public String getEmail() {
        return email;
    }


    public String getTel1() {
        return tel1;
    }

    public String getTel2() {
        return tel2;
    }

    public String getTel3() {
        return tel3;
    }


    public String getName() {
        return name;
    }

    public String getBirth() {
        return birth;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public List<String> getBackgroundUrls() {
        return backgroundUrls;
    }

    public String getNickName() {
        return nickName;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // --- Parcelable 구현 시작 ---

    /**
     * Parcel에서 데이터를 읽어와 ProfileData 객체를 생성하는 생성자입니다.
     * writeToParcel() 메서드에서 데이터를 쓴 순서와 정확히 일치해야 합니다.
     * @param in 데이터를 포함하는 Parcel 객체
     */
    protected ProfileData(Parcel in) {
        userId = in.readString();
        email = in.readString();
        tel1 = in.readString();
        tel2 = in.readString();
        tel3 = in.readString();
        name = in.readString();
        birth = in.readString();
        profileUrl = in.readString();
        // List<String>을 읽을 때는 readStringList()를 사용합니다.
        // 이 메서드는 내부적으로 새로운 ArrayList를 생성하여 데이터를 채웁니다.
        backgroundUrls = new ArrayList<>(); // 먼저 빈 리스트를 초기화합니다.
        in.readStringList(backgroundUrls); // Parcel에서 읽어온 문자열들을 리스트에 추가합니다.
        nickName = in.readString();
        statusMsg = in.readString();
        friends = new ArrayList<>();
    }

    /**
     * Parcelable 객체를 생성하는 데 필요한 CREATOR 상수입니다.
     * 이 상수는 Parcel에서 데이터를 읽어 새 객체를 만들거나, Parcelable 객체 배열을 생성하는 데 사용됩니다.
     */
    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
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
        dest.writeString(tel1);
        dest.writeString(tel2);
        dest.writeString(tel3);
        dest.writeString(name);
        dest.writeString(birth);
        dest.writeString(profileUrl);
        // List<String>을 Parcel에 쓸 때는 writeStringList()를 사용합니다.
        dest.writeStringList(backgroundUrls);
        dest.writeString(nickName);
        dest.writeString(statusMsg);
        dest.writeList(friends);
    }

    // --- Parcelable 구현 끝 ---

    // (선택 사항) 디버깅을 위한 toString()
    @Override
    public String toString() {
        return "Profile{" +
                "nickName='" + nickName + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", tel1='" + tel1 + '\'' +
                ", tel2='" + tel2 + '\'' +
                ", tel3='" + tel3 + '\'' +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", backgroundUrls=" + backgroundUrls + '\'' +
                ", friends=" + friends +
                '}';
    }
}
