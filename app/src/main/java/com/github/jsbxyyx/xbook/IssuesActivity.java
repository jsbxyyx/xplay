package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

import java.util.Map;

/**
 * @author jsbxyyx
 */
public class IssuesActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
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
        btn_submit_issues.setOnClickListener(v -> {
            String title = et_issues_title.getText().toString();
            String body = et_issues_body.getText().toString();
            if (Common.isEmpty(title) || Common.isEmpty(body)) {
                UiUtils.showToast("标题或内容不能为空");
                return;
            }
            Map<String, String> kv = Common.parseKv(SessionManager.getSession());
            String userid = kv.getOrDefault(Common.serv_userid, "");
            body += ("\n\n用户 : [" + userid + "]" +
                    "\n\n来源 : [" + android.os.Build.MODEL + " | " + android.os.Build.VERSION.RELEASE + "]" +
                    "\n\n[APP : " + UiUtils.getVersionName() + "]");
            DialogLoading loading = new DialogLoading(mActivity, "疯狂提交中...");
            loading.show();
            bookNetHelper.cloudIssues(title, body, new DataCallback() {
                @Override
                public void call(Object o, Throwable err) {
                    UiUtils.post(() -> {
                        loading.dismiss();
                    });
                    if (err != null) {
                        UiUtils.showToast("提交反馈失败：" + err.getMessage());
                        return;
                    }
                    UiUtils.showToast("提交成功");
                }
            });
        });

    }
}