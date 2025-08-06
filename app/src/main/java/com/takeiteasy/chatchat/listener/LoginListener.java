package com.takeiteasy.chatchat.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.takeiteasy.chatchat.SignUpActivity;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;

public class LoginListener {
    private final ActivityLoginBinding binding;
    private final LoginViewModel viewModel;
    private final Context context;

    public LoginListener(ActivityLoginBinding binding, LoginViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    public void initListeners() {
        // 로그인 버튼 클릭 리스너
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 사용자 입력 값 가져오기
                String email = binding.editTextEmail.getText().toString().trim(); // 공백 제거
                String password = binding.editTextPassword.getText().toString();

                // 2. 간단한 유효성 검사 (필수 입력 필드 확인)
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 함수 종료
                }

                viewModel.login(email, password);
            }
        });

        binding.textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "회원가입 화면으로 이동", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, SignUpActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
