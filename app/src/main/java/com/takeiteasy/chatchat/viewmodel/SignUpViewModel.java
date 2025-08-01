package com.takeiteasy.chatchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.takeiteasy.chatchat.model.auth.LoginData;
import com.takeiteasy.chatchat.model.auth.LoginLoadListener;
import com.takeiteasy.chatchat.model.auth.repository.LoginRepository;
import com.takeiteasy.chatchat.model.signup.SignUpCheckListener;
import com.takeiteasy.chatchat.model.signup.SignUpCompleteListner;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.model.signup.SignUpStatus;
import com.takeiteasy.chatchat.model.signup.repository.SignUpRepostiroy;

import java.util.stream.Stream;

public class SignUpViewModel extends ViewModel  {
    private SignUpRepostiroy repository;
    private MutableLiveData<SignUpStatus> status;

    public SignUpViewModel() {
        this.repository = new SignUpRepostiroy();
        this.status = new MutableLiveData<>();
    }

    public LiveData<SignUpStatus> getStatus() {
        return status;
    }

    public void signUp(SignUpData signUpData) {
        repository.signUp(signUpData, new SignUpCompleteListner() {
            @Override
            public void onComplete(SignUpStatus signUpStatus) {
                status.setValue(signUpStatus);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}
