package com.github.jsbxyyx.xbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;

/**
 * @author jsbxyyx
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String data = SPUtils.getData(getBaseContext(), Common.search_ext_key);

        LinearLayout ll_ext = findViewById(R.id.ll_ext);
        int count = ll_ext.getChildCount();
        for (int i =0; i < count; i++) {
            View view = ll_ext.getChildAt(i);
            if (view instanceof CheckBox) {
                String text = ((CheckBox) view).getText().toString();
                if (data.contains(text + Common.comma)) {
                    ((CheckBox) view).setChecked(true);
                }
                view.setOnClickListener((v) -> {
                    CheckBox cb = (CheckBox) v;
                    String text_ = cb.getText().toString();
                    String data_ = SPUtils.getData(getBaseContext(), Common.search_ext_key);
                    if (cb.isChecked()) {
                        if (!data_.contains(text_ + Common.comma)) {
                            data_ += (text_ + Common.comma);
                            SPUtils.putData(getBaseContext(), Common.search_ext_key, data_);
                        }
                    } else {
                        data_ = data_.replace(text_ + Common.comma, "");
                        SPUtils.putData(getBaseContext(), Common.search_ext_key, data_);
                    }
                    LogUtil.d(getClass().getSimpleName(), "ext: %s", SPUtils.getData(getBaseContext(), Common.search_ext_key));
                });
            }
        }

    }

}