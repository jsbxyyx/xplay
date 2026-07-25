package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookV2NetHelper;
import com.github.jsbxyyx.xplay.lib.model.LibUserListing;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class RegistrationActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private BookV2NetHelper bookV2NetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        bookV2NetHelper = new BookV2NetHelper();

        EditText et_login_user = findViewById(R.id.et_login_user);
        EditText et_login_password = findViewById(R.id.et_login_password);
        EditText et_login_code = findViewById(R.id.et_login_code);
        Button btn_send_code = findViewById(R.id.btn_send_code);
        EditText et_nickname = findViewById(R.id.et_nickname);
        Button btn_registration = findViewById(R.id.btn_registration);
        TextView tv_login = findViewById(R.id.tv_login);
        TextView tv_forgetpwd = findViewById(R.id.tv_forgetpwd);

        btn_send_code.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            String nickname = et_nickname.getText().toString();
            bookV2NetHelper.sendCode("register", user, password, nickname, "", new DataCallback<LibUserListing>() {
                @Override
                public void call(LibUserListing dataObject, Throwable err) {
                    if (err != null) {
                        LogUtil.d(TAG, "send code failed. %s", LogUtil.getStackTraceString(err));
                        UiUtils.showToast("发送验证码:" + err.getMessage());
                        return;
                    }
                    int success = dataObject == null ? 0 : 1;
                    if (success == 1) {
                        UiUtils.showToast("发送成功");
                        LogUtil.d(TAG, "send code success.");
                    } else {
                        String errText = "发送验证码失败";
                        UiUtils.showToast(errText);
                        LogUtil.d(TAG, "send code failed. %s", errText);
                    }
                }
            });
        });

        btn_registration.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            String code = et_login_code.getText().toString();
            String nickname = et_nickname.getText().toString();
            DialogLoading loading = new DialogLoading(this);
            loading.show();
            bookV2NetHelper.register(user, password, nickname, new DataCallback<LibUserListing>() {
                @Override
                public void call(LibUserListing dataObject, Throwable err) {
                    UiUtils.post(() -> {
                        loading.dismiss();
                    });
                    if (err != null) {
                        LogUtil.d(TAG, "registration failed. %s", LogUtil.getStackTraceString(err));
                        UiUtils.showToast("注册失败:" + err.getMessage());
                        return;
                    }
                    // V2 已完成 session/profile 保存
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        });

        tv_login.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });

        tv_forgetpwd.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), ForgetpwdActivity.class);
            startActivity(intent);
        });
    }
}