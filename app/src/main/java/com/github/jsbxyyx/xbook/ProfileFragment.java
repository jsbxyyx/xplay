package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;
import com.github.jsbxyyx.xbook.data.bean.Profile;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ProfileFragment extends Fragment {

    private String TAG = "xbook";

    private View mView;
    protected Activity mActivity;
    private ListView lv_profile;
    private ListView lv_download_book;
    private ListBookDownloadAdapter mBookDownloadAdapter;
    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    public ProfileFragment() {
        bookNetHelper = new BookNetHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        bookDbHelper = new BookDbHelper(mActivity);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    private void initData() {
        if (Common.isEmpty(SPUtils.getData(mActivity, Common.login_key))) {
            mView.findViewById(R.id.layout_login_in).setVisibility(View.GONE);
            View layout_login = mView.findViewById(R.id.layout_login);
            layout_login.setVisibility(View.VISIBLE);
            mView.findViewById(R.id.btn_open_login).setOnClickListener((v) -> {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("className", MainActivity.class.getName());
                startActivity(intent);
            });
        } else {
            LogUtil.d(TAG, "login in : %s", SPUtils.getData(mActivity, Common.login_key));
            SessionManager.setSession(SPUtils.getData(mActivity, Common.login_key));

            mView.findViewById(R.id.layout_login_in).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.layout_login).setVisibility(View.GONE);
            TextView tv_profile_nickname = mView.findViewById(R.id.tv_profile_nickname);
            TextView tv_profile_email = mView.findViewById(R.id.tv_profile_email);
            ImageView iv_profile_avatar = mView.findViewById(R.id.iv_profile_avatar);

            mView.findViewById(R.id.btn_logout).setOnClickListener((v) -> {
                SPUtils.putData(mActivity, Common.login_key, "");
                SPUtils.putData(mActivity, Common.profile_nickname_key, "");
                SPUtils.putData(mActivity, Common.profile_email_key, "");
                SessionManager.setSession(SPUtils.getData(mActivity, Common.login_key));
                onResume();
            });
            String email = SPUtils.getData(mActivity, Common.profile_email_key);
            if (Common.isEmpty(email)) {
                bookNetHelper.profile(new DataCallback<Profile>() {
                    @Override
                    public void call(Profile profile, Throwable err) {
                        mActivity.runOnUiThread(() -> {
                            if (err != null) {
                                UiUtils.showToast("获取个人资料失败:" + err.getMessage());
                                return;
                            }
                            tv_profile_nickname.setText(profile.getNickname());
                            tv_profile_email.setText(profile.getEmail());
                            iv_profile_avatar.setImageBitmap(headBitmap(profile.getEmail(), 128, "#6750a4"));
                            SPUtils.putData(mActivity, Common.profile_email_key, profile.getEmail());
                            SPUtils.putData(mActivity, Common.profile_nickname_key, profile.getNickname());
                        });
                    }
                });
            } else {
                tv_profile_nickname.setText(SPUtils.getData(mActivity, Common.profile_nickname_key));
                tv_profile_email.setText(email);
                iv_profile_avatar.setImageBitmap(headBitmap(email, 128, "#6750a4"));
            }
        }

        lv_profile = mView.findViewById(R.id.lv_profile);
        List<Object> data = new ArrayList<>();
        data.add("云同步本地");
        data.add("设置");
        data.add("意见反馈");
        data.add("帮助");
        data.add("测试");
        lv_profile.setAdapter(new ArrayAdapter(mActivity, R.layout.profile_item, R.id.tv_profile_item, data));
        lv_profile.setOnItemClickListener((parent, view1, position, id) -> {
            LogUtil.d(TAG, "setOnItemClickListener: %d", position);
            if (position == data.size() - 1) {
                test();
            } else if (position == 0) {
                downAllBook();
            } else if (position == 1) {
                settings();
            } else if (position == 2) {
                issues();
            } else if (position == 3) {
                donate();
            }
        });

        String readerImageShow = SPUtils.getData(mActivity, Common.reader_image_show_key, "1");

        List<Book> dataList = bookDbHelper.findAllBook();
        lv_download_book = mView.findViewById(R.id.lv_download_book);
        mBookDownloadAdapter = new ListBookDownloadAdapter(mActivity, dataList,
                Common.checked.equals(readerImageShow), new ListItemClickListener() {
            @Override
            public void onClick(View view, String type, int position) {
                if (Common.action_delete.equals(type)) {
                    Book book = mBookDownloadAdapter.getDataList().get(position);
                    String file_path = book.getRemarkProperty("file_path");

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
                    Book book = mBookDownloadAdapter.getDataList().get(position);
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
                            book_db.putRemarkProperty("sha", sha);
                            bookDbHelper.updateBook(book_db);
                            LogUtil.d(TAG, "upload 2: %s", name);
                            mActivity.runOnUiThread(() -> {
                                UiUtils.showToast("同步成功《" + book.getTitle() + "》");
                            });
                        }
                    });
                } else if (Common.action_download_meta.equals(type)) {
                    Book book = mBookDownloadAdapter.getDataList().get(position);
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
                                Book book = JsonUtil.convertValue(tree, new TypeReference<Book>() {
                                });
                                Book book_db = bookDbHelper.findBookById(book.getId() + "");
                                if (book_db != null) {
                                    book_db.putRemarkProperty("sha", sha);
                                    bookDbHelper.updateBook(book_db);
                                    if (book.getBookReader() != null) {
                                        bookDbHelper.updateBookReaderByBookId(book.getBookReader());
                                    }
                                    LogUtil.d(TAG, "call: update book: %s", book_db.getTitle());
                                } else {
                                    bookDbHelper.insertBook(book);
                                    if (book.getBookReader() != null) {
                                        bookDbHelper.insertBookReader(book.getBookReader());
                                    }
                                    LogUtil.d(TAG, "call: insert book: %s", book.getTitle());
                                }
                                mActivity.runOnUiThread(() -> {
                                    UiUtils.showToast("同步阅读进度成功");
                                });
                            }
                        }
                    });
                } else if (Common.action_file_download.equals(type)) {
                    Book book = mBookDownloadAdapter.getDataList().get(position);
                    if (!new File(book.getRemarkProperty("file_path")).exists()) {
                        View parent = (View) view.getParent();
                        TextView tv_text = parent.findViewById(R.id.tv_text);
                        bookNetHelper.downloadWithMagic(book.getDownloadUrl(), Common.xbook_dir, book.getBid(), new DataCallback.NopDataCallback(), new ProgressListener() {
                            @Override
                            public void onProgress(long bytesRead, long total) {
                                double percent = bytesRead * 1.0 / total * 100;
                                LogUtil.d(TAG, "下载进度：%.1f%%", percent);
                                mActivity.runOnUiThread(() -> {
                                    tv_text.setVisibility(View.VISIBLE);
                                    tv_text.setText(String.format("进度条：%.1f%%", percent));
                                });
                            }
                        }, Common.MAGIC);
                    }
                } else if (Common.action_image_hide.equals(type)) {
                    Book book = mBookDownloadAdapter.getDataList().get(position);
                    View parent = (View) view.getParent();
                    ImageView iv = parent.findViewById(R.id.book_reader_image);
                    if (iv.getVisibility() == View.GONE) {
                        Picasso.get().load(book.getCoverImage()).into(iv);
                        iv.setVisibility(View.VISIBLE);
                    } else {
                        iv.setVisibility(View.GONE);
                    }
                }
            }
        });
        lv_download_book.setAdapter(mBookDownloadAdapter);
        lv_download_book.setOnItemClickListener((parent, view, position, id) -> {
            Book book = mBookDownloadAdapter.getDataList().get(position);
            String file_path = book.getRemarkProperty("file_path");
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
    }

    private void upAllBook() {
        List<Book> allBook = bookDbHelper.findAllBook();
        for (Book book : allBook) {
            CountDownLatch latch = new CountDownLatch(1);
            bookNetHelper.cloudSync(book, new DataCallback<JsonNode>() {
                @Override
                public void call(JsonNode o, Throwable err) {
                    latch.countDown();
                    if (err != null) {
                        LogUtil.d(TAG, "书籍同步失败: %s", LogUtil.getStackTraceString(err));
                        return;
                    }
                    LogUtil.d(TAG, "upAllBook: %s", book.getTitle());
                    String sha = o.get("data").get("sha").asText();
                    Book book_db = bookDbHelper.findBookById(book.getId() + "");
                    book_db.putRemarkProperty("sha", sha);
                    bookDbHelper.updateBook(book_db);
                    mActivity.runOnUiThread(() -> {
                        UiUtils.showToast("同步成功《" + book.getTitle() + "》");
                    });
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mActivity.runOnUiThread(() -> {
            UiUtils.showToast("本地同步到云成功");
        });
    }

    private void downAllBook() {
        LoadingDialog loading = new LoadingDialog(mActivity);
        mActivity.runOnUiThread(() -> {
            loading.show();
        });
        bookNetHelper.cloudList(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                if (err != null) {
                    mActivity.runOnUiThread(() -> {
                        loading.dismiss();
                        UiUtils.showToast("云同步失败:" + err.getMessage());
                    });
                    return;
                }
                LogUtil.d(TAG, "call: 1 : %s", o);
                JsonNode data = o.get("data");
                List<JsonNode> nodeList = new ArrayList<>(data.size());
                for (int i = 0; i < data.size(); i++) {
                    nodeList.add(data.get(i));
                }
                nodeList.sort(new Comparator<JsonNode>() {
                    @Override
                    public int compare(JsonNode o1, JsonNode o2) {
                        String name1 = o1.get("name").asText();
                        String name2 = o2.get("name").asText();
                        boolean b1 = name1.endsWith(Common.book_metadata_suffix);
                        boolean b2 = name2.endsWith(Common.book_metadata_suffix);
                        return b1 && b2 ? name1.compareTo(name2) : b1 ? -1 : b2 ? 1 : 0;
                    }
                });
                for (int i = 0; i < nodeList.size(); i++) {
                    JsonNode item = nodeList.get(i);
                    String name = item.get("name").asText();
                    String sha = item.get("sha").asText();
                    if (name.endsWith(Common.log_suffix)) {
                        LogUtil.d(TAG, "ignore name : %s", name);
                        continue;
                    }
                    if (!name.endsWith(Common.book_metadata_suffix)) {
                        String id = name.split("\\-")[0];
                        Book book = bookDbHelper.findBookById(id);
                        if (book == null) {
                            LogUtil.d(TAG, "ignore name : %s", name);
                            continue;
                        }
                        File file = new File(book.getRemarkProperty("file_path"));
                        if (file.exists()) {
                            LogUtil.d(TAG, "download: %s exist.", book.getTitle());
                            continue;
                        }
                    }
                    final int idx = i;
                    CountDownLatch latch = new CountDownLatch(1);
                    bookNetHelper.cloudDownload(name, item.get("token").asText(), new DataCallback<byte[]>() {
                        @Override
                        public void call(byte[] bytes, Throwable err) {
                            try {
                                if (err != null) {
                                    LogUtil.e(TAG, "%s 云下载失败. %s", name, LogUtil.getStackTraceString(err));
                                    mActivity.runOnUiThread(() -> {
                                        UiUtils.showToast("错误:" + err.getMessage());
                                    });
                                    return;
                                }
                                if (name.endsWith(Common.book_metadata_suffix)) {
                                    JsonNode tree = JsonUtil.readTree(new String(bytes, StandardCharsets.UTF_8));
                                    LogUtil.d(TAG, "call: 2 : %d : %s", idx, tree);
                                    Book book = JsonUtil.convertValue(tree, new TypeReference<Book>() {
                                    });
                                    Book book_db = bookDbHelper.findBookById(book.getId() + "");
                                    if (book_db != null) {
                                        book_db.putRemarkProperty("sha", sha);
                                        bookDbHelper.updateBook(book_db);
                                        if (book.getBookReader() != null) {
                                            bookDbHelper.updateBookReaderByBookId(book.getBookReader());
                                        }
                                        LogUtil.d(TAG, "call: update book: %s", book_db.getTitle());
                                    } else {
                                        bookDbHelper.insertBook(book);
                                        if (book.getBookReader() != null) {
                                            bookDbHelper.insertBookReader(book.getBookReader());
                                        }
                                        LogUtil.d(TAG, "call: insert book: %s", book.getTitle());
                                    }
                                } else {
                                    String id = name.split("\\-")[0];
                                    Book book = bookDbHelper.findBookById(id);
                                    String file_path = book.getRemarkProperty("file_path");
                                    Files.write(new File(file_path).toPath(), bytes);
                                    LogUtil.d(TAG, "downloaded: %s", name);
                                    mActivity.runOnUiThread(() -> {
                                        UiUtils.showToast("云同步《" + name + "》成功");
                                    });
                                }
                            } catch (Exception e) {
                                LogUtil.e(TAG, "cloud download err. %s", LogUtil.getStackTraceString(e));
                            } finally {
                                latch.countDown();
                            }
                        }
                    });
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mActivity.runOnUiThread(() -> {
                    loading.dismiss();
                    UiUtils.showToast("云同步到本地完成");
                });

            }
        });
    }

    private void settings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void issues() {
        Intent intent = new Intent(getContext(), IssuesActivity.class);
        startActivity(intent);
    }

    private void donate() {
        Intent intent = new Intent(getContext(), VideoViewActivity2.class);
        intent.putExtra("playUrl", "https://http2.idingdang.org/donate");
        intent.putExtra("orientation", "v");
        startActivity(intent);
    }

    private void test() {
        LogUtil.d(TAG, "test: ");
        LoadingDialog loading = new LoadingDialog(mActivity);
        loading.show();
    }

    private Bitmap headBitmap(String name, int size, String colorHex) {

        name = name != null ? (name.length() > 2 ? name.substring(0, 2) : name) : "";

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        Rect rect = new Rect(0, 0, size, size);//画一个矩形
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.parseColor(colorHex));
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setAntiAlias(true);
        canvas.drawCircle(size / 2, size / 2, size / 2, rectPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(size / 3);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(name, rect.centerX(), baseLineY, textPaint);
        return bitmap;
    }

}