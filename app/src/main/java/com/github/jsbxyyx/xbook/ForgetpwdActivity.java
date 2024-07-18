package com.github.jsbxyyx.xbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

public class ForgetpwdActivity extends AppCompatActivity {

    private BookNetHelper bookNetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);

        bookNetHelper = new BookNetHelper();

        EditText et_login_user = findViewById(R.id.et_login_user);
        EditText et_login_password = findViewById(R.id.et_login_password);
        EditText et_login_code = findViewById(R.id.et_login_code);
        Button btn_send_code = findViewById(R.id.btn_send_code);
        Button btn_resetpwd = findViewById(R.id.btn_resetpwd);
        TextView tv_login = findViewById(R.id.tv_login);

        btn_send_code.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            bookNetHelper.sendCodePasswordRecovery(user, new DataCallback<JsonNode>() {
                @Override
                public void call(JsonNode dataObject, Throwable err) {
                    runOnUiThread(() -> {
                        if (err != null) {
                            UiUtils.showToast("发送重置验证码:" + err.getMessage());
                            return;
                        }
                        int success = dataObject.get("success").asInt();
                        if (success == 1) {
                            UiUtils.showToast("发送成功");
                        } else {
                            UiUtils.showToast(dataObject.get("err").asText());
                        }
                    });
                }
            });
        });

        btn_resetpwd.setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            String code = et_login_code.getText().toString();
            bookNetHelper.resetpwd(user, password, code, new DataCallback<JsonNode>() {
                @Override
                public void call(JsonNode respData, Throwable err) {
                    runOnUiThread(() -> {
                        if (err != null) {
                            UiUtils.showToast("重置密码失败:" + err.getMessage());
                            return;
                        }
                        if (respData.get("success").asInt() == 1) {
                            UiUtils.showToast("重置成功");
                        } else {
                            UiUtils.showToast(respData.get("message").asText(""));
                        }
                    });
                }
            });
        });

        tv_login.setOnClickListener((v) -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        });

    }
}