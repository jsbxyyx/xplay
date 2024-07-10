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
                            Toast.makeText(getBaseContext(), "err:" + err.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        int success = dataObject.get("success").asInt();
                        if (success == 1) {
                            Toast.makeText(getBaseContext(), "发送成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), dataObject.get("err").asText(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getBaseContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (respData.get("success").asInt() == 1) {
                            Toast.makeText(getBaseContext(), "重置成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), respData.get("message").asText(""), Toast.LENGTH_LONG).show();
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