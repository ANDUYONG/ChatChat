package com.takeiteasy.chatchat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.binder.MainBinder;
import com.takeiteasy.chatchat.binder.SignUpBinder;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.databinding.ActivitySignUpBinding;
import com.takeiteasy.chatchat.listener.MainListener;
import com.takeiteasy.chatchat.listener.SignUpListener;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.viewmodel.LoginViewModel;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;
import com.takeiteasy.chatchat.viewmodel.SignUpViewModel;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;
    private SignUpBinder binder;
    private SignUpListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 뷰 바인딩 초기화
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. viewModel 초기화
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // 3. 바인더와 리스너 초기화
        binder = new SignUpBinder(binding, viewModel);
        listener = new SignUpListener(binding, viewModel);

        // 4. 컴포넌트 들을 서로 연결
        binder.bind(this, this::finish);
        listener.initListeners(this::finish);
    }
}