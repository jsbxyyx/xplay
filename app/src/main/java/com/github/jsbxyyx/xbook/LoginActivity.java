package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookV2NetHelper;
import com.github.jsbxyyx.xplay.lib.model.LibUserListing;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private BookV2NetHelper bookV2NetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bookV2NetHelper = new BookV2NetHelper();

        Intent intent = getIntent();
        String className = intent.getStringExtra("className");

        EditText et_login_user = findViewById(R.id.et_login_user);
        EditText et_login_password = findViewById(R.id.et_login_password);

        findViewById(R.id.btn_login).setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            DialogLoading loading = new DialogLoading(this);
            loading.show();
            bookV2NetHelper.login(user, password, new DataCallback<LibUserListing>() {
                @Override
                public void call(LibUserListing userListing, Throwable err) {
                    UiUtils.post(() -> {
                        loading.dismiss();
                    });
                    if (err != null) {
                        LogUtil.d(TAG, "login failed. %s", LogUtil.getStackTraceString(err));
                        UiUtils.showToast("登录失败 : " + err.getMessage());
                        return;
                    }
                    // V2 login 已经保存 session/profile 信息
                    Intent goIntent = new Intent();
                    if (!Common.isEmpty(className)) {
                        goIntent.setClassName(getPackageName(), className);
                    } else {
                        goIntent.setClassName(getPackageName(), MainActivity.class.getName());
                    }
                    goIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goIntent);
                }
            });
        });

        findViewById(R.id.tv_registration).setOnClickListener((v) -> {
            Intent intent1 = new Intent(getBaseContext(), RegistrationActivity.class);
            startActivity(intent1);
        });

    }
}