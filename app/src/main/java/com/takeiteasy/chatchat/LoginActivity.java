package com.takeiteasy.chatchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.binder.LoginBinder;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.listener.LoginListener;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private LoginBinder binder;
    private LoginListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 뷰 바인딩 초기화
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new LoginBinder(binding);
        listener = new LoginListener(binding, viewModel);

        // 4. 컴포넌트들을 서로 연결
        //  - 로그인 성공 후 LoginActivity 종료 (뒤로 가기 방지)
        binder.bind(viewModel, this, this::finish);
        listener.initListeners();
    }
}