package com.takeiteasy.chatchat.binder;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.SignUpActivity;
import com.takeiteasy.chatchat.databinding.ActivitySignUpBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.signup.SignUpData;
import com.takeiteasy.chatchat.viewmodel.SignUpViewModel;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignUpBinder {
    private final ActivitySignUpBinding binding;
    private final SignUpViewModel viewModel;
    private final Context context;

    public SignUpBinder(ActivitySignUpBinding binding, SignUpViewModel viewModel) {
        this.binding = binding;
        this.viewModel = viewModel;
        this.context = binding.getRoot().getContext();
    }

    public void bind(LifecycleOwner lifecycleOwner, Action action) {
        // Spinner 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.phone_prefixes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPhonePrefix.setAdapter(adapter);

        viewModel.getStatus().observe(lifecycleOwner, response -> {
            switch(response) {
                case EMAIL_ALREADY_EXISTS:
                    Toast.makeText(context, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    Toast.makeText(context, "가입 완료!", Toast.LENGTH_SHORT).show();
                    action.execute();
                    break;
                case FAILURE:
                    Toast.makeText(context, "가입 실패! 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

    }
}
