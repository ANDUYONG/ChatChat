package com.takeiteasy.chatchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.takeiteasy.chatchat.binder.LoginBinder;
import com.takeiteasy.chatchat.binder.MainBinder;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.listener.LoginListener;
import com.takeiteasy.chatchat.listener.MainListener;
import com.takeiteasy.chatchat.model.auth.repository.MessageService;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private MainBinder binder;
    private MainListener listener;
    private boolean isFirstLaunch = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String loginUserId = preferenceManager.getString("userId", null);
        MessageService messaging = new MessageService(loginUserId);

        // 1. 뷰 바인딩 초기화
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new MainBinder(binding);
        listener = new MainListener(binding, viewModel);

        // 4. 컴포넌트 들을 서로 연결
        binder.bind(viewModel, this);
        listener.initListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        SharedPreferences.Editor editor = preferenceManager.edit();
//        editor.clear(); // 모든 키-값 쌍 삭제
//        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        viewModel.loadProfiles(preferenceManager.getString("userId", null));
    }

    // 화면 회전 시 isFirstLaunch가 재설정되는 것을 방지하려면
    // onSaveInstanceState와 onRestoreInstanceState를 사용할 수 있습니다.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFirstLaunch", isFirstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFirstLaunch = savedInstanceState.getBoolean("isFirstLaunch");
    }
}
