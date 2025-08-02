package com.takeiteasy.chatchat.model.auth;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LoginData implements Parcelable {
    private String userId;

    public String getUserId() {
        return userId;
    }

    private String email;
    private String pwd;
    private boolean isLoginCheck = false;

    public boolean getIsLoginCheck() {
        return isLoginCheck;
    }

    public void setIsLoginCheck(boolean isLoginCheck) {
        this.isLoginCheck = isLoginCheck;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    public LoginData() {}

    public LoginData(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }

    public LoginData(Parcel in) {
        this.userId = in.readString();
        this.email = in.readString();
        this.pwd = in.readString();
    }

    public static final Creator<LoginData> CREATOR = new Creator<LoginData>() {
        @Override
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        @Override
        public LoginData[] newArray(int size) {
            return new LoginData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(pwd);
    }

    // (선택 사항) 디버깅을 위한 toString()
    @Override
    public String toString() {
        return "LoginData{" +
                "userId='" + userId + '\'' +
                "email='" + email + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
