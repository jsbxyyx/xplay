package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class RegistrationActivity extends AppCompatActivity {

    private BookNetHelper bookNetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        bookNetHelper = new BookNetHelper();

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
            bookNetHelper.sendCode(user, password, new DataCallback<JsonNode>() {
                @Override
                public void call(JsonNode dataObject, Throwable err) {
                    runOnUiThread(() -> {
                        if (err != null) {
                            UiUtils.showToast("发送验证码:" + err.getMessage());
                            return;
                        }
                        int success = dataObject.get("success").asInt();
                        if (success == 1) {
                            UiUtils.showToast("发送成功");
                        } else {
                            UiUtils.showToast(dataObject.get("err").asText(""));
                        }
                    });
                }
            });
        });

        btn_registration.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            String code = et_login_code.getText().toString();
            String nickname = et_nickname.getText().toString();
            LoadingDialog loading = new LoadingDialog(this);
            loading.show();
            bookNetHelper.registrationWithNickname(user, password, code, nickname, new DataCallback<String>() {
                @Override
                public void call(String str, Throwable err) {
                    runOnUiThread(() -> {
                        loading.dismiss();
                        if (err != null) {
                            UiUtils.showToast("注册失败:" + err.getMessage());
                            return;
                        }
                        SessionManager.setSession(str);
                        SPUtils.putData(getBaseContext(), Common.login_key, str);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    });
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