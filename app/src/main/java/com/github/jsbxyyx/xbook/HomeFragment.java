package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class HomeFragment extends Fragment {

    private String TAG = "xbook";

    private View mView;
    private Activity mActivity;

    private BookNetHelper bookNetHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bookNetHelper = new BookNetHelper();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        mActivity = getActivity();

        bookNetHelper.cloudVersions(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                if (err != null) {
                    LogUtil.d(getClass().getSimpleName(), "%s", LogUtil.getStackTraceString(err));
                    mActivity.runOnUiThread(() -> {
                        UiUtils.showToast("获取版本更新失败:" + err.getMessage());
                    });
                    return;
                }
                JsonNode data = o.get("data");
                if (data.isEmpty()) {
                    LogUtil.d(getClass().getSimpleName(), "versions empty.");
                    return;
                }
                JsonNode update = data.get(0);
                String versionName = UiUtils.getVersionName();
                double localName = Double.parseDouble(versionName);
                double cloudName = Double.parseDouble(update.get("name").asText().trim());
                if (cloudName > localName) {
                    mActivity.runOnUiThread(() -> {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("提示")
                                .setMessage("有新版本啦，前往 我的-设置 进行版本更新")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Intent localIntent = new Intent(mActivity, SettingsActivity.class);
                                        startActivity(localIntent);
                                    }
                                }).setNegativeButton(android.R.string.no, null)
                                .setCancelable(false)
                                .show();
                    });
                }
            }
        });

    }

}