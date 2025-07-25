package com.takeiteasy.chatchat.model.profile;

import java.util.List;

public class ProfileData {
    private String email;
    private String tel; // 전화번호 (SignUpData의 phone1-phone2-phone3이 합쳐진 형태일 수 있음)
    private String name; // 본명
    private String birth;
    private String profileUrl; // 프로필 사진 URL
    private List<String> backgroundUrls; // 배경사진 URL 목록
    private String nickName; // 화면상에 표시될 이름 (사용자 닉네임)
    private String statusMsg; // 화면상에 표시될 상태 메시지

    public ProfileData(String email, String tel, String name, String birth,
                   String profileUrl, List<String> backgroundUrls,
                   String nickName, String statusMsg) {
        this.email = email;
        this.tel = tel;
        this.name = name;
        this.birth = birth;
        this.profileUrl = profileUrl;
        this.backgroundUrls = backgroundUrls;
        this.nickName = nickName;
        this.statusMsg = statusMsg;
    }

    // 게터(Getters) 메서드
    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
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

    // (선택 사항) 디버깅을 위한 toString()
    @Override
    public String toString() {
        return "Profile{" +
                "nickName='" + nickName + '\'' +
                ", statusMsg='" + statusMsg + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", backgroundUrls=" + backgroundUrls +
                '}';
    }
}
