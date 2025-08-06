package com.takeiteasy.chatchat.binder.profile;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.takeiteasy.chatchat.databinding.ActivityProfileEditBinding;
import com.takeiteasy.chatchat.funtional.Action;
import com.takeiteasy.chatchat.model.profile.ProfileData;
import com.takeiteasy.chatchat.viewmodel.MainViewModel;

import java.util.function.Consumer;

public class ProfileEditBinder {
    private final ActivityProfileEditBinding binding;
    private final Context context;

    public ProfileEditBinder(ActivityProfileEditBinding binding) {
        this.binding = binding;
        this.context = binding.getRoot().getContext();
    }

    public void bind(MainViewModel viewModel, LifecycleOwner lifecycleOwner, Consumer<ProfileData> consumer) {
        viewModel.getProfile().observe(lifecycleOwner, consumer::accept);
    }
}
