package com.github.jsbxyyx.xbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xplay.lib.model.LibUserListing;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookV2NetHelper;

public class ForgetpwdActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private BookV2NetHelper bookV2NetHelper;
    private BookNetHelper bookV1NetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);

        bookV1NetHelper = new BookNetHelper();
        bookV2NetHelper = new BookV2NetHelper();

        EditText et_login_user = findViewById(R.id.et_login_user);
        EditText et_login_password = findViewById(R.id.et_login_password);
        EditText et_login_code = findViewById(R.id.et_login_code);
        Button btn_send_code = findViewById(R.id.btn_send_code);
        Button btn_resetpwd = findViewById(R.id.btn_resetpwd);
        TextView tv_login = findViewById(R.id.tv_login);

        btn_send_code.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            bookV2NetHelper.sendCode("reset_password", user, null, null, "", new DataCallback<LibUserListing>() {
                @Override
                public void call(LibUserListing dataObject, Throwable err) {
                    if (err != null) {
                        LogUtil.d(TAG, "send reset code failed. %s", LogUtil.getStackTraceString(err));
                        UiUtils.showToast("发送重置验证码:" + err.getMessage());
                        return;
                    }
                    int success = dataObject == null ? 0 : 1;
                    if (success == 1) {
                        LogUtil.d(TAG, "send reset code success.");
                        UiUtils.showToast("发送成功");
                    } else {
                        String errText = "发送验证码失败";
                        LogUtil.d(TAG, "send reset code failed. %s", errText);
                        UiUtils.showToast(errText);
                    }
                }
            });
        });

        btn_resetpwd.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            String code = et_login_code.getText().toString();
            bookV1NetHelper.resetpwd(user, password, code, new DataCallback<JsonNode>() {
                @Override
                public void call(JsonNode respData, Throwable err) {
                    if (err != null) {
                        LogUtil.d(TAG, "reset password failed. %s", LogUtil.getStackTraceString(err));
                        UiUtils.showToast("重置密码失败:" + err.getMessage());
                        return;
                    }
                    if (respData.get("success").asInt() == 1) {
                        LogUtil.d(TAG, "reset password success.");
                        UiUtils.showToast("重置成功");
                    } else {
                        String errText = respData.get("message").asText("");
                        UiUtils.showToast(errText);
                        LogUtil.d(TAG, "reset password failed. %s", errText);
                    }
                }
            });
        });

        tv_login.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });

    }
}