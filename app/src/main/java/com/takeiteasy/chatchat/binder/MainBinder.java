package com.takeiteasy.chatchat.binder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.takeiteasy.chatchat.ChatListActivity;
import com.takeiteasy.chatchat.FriendAddActivity;
import com.takeiteasy.chatchat.MainActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityLoginBinding;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class MainBinder {
    private final ActivityMainBinding binding;
    private final Context context;
    private ProfileDataListAdapter profileListAdapter;

    public MainBinder(ActivityMainBinding binding) {
        this.binding = binding;
        this.context = binding.getRoot().getContext();
    }

    public void bind(MainViewModel viewModel, LifecycleOwner lifecycleOwner) {
        RecyclerView profileView = binding.profileView;
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        //        String loginUserId = preferenceManager.getString("userId", null);
        //        String loginUserId = preferenceManager.getString("userId", null);

        // ViewModel을 바인딩 객체에 연결
        binding.setViewModel(viewModel);
        // LifecycleOwner를 설정하여 LiveData가 뷰와 생명주기를 같이하도록 함
        binding.setLifecycleOwner(lifecycleOwner);

        // Firebase 앱 초기화
        // 이 코드를 추가하면, google-services.json 파일의 설정을 바탕으로 Firebase가 초기화됩니다.
        FirebaseApp.initializeApp(context);

        // 2. LayoutManager 설정 (수직 스크롤 목록)
        profileView.setLayoutManager(new LinearLayoutManager(context));

        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
        viewModel.getProfiles().observe(lifecycleOwner, profileList -> {
            Toast.makeText(context, "목록 조회!", Toast.LENGTH_SHORT).show();

            // 또는 기존 어댑터의 데이터를 업데이트:
            if (profileListAdapter != null) {
                profileListAdapter.setProfileDatas(profileList);
            } else {
                profileListAdapter = new ProfileDataListAdapter(context, profileList);
                profileView.setAdapter(profileListAdapter);
            }
        });
    }
}
