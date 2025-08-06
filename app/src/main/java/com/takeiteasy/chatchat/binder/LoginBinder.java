package com.takeiteasy.chatchat.binder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.MainActivity;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;

public class LoginBinder {
    private final ActivityLoginBinding binding;
    private final Context context;

    public LoginBinder(ActivityLoginBinding binding) {
        this.binding = binding;
        this.context = binding.getRoot().getContext();
    }

    public void bind(LoginViewModel viewModel, LifecycleOwner lifecycleOwner, Action action) {
        // ViewModel을 바인딩 객체에 연결
        binding.setViewModel(viewModel);
        // LifecycleOwner를 설정하여 LiveData가 뷰와 생명주기를 같이하도록 함
        binding.setLifecycleOwner(lifecycleOwner);

        viewModel.getLoginUser().observe(lifecycleOwner, response -> {
            if(response != null) {
                Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show();

                // SharedPreferences에 로그인 상태 저장
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true); // 로그인 상태를 true로 설정
                editor.putString("userId", response.getUserId());
                editor.putString("email", response.getEmail());

                editor.commit(); // 동기적으로 저장

                // MainActivity로 화면 전환
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("email", response.getEmail());
                context.startActivity(intent);

                // 현재 LoginActivity 종료 (뒤로 가기 방지)
                action.execute();
            } else {
                Toast.makeText(context, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
