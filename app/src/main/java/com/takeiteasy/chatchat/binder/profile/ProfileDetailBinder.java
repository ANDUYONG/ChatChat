package com.takeiteasy.chatchat.binder.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.takeiteasy.chatchat.ProfileDetailActivity;
import com.takeiteasy.chatchat.R;
import com.takeiteasy.chatchat.databinding.ActivityMainBinding;
import com.takeiteasy.chatchat.databinding.ActivityProfileDetailBinding;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.model.profile.adapter.ProfileDataListAdapter;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

public class ProfileDetailBinder {
    private final ActivityProfileDetailBinding binding;
    private final Context context;
    private ProfileData profile;

    public ProfileDetailBinder(ActivityProfileDetailBinding binding) {
        this.binding = binding;
        this.context = binding.getRoot().getContext();
    }

    public void bind(MainViewModel viewModel, LifecycleOwner lifecycleOwner) {
        // ViewModel의 LiveData를 관찰하여 데이터가 변경될 때 UI 업데이트
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        String loginUserId = preferenceManager.getString("userId", null);
        viewModel.getProfile().observe(lifecycleOwner, response -> {
            profile = response;
            if (response != null) {
//                if(!mAuth.getCurrentUser().equals(profile.getUserId())) {
                if(!loginUserId.equals(response.getUserId())) {
                    // LinearLayout을 보이지 않게 하지만, 공간은 그대로 유지합니다.
                    binding.layoutEditProfile.setVisibility(View.INVISIBLE);
                }

                // 받은 데이터를 UI에 표시하거나 추가 처리 (예: 서버로 전송)
                // 1. 조회된 사용자 이름
                binding.textViewUserName.setText(response.getNickName());
                // 2. 사용자 상메
                binding.textViewStatusMessage.setText(response.getStatusMsg());
                // 3. 이미지 url 바인딩
                this.loadImage(binding.profileView);
                this.loadImage(binding.profileViewBackground);


            }
        });
    }

    private void loadImage(ImageView imageView) {
        // 이것은 로컬 벡터 드로어블입니다.
        // 안드로이드 내장 기능을 사용해 이미지를 직접 설정합니다.
        // 이것은 Firebase Storage에서 가져올 원격 이미지입니다.
        if(profile.getProfileUrl() != null && !profile.getProfileUrl().isEmpty()) {
            String imageUri = "default_profile_background.png";
            if (binding.profileView.getId() == imageView.getId()) {
                imageUri = profile.getProfileUrl();
            } else {
                int size = profile.getBackgroundUrls().size();
                if(profile.getBackgroundUrls() != null && size > 0) {
                    imageUri = profile.getBackgroundUrls().get(size-1);
                }
            }
            // Firebase Storage 인스턴스와 참조를 가져옵니다.
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageUri);

            // 다운로드 URL을 가져옵니다.
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Glide를 사용하여 URL로부터 원격 이미지를 로드합니다.
                Glide.with(context)
                        .load(uri)
                        .into(imageView);
            }).addOnFailureListener(exception -> {
                // 오류를 처리합니다 (예: 로컬 리소스의 기본 이미지를 설정).
                System.err.println("이미지 다운로드 URL을 가져오는 데 실패했습니다: " + exception.getMessage());
                // 필요하다면, 여기서 대체 이미지를 설정할 수 있습니다.
            });
        } else {
            if (binding.profileView.getId() == imageView.getId()) {
                imageView.setImageResource(R.drawable.ic_default_profile_filled);
            } else {
                imageView.setImageResource(R.drawable.default_profile_background);
            }
        }
    }
}
