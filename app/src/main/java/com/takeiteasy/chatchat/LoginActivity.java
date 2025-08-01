package com.takeiteasy.chatchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.model.auth.LoginData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI 요소 참조
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        // textViewForgotPassword = findViewById(R.id.textViewForgotPassword); // 필요시
         textViewSignUp = findViewById(R.id.textViewSignUp); // 필요시

        // 3. 로그인 정보 검증 (★★★★★ 이 부분은 실제 앱에서 서버 통신으로 대체됩니다 ★★★★★)
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getLoginUser().observe(this, response -> {
            if(response != null) {
                Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                // SharedPreferences에 로그인 상태 저장
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true); // 로그인 상태를 true로 설정
                editor.apply(); // 비동기적으로 저장

                // MainActivity로 화면 전환
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("email", response.getEmail());
                startActivity(intent);

                // 현재 LoginActivity 종료 (뒤로 가기 방지)
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 로그인 버튼 클릭 리스너
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 사용자 입력 값 가져오기
                String email = editTextEmail.getText().toString().trim(); // 공백 제거
                String password = editTextPassword.getText().toString();

                // 2. 간단한 유효성 검사 (필수 입력 필드 확인)
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 함수 종료
                }

                viewModel.login(email, password);
            }
        });



        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "회원가입 화면으로 이동", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                 startActivity(intent);
            }
        });
    }
}