package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

/**
 * @author jsbxyyx
 */
public class IssuesActivity extends AppCompatActivity {

    private BookNetHelper bookNetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        bookNetHelper = new BookNetHelper();

        Activity mActivity = this;

        EditText et_issues_title = findViewById(R.id.et_issues_title);
        EditText et_issues_body = findViewById(R.id.et_issues_body);

        Button btn_submit_issues = findViewById(R.id.btn_submit_issues);
        btn_submit_issues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_issues_title.getText().toString();
                String body = et_issues_body.getText().toString();
                if (Common.isEmpty(title) || Common.isEmpty(body)) {
                    Toast.makeText(getBaseContext(), "标题或内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                body += ("\n\n来源：[" + android.os.Build.MODEL + "] " + android.os.Build.VERSION.RELEASE + "\n");
                LoadingDialog loading = new LoadingDialog(mActivity, "疯狂提交中...");
                loading.show();
                bookNetHelper.cloudIssues(title, body, (o, err) -> {
                    runOnUiThread(() -> {
                        loading.dismiss();
                        if (err != null) {
                            Toast.makeText(getBaseContext(), "提交反馈失败：" + err.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getBaseContext(), "提交成功", Toast.LENGTH_LONG).show();
                    });
                });
            }
        });

    }
}