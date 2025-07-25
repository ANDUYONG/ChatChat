package com.takeiteasy.chatchat;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager; // Deprecated 되었지만 간단한 예시로 사용
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000; // 2초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 로그인 확인 로직
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 로그인 상태 확인
                boolean isLoggedIn = checkIfUserIsLoggedIn();
                Intent intent;
                if (isLoggedIn) {
                    // 로그인 되어 있으면 MainActivity로 이동
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // 로그인 되어 있지 않으면 LoginActivity로 이동
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

    /**
//     * 사용자의 로그인 상태를 확인하는 메서드.
//     * 실제 앱에서는 서버 통신, 로컬 DB 확인 등 복잡한 로직이 들어갈 수 있습니다.
//     * 여기서는 SharedPreferences를 사용하여 간단히 구현합니다.
//     *
 * 로그인 로직 구현 예정
     */
    private boolean checkIfUserIsLoggedIn() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//         "isLoggedIn" 키의 값을 확인. 기본값은 false.
        return preferences.getBoolean("isLoggedIn", false);
//        return true;
    }
}