package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

    private BookNetHelper bookNetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bookNetHelper = new BookNetHelper();

        Intent intent = getIntent();
        String className = intent.getStringExtra("className");

        EditText et_login_user = findViewById(R.id.et_login_user);
        EditText et_login_password = findViewById(R.id.et_login_password);

        findViewById(R.id.btn_login).setOnClickListener((v) -> {
            String user = et_login_user.getText().toString();
            String password = et_login_password.getText().toString();
            bookNetHelper.login(user, password, new DataCallback<String>() {
                @Override
                public void call(String str, Throwable err) {
                    runOnUiThread(() -> {
                        if (err != null) {
                            Toast.makeText(getBaseContext(), "登录失败 : " + err.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        SessionManager.setSession(str);
                        SPUtils.putData(getBaseContext(), Common.login_key, str);
                        Intent goIntent = new Intent();
                        if (!TextUtils.isEmpty(className)) {
                            goIntent.setClassName(getPackageName(), className);
                        } else {
                            goIntent.setClassName(getPackageName(), MainActivity.class.getName());
                        }
                        goIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goIntent);
                    });
                }
            });
        });

        findViewById(R.id.tv_registration).setOnClickListener((v) -> {
            Intent intent1 = new Intent(getBaseContext(), RegistrationActivity.class);
            startActivity(intent1);
        });

    }
}