package com.takeiteasy.chatchat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.takeiteasy.chatchat.model.signup.SignUpData;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewPasswordError;
    private EditText editTextConfirmPassword;
    private TextView textViewPasswordMatch;
    private EditText editTextBirthday;
    private int year, month, day;
    private Spinner spinnerPhonePrefix; // Button 대신 Spinner로 변경
    private EditText editTextPhone1;
    private EditText editTextPhone2;
    private Button buttonSignUp;
    private ImageView imageViewBack;
    // 비밀번호 유효성 검사 (예시: 최소 8자, 숫자, 문자 포함)
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- 중요: DatePickerDialog의 초기 날짜를 현재 날짜로 설정 ---
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH); // Calendar.MONTH는 0부터 시작 (0 = 1월)
        day = c.get(Calendar.DAY_OF_MONTH);
        // ------------------------------------------------------------------


        // UI 요소 초기화
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewPasswordError = findViewById(R.id.textViewPasswordError);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        textViewPasswordMatch = findViewById(R.id.textViewPasswordMatch);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        spinnerPhonePrefix = findViewById(R.id.spinnerPhonePrefix); // Spinner로 변경
        editTextPhone1 = findViewById(R.id.editTextPhone1);
        editTextPhone2 = findViewById(R.id.editTextPhone2);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        
        // 뒤로 가기 버튼 클릭 리스너
        imageViewBack.setOnClickListener(v -> finish());

        // 생년월일 EditText 클릭 시 DatePickerDialog 표시
        editTextBirthday.setOnClickListener(v -> showDatePickerDialog());

        // 비밀번호 유효성 검사
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!PASSWORD_PATTERN.matcher(s.toString()).matches()) {
                    textViewPasswordError.setVisibility(View.VISIBLE);
                } else {
                    textViewPasswordError.setVisibility(View.GONE);
                }
                checkPasswordsMatch();
            }
        });

        // 비밀번호 확인 일치 여부 검사
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkPasswordsMatch();
            }
        });

        // Spinner 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phone_prefixes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhonePrefix.setAdapter(adapter);

        // Spinner 항목 선택 리스너 (선택된 값 가져오기)
        spinnerPhonePrefix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPrefix = parent.getItemAtPosition(position).toString();
                // 선택된 접두사를 사용하여 추가 로직 구현 (예: Toast 메시지)
                // Toast.makeText(SignUpActivity.this, "선택된 접두사: " + selectedPrefix, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때
            }
        });


        // 가입하기 버튼 클릭 리스너
        buttonSignUp.setOnClickListener(v -> {
            if (validateInputs()) {
                // 1. 화면의 입력 필드에서 값 가져오기
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString(); // trim() 사용하지 않음 (비밀번호 공백은 의미있을 수 있음)

                // 생년월일은 DatePicker 등에서 받아와 "YYYY-MM-DD" 형식으로 미리 포맷했다고 가정합니다.
                // 여기서는 EditText에서 직접 가져오지만, 실제 앱에서는 DatePicker 사용을 권장합니다.
                String birthday = editTextBirthday.getText().toString().trim();

                // 전화번호는 스피너와 두 개의 EditText에서 가져옵니다.
                // 'phone1'에 스피너 값, 'phone2'에 editTextPhone1 값, 'phone3'에 editTextPhone2 값 매핑
                String phone1 = spinnerPhonePrefix.getSelectedItem().toString().trim(); // 예: "010", "+82" 등
                String phone2 = editTextPhone1.getText().toString().trim();           // 예: "1234"
                String phone3 = editTextPhone2.getText().toString().trim();           // 예: "5678"

                // 2. SignUpData 객체 생성
                // 이 때 SignUpData 클래스의 생성자 인자 순서와 타입이 정확히 일치해야 합니다.
                SignUpData signUpData = new SignUpData(
                        email,
                        password,
                        birthday,
                        phone1, // 스피너 값
                        phone2, // editTextPhone1 값
                        phone3  // editTextPhone2 값
                );

                // TODO: 데이터베이스에 회원가입 정보 저장

                // 3. 생성된 SignUpData 객체를 Intent에 담아 다음 Activity로 전달
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class); // 'NextActivity.class'를 실제 다음 화면 액티비티로 변경
                intent.putExtra("signUpData", signUpData); // "signUpData"는 키(key)입니다. 다음 액티비티에서 이 키로 객체를 받습니다.
                startActivity(intent);

                // 선택 사항: 현재 회원가입 화면을 종료하여 뒤로 가기 시 다시 이 화면으로 돌아오지 않도록 합니다.
                Toast.makeText(SignUpActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this, // Context (Activity 인스턴스)
            (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                // 날짜 선택 시 호출되는 콜백
                year = selectedYear;
                month = selectedMonth; // 월은 0부터 시작 (0: 1월, 11: 12월)
                day = selectedDayOfMonth;

                // EditText에 선택된 날짜 표시 (예: YYYY-MM-DD 형식)
                String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day);
                editTextBirthday.setText(selectedDate);
            },
            year, month, day); // 초기 선택 날짜 (현재 날짜로 초기화)

        datePickerDialog.show(); // DatePickerDialog 표시
    }

    private void checkPasswordsMatch() {

    }

    /**
     * 모든 입력 필드의 유효성을 검사하는 메서드
     */
    private boolean validateInputs() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String birthday = editTextBirthday.getText().toString();
        String phonePrefix = spinnerPhonePrefix.getSelectedItem().toString(); // Spinner에서 선택된 값 가져오기
        String phone1 = editTextPhone1.getText().toString();
        String phone2 = editTextPhone2.getText().toString();

        // 이메일 유효성 (간단한 예시)
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("올바른 이메일 주소를 입력해주세요.");
            return false;
        }

        // 비밀번호 유효성
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this, "비밀번호는 최소 8자, 숫자와 문자를 포함해야 합니다.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 비밀번호 일치 여부
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("비밀번호가 일치하지 않습니다.");
            return false;
        }

        // 생년월일 입력 여부
        if (birthday.isEmpty()) {
            editTextBirthday.setError("생년월일을 입력해주세요.");
            return false;
        }

        // 전화번호 입력 여부 (Spinner는 항상 선택되므로, EditText만 확인)
        if (phone1.isEmpty() || phone2.isEmpty()) {
            Toast.makeText(this, "전화번호를 모두 입력해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 모든 유효성 검사 통과
        return true;
    }
}