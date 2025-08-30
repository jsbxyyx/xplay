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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.DateUtils;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.ThreadUtils;
import com.github.jsbxyyx.xbook.common.Tuple;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.contribution.ContributionConfig;
import com.github.jsbxyyx.xbook.contribution.ContributionItem;
import com.github.jsbxyyx.xbook.contribution.ContributionView;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;
import com.github.jsbxyyx.xbook.data.bean.ViewTime;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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

    private final String TAG = getClass().getSimpleName();

    private View mView;
    private Activity mActivity;
    private ContributionView contributionView;

    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    private RecyclerView rv_download_book;
    private ListBookDownloadAdapter listBookDownloadAdapter;

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
                UiUtils.showToast(builder.toString());
                //contribution_view_text.setText(builder.toString());
            }
        });

        bookNetHelper.cloudVersions(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                if (err != null) {
                    LogUtil.d(TAG, "%s", LogUtil.getStackTraceString(err));
                    UiUtils.showToast("获取版本更新失败:" + err.getMessage());
                    return;
                }
                JsonNode data = o.get("data");
                if (data.isEmpty()) {
                    LogUtil.d(TAG, "versions empty.");
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

        showDownloadBook();
    }

    private void showDownloadBook() {
        String readerImageShow = SPUtils.getData(mActivity, Common.reader_image_show_key, "1");
        List<Book> dataList = bookDbHelper.findAllBook();
        dataList.sort((t1, t2) -> {
            if (t1 == null && t2 == null) {
                return 0;
            }
            if (t1 == null) {
                return 1;
            }
            if (t2 == null) {
                return -1;
            }
            BookReader reader1 = t1.getBookReader();
            BookReader reader2 = t2.getBookReader();
            if (reader1 == null && reader2 == null) {
                return 0;
            }
            if (reader1 == null) {
                return 1;
            }
            if (reader2 == null) {
                return -1;
            }
            String updated1 = reader1.getUpdated();
            String updated2 = reader2.getUpdated();
            if (updated1 == null && updated2 == null) {
                return 0;
            }
            if (updated1 == null) {
                return 1;
            }
            if (updated2 == null) {
                return -1;
            }
            return updated2.compareTo(updated1);
        });
        rv_download_book = mView.findViewById(R.id.rv_download_book);
        LinearLayoutManager downloadLayoutManager = new LinearLayoutManager(mActivity);
        rv_download_book.setLayoutManager(downloadLayoutManager);
        rv_download_book.setHasFixedSize(true);

        listBookDownloadAdapter = new ListBookDownloadAdapter(mActivity, dataList,
                Common.checked.equals(readerImageShow), (book, type, position) -> {
                    if (Common.action_delete.equals(type)) {
                        String file_path = book.extractFilePath();
                        new AlertDialog.Builder(mActivity)
                                .setTitle("提示")
                                .setMessage("确认删除?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        bookDbHelper.deleteBook(book.getId());
                                        boolean delete = new File(file_path).delete();
                                        LogUtil.d(TAG, "onClick: delete [%s] : %s", file_path, delete);
                                        onResume();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } else if (Common.action_upload.equals(type)) {
                        bookNetHelper.cloudSync(book, new DataCallback<JsonNode>() {
                            @Override
                            public void call(JsonNode o, Throwable err) {
                                if (err != null) {
                                    LogUtil.d(TAG, "书籍同步失败: %s", book.getTitle());
                                    return;
                                }
                                String name = o.get("data").get("name").asText();
                                String sha = o.get("data").get("sha").asText();
                                Book book_db = bookDbHelper.findBookById(book.getId() + "");
                                book_db.fillSha(sha);
                                bookDbHelper.updateBook(book_db);
                                LogUtil.d(TAG, "upload 2: %s", name);
                                mActivity.runOnUiThread(() -> {
                                    UiUtils.showToast("同步成功《" + book.getTitle() + "》");
                                });
                            }
                        });
                    } else if (Common.action_download_meta.equals(type)) {
                        bookNetHelper.cloudGetMeta(book, new DataCallback<JsonNode>() {
                            @Override
                            public void call(JsonNode o, Throwable err) {
                                if (err != null) {
                                    mActivity.runOnUiThread(() -> {
                                        UiUtils.showToast("同步阅读进度失败：" + err.getMessage());
                                    });
                                    return;
                                }
                                if (o.get("data").has("content")) {
                                    String content = o.get("data").get("content").asText().replace("\n", "");
                                    String sha = o.get("data").get("sha").asText();
                                    JsonNode tree = JsonUtil.readTree(new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8));
                                    Book book1 = JsonUtil.convertValue(tree, new TypeReference<Book>() {
                                    });
                                    Book book_db = bookDbHelper.findBookById(book1.getId() + "");
                                    if (book_db != null) {
                                        book_db.fillSha(sha);
                                        bookDbHelper.updateBook(book_db);
                                        if (book1.getBookReader() != null) {
                                            bookDbHelper.updateBookReaderByBookId(book1.getBookReader());
                                        }
                                        LogUtil.d(TAG, "call: update book: %s", book_db.getTitle());
                                    } else {
                                        bookDbHelper.insertBook(book1);
                                        if (book1.getBookReader() != null) {
                                            bookDbHelper.insertBookReader(book1.getBookReader());
                                        }
                                        LogUtil.d(TAG, "call: insert book: %s", book1.getTitle());
                                    }
                                    mActivity.runOnUiThread(() -> {
                                        UiUtils.showToast("同步阅读进度成功");
                                    });
                                }
                            }
                        });
                    } else if (Common.action_file_download.equals(type)) {
                        if (!new File(book.extractFilePath()).exists()) {
                            bookNetHelper.downloadWithMagic(book.getDownloadUrl(), Common.xbook_dir, book.getBid(), new DataCallback<File>() {
                                @Override
                                public void call(File f, Throwable err) {
                                    String file_path = book.extractFilePath();
                                    if (!Common.isBlank(file_path) && !file_path.equals(f.getAbsolutePath())) {
                                        book.fillFilePath(f.getAbsolutePath());
                                        bookDbHelper.updateBook(book);
                                        LogUtil.i(TAG, "update " + book.getBid() + "/" + book.getTitle() + " file path.");
                                    }
                                }
                            }, (bytesRead, total) -> {
                                double percent = bytesRead * 1.0 / total * 100;
                                LogUtil.d(TAG, "下载进度：%.1f%%", percent);
                                if (percent >= 1.0) {
                                    UiUtils.showToast(String.format("%s 下载进度：%.1f%%", book.getTitle(), percent));
                                }
                            }, Common.MAGIC);
                        }
                    } else if (Common.action_image_hide.equals(type)) {
                        View parent = rv_download_book.getLayoutManager().getChildAt(position);
                        if (parent != null) {
                            ImageView iv = parent.findViewById(R.id.book_reader_image);
                            if (iv.getVisibility() == View.GONE) {
                                Picasso.get().load(book.getCoverImage()).error(R.drawable.baseline_menu_book_24).into(iv);
                                iv.setVisibility(View.VISIBLE);
                            } else {
                                iv.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        listBookDownloadAdapter.setOnItemClickListener((book, position) -> {
            String file_path = book.extractFilePath();
            Intent intent = new Intent(mActivity, ViewActivity.class);
            intent.putExtra("file_path", file_path);
            intent.putExtra("book_id", book.getId() + "");
            intent.putExtra("book_title", book.getTitle());
            BookReader bookReader = book.getBookReader();
            if (bookReader == null) {
                intent.putExtra("cur", "");
                intent.putExtra("pages", "");
            } else {
                intent.putExtra("cur", bookReader.getCur());
                intent.putExtra("pages", bookReader.getPages());
            }
            startActivity(intent);
        });
        rv_download_book.setAdapter(listBookDownloadAdapter);
    }
}