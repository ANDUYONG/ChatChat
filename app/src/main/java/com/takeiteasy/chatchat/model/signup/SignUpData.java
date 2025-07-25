package com.takeiteasy.chatchat.model.signup;

import android.os.Parcel;
import android.os.Parcelable;

public class SignUpData implements Parcelable {
    private String email;
    private String password; // 실제 앱에서는 원본 비밀번호를 객체로 직접 전달하지 않도록 주의하세요.
    // 보통 서버에 전송 후 즉시 지우거나, 해시된 비밀번호를 사용합니다.
    private String birthday; // YYYY-MM-DD 형식으로 저장한다고 가정
    private String phone1;  // 예: "10" (010의 10)
    private String phone2;  // 예: "1234"
    private String phone3;  // 예: "5678" (phonePart2가 2개로 나뉘어 있을 경우)

    // 1. 생성자: 모든 필드 값을 받아 객체를 초기화합니다.
    public SignUpData(String email, String password, String birthday,
                      String phone1, String phone2, String phone3) {
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
    }

    // 2. 게터(Getters): 객체에 저장된 값을 외부에서 읽을 수 있도록 합니다.
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    // ⭐ 추가된 부분: phone1, phone2, phone3의 게터 메서드
    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone3() {
        return phone3;
    }



    // 3. Parcelable 구현 (안드로이드 컴포넌트 간 데이터 전달을 위해 필수)
    // Parcelable은 데이터를 직렬화하여 Intent, Bundle 등으로 전달할 수 있게 해줍니다.

    protected SignUpData(Parcel in) {
        // 읽는 순서는 쓰는 순서와 정확히 일치해야 합니다.
        email = in.readString();
        password = in.readString();
        birthday = in.readString();
        phone1 = in.readString();
        phone2 = in.readString();
        phone3 = in.readString();
    }

    public static final Creator<SignUpData> CREATOR = new Creator<SignUpData>() {
        @Override
        public SignUpData createFromParcel(Parcel in) {
            return new SignUpData(in);
        }

        @Override
        public SignUpData[] newArray(int size) {
            return new SignUpData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // 특별한 종류의 파일 디스크립터를 포함하지 않으므로 0을 반환
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 필드들을 Parcel에 쓰는 순서. 이 순서대로 읽어야 합니다.
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(birthday);
        dest.writeString(phone1);
        dest.writeString(phone2);
        dest.writeString(phone3);
    }

    // (선택 사항) 디버깅을 위한 toString() 메서드
    @Override
    public String toString() {
        return "SignUpData{" +
                "email='" + email + '\'' +
                ", password='" + "[REDACTED]" + '\'' + // 비밀번호는 로그에 노출하지 않도록 주의
                ", birthday='" + birthday + '\'' +
                ", tel='" + phone1 + "-" + phone2 + "-" + phone3 + '\'' +
                '}';
    }
}
