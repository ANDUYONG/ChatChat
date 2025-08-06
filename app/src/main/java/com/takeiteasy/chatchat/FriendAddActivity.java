package com.takeiteasy.chatchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.takeiteasy.chatchat.binder.FriendAddBinder;
import com.takeiteasy.chatchat.binder.SignUpBinder;
import com.takeiteasy.chatchat.databinding.ActivityFriendAddBinding;
import com.takeiteasy.chatchat.databinding.ActivitySignUpBinding;
import com.takeiteasy.chatchat.helper.IntentHelper;
import com.takeiteasy.chatchat.listener.FriendAddListener;
import com.takeiteasy.chatchat.listener.SignUpListener;
import com.takeiteasy.chatchat.model.profile.FriendData;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;
import com.takeiteasy.chatchat.viewmodel.SignUpViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendAddActivity extends AppCompatActivity {
    private ActivityFriendAddBinding binding;
    private MainViewModel viewModel;
    private FriendAddBinder binder;
    private FriendAddListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 뷰 바인딩 초기화
        binding = ActivityFriendAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new FriendAddBinder(binding, viewModel);
        listener = new FriendAddListener(binding, viewModel);

        // 4. 컴포넌트 들을 서로 연결
        binder.bind(this, this::finish);
        listener.initListeners(this::finish);
    }
}