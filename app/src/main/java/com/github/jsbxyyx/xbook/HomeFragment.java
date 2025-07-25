package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.DateUtils;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.ThreadUtils;
import com.github.jsbxyyx.xbook.common.Tuple;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.contribution.ContributionConfig;
import com.github.jsbxyyx.xbook.contribution.ContributionItem;
import com.github.jsbxyyx.xbook.contribution.ContributionView;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.ViewTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class HomeFragment extends Fragment {

    private String TAG = "xbook";

    private View mView;
    private Activity mActivity;
    private ContributionView contributionView;

    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        mActivity = getActivity();

        bookNetHelper = new BookNetHelper();
        bookDbHelper = BookDbHelper.getInstance();

        TextView contribution_view_text = mActivity.findViewById(R.id.contribution_view_text);

        Tuple<Integer, Date, List<ContributionItem>> tuple = contributionViewInitData();

        contributionView = mActivity.findViewById(R.id.contribution_view);

        contributionView.setData(tuple.getSecond(), tuple.getThird(), ContributionConfig.defaultConfig());
        contributionView.setOnItemClick(new ContributionView.OnItemClickListener() {
            @Override
            public void onClick(int position, ContributionItem item) {
                StringBuilder builder = new StringBuilder();
                if (item.getData() != null) {
                    builder.append(DateUtils.format(item.getTime(), "yyyy年MM月dd日"))
                            .append(DateUtils.getWeekOfDate(item.getTime()))
                            .append("阅读")
                            .append(String.format("%.1f", item.getNumber() / 1000 / 60.0))
                            .append("分钟");
                    contribution_view_text.setTextColor(0xFF216E39);
                } else {
                    builder.append(DateUtils.format(item.getTime(), "yyyy年MM月dd日"))
                            .append(DateUtils.getWeekOfDate(item.getTime()))
                            .append("阅读")
                            .append("0")
                            .append("分钟");
                    contribution_view_text.setTextColor(Color.GRAY);
                }
                contribution_view_text.setText(builder.toString());
            }
        });

        bookNetHelper.cloudVersions(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                if (err != null) {
                    LogUtil.d(getClass().getSimpleName(), "%s", LogUtil.getStackTraceString(err));
                    UiUtils.showToast("获取版本更新失败:" + err.getMessage());
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
                    String message = "有新版本啦，前往 我的-设置 进行版本更新";
                    mActivity.runOnUiThread(() -> {
                        new AlertDialog.Builder(mActivity)
                                .setTitle("提示")
                                .setMessage(message)
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

    private Tuple<Integer, Date, List<ContributionItem>> contributionViewInitData() {
        int days = 182;
        Date startDate = DateUtils.setHms(new Date(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * days)), 0, 0, 0, 0);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        List<ContributionItem> initData = new ArrayList<>(180);
        for (int i = 0; i <= days; i++) {
            initData.add(new ContributionItem(startCalendar.getTime(), 0));
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return new Tuple<>(days, startDate, initData);
    }

    @Override
    public void onResume() {
        super.onResume();

        Tuple<Integer, Date, List<ContributionItem>> tuple = contributionViewInitData();

        ThreadUtils.submit(() -> {
            Map<String, String> kv = Common.parseKv(SessionManager.getSession());
            List<ViewTime> list = bookDbHelper.findViewTime(tuple.getSecond(), kv.getOrDefault(Common.serv_userid, ""));
            List<ContributionItem> data = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                Map<String, List<ViewTime>> map = new LinkedHashMap<>();
                for (ViewTime viewTime : list) {
                    String created = viewTime.getCreated();
                    String key = DateUtils.format(new Date(Long.parseLong(created)), "yyyy-MM-dd");
                    if (map.get(key) != null) {
                        map.get(key).add(viewTime);
                    } else {
                        List<ViewTime> viewTimeList = new ArrayList<>();
                        viewTimeList.add(viewTime);
                        map.put(key, viewTimeList);
                    }
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tuple.getSecond());
                for (int i = 0; i <= tuple.getFirst(); i++) {
                    String format = DateUtils.format(calendar.getTime(), "yyyy-MM-dd");
                    if (!map.containsKey(format)) {
                        data.add(new ContributionItem(calendar.getTime(), 0));
                    } else {
                        LogUtil.d(TAG, "contains date : %s", format);
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                for (Map.Entry<String, List<ViewTime>> entry : map.entrySet()) {
                    Date parse = DateUtils.parse(entry.getKey(), "yyyy-MM-dd");
                    int millisecond = 0;
                    for (ViewTime vt : entry.getValue()) {
                        millisecond += vt.getTime();
                    }
                    data.add(new ContributionItem(parse, millisecond, entry.getValue()));
                }
            }
            mHandler.post(() -> {
                if (!data.isEmpty()) {
                    contributionView.setData(tuple.getSecond(), data);
                }
            });
        });
    }
}