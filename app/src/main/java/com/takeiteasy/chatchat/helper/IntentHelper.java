package com.takeiteasy.chatchat.helper;

import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;

import com.takeiteasy.chatchat.model.signup.SignUpData;

public class IntentHelper {
    /**
     * Intent에서 ParcelableExtra를 안전하게 가져오는 제네릭 메서드입니다.
     * Android API 레벨에 따라 적절한 getParcelableExtra 메서드를 사용합니다.
     *
     * @param <T> 가져올 Parcelable 객체의 타입입니다. (예: SignUpData, ProfileData)
     * @param intent 데이터를 포함하는 Intent 객체입니다.
     * @param name Intent에 저장된 데이터의 키(key) 이름입니다.
     * @param clazz 가져올 Parcelable 객체의 Class 타입입니다. (예: SignUpData.class)
     * @return Intent에서 가져온 Parcelable 객체 (해당 타입으로 캐스팅됨), 또는 없으면 null을 반환합니다.
     */
    public static <T extends Parcelable> T getExtra(Intent intent, String name, Class<T> clazz) {
        T data = null; // 초기값을 null로 설정합니다.

        // Intent가 null이 아니고, 해당 키가 포함되어 있는지 확인하여 NullPointerException을 방지합니다.
        if (intent != null && intent.hasExtra(name)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33 (Android 13) 이상에서는 Class 타입을 인자로 받는 안전한 메서드를 사용합니다.
                data = intent.getParcelableExtra(name, clazz);
            } else {
                // API 33 미만에서는 Parcelable을 반환하는 메서드를 사용하고, 명시적으로 캐스팅합니다.
                // 이 경우 컴파일러 경고가 발생할 수 있지만, 타입 안전성을 위해 명시적 캐스팅이 필요합니다.
                //noinspection deprecation
                data = intent.getParcelableExtra(name);
                if (data != null && !clazz.isInstance(data)) {
                    // 만약 가져온 객체가 예상한 타입이 아니라면 null로 처리하거나 예외를 던질 수 있습니다.
                    // 여기서는 null로 처리하여 잘못된 타입 캐스팅 오류를 방지합니다.
                    data = null;
                } else if (data != null) {
                    // 안전한 캐스팅을 위해 타입 체크 후 캐스팅합니다.
                    data = clazz.cast(data);
                }
            }
        }
        return data;
    }
}
