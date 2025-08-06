package com.takeiteasy.chatchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.binder.MainBinder;
import com.takeiteasy.chatchat.binder.profile.ProfileDetailBinder;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.databinding.ActivityProfileDetailBinding;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.listener.MainListener;
import com.takeiteasy.chatchat.listener.profile.ProfileDetailListener;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.ArrayList;

public class ProfileDetailActivity extends AppCompatActivity {
    private ActivityProfileDetailBinding binding;
    private MainViewModel viewModel;
    private ProfileDetailBinder binder;
    private ProfileDetailListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 뷰 바인딩 초기화
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new ProfileDetailBinder(binding);
        listener = new ProfileDetailListener(binding, viewModel);

        // 4. 컴포넌트 들을 서로 연결
        binder.bind(viewModel, this);
        listener.initListeners(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        ProfileData profileData = intent.getParcelableExtra("profileData");
        String loginUserId = profileData.getUserId();
        viewModel.loadProfile(loginUserId);
    }
}