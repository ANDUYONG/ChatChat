package com.takeiteasy.chatchat.viewmodel;

import android.os.Parcelable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.takeiteasy.chatchat.model.auth.LoginData;
import com.takeiteasy.chatchat.model.auth.LoginLoadListener;
import com.takeiteasy.chatchat.model.auth.repository.LoginRepository;
import com.takeiteasy.chatchat.model.profile.ProfileLoadListener;
import com.takeiteasy.chatchat.model.profile.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginViewModel extends ViewModel {
    private LoginRepository repository;
    private MutableLiveData<LoginData> user;

    public LoginViewModel() {
        this.repository = new LoginRepository();
        this.user = new MutableLiveData<>();
    }

    public LiveData<LoginData> getLoginUser() {
        return user;
    }

    public void login(String email, String pwd) {
        repository.fetchLogin(new LoginLoadListener() {
            @Override
            public void onLoginLoaded(Stream<LoginData> users) {
                LoginData authenticatedUser = users
                        .filter(x -> x.getEmail() != null && x.getEmail().equals(email))
                        // 2. 비밀번호 비교: 클라이언트에서 받은 비밀번호(평문)를 해싱하여 저장된 해시된 비밀번호와 비교
                        //    (주의: 실제 환경에서는 checkUser.getPwd()를 먼저 해싱한 후 x.getPwd()와 비교해야 함)
                        .filter(x -> x.getPwd() != null && x.getPwd().equals(pwd)) // 여기서는 평문 비교를 가정하지만, 실제로는 해시된 값을 비교해야 합니다.
                        .findFirst()
                        .orElse(null); // 일치하는 첫 번째 사용자를 찾습니다.
                user.setValue(authenticatedUser);
            }

            @Override
            public void onLoginLoadFailed(Exception e) {
                user.setValue(null);
            }
        });
    }
}
