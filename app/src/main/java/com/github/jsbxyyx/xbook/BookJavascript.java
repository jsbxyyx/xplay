package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.media.MediaPlayer;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;
import com.github.jsbxyyx.xbook.tts.TTSClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * @author jsbxyyx
 * @since
 */
public class BookJavascript {

    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private WebView mWebView;
    private BookDbHelper bookDbHelper;
    private MediaPlayer mediaPlayer;

    public BookJavascript(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
        bookDbHelper = BookDbHelper.getInstance();
    }

    @JavascriptInterface
    public void report(String bookId, String current, String pages) {
        LogUtil.d(getClass().getSimpleName(), "report: %s : %s / %s", bookId, current, pages);
        Book book = bookDbHelper.findBookById(bookId);
        BookReader bookReader = bookDbHelper.findBookReaderByBookId(Long.valueOf(bookId));
        if (bookReader == null) {
            bookReader = new BookReader();
            bookReader.setId(IdUtil.nextId() + "");
            bookReader.setBookId(bookId);
            bookReader.setCur(current);
            bookReader.setPages(pages);
            bookReader.setUser(book.getUser());
            bookReader.setRemark("");
            bookDbHelper.insertBookReader(bookReader);
        } else {
            bookReader.setCur(current);
            bookReader.setPages(pages);
            bookDbHelper.updateBookReaderByBookId(bookReader);
        }
    }

    @JavascriptInterface
    public void toast(String text) {
        UiUtils.showToast(text);
    }

    @JavascriptInterface
    public String suid() {
        String s = SessionManager.getSession();
        Map<String, String> kvMap = Common.parseKv(s);
        return kvMap.getOrDefault(Common.serv_userid, "");
    }

    @JavascriptInterface
    public String vc() {
        return UiUtils.getVersionCode() + "";
    }

    @JavascriptInterface
    public String vn() {
        return UiUtils.getVersionName();
    }

    @JavascriptInterface
    public String play(String text, String callback) {
        try {
            ByteArrayOutputStream output = TTSClient.audioByText(text, null, null, null, null, null);
            if (output.size() <= 0) {
                UiUtils.showToast("TTS failed, empty.");
                return "-1";
            }
            File tempMp3 = File.createTempFile("tts", ".mp3");
            try (FileOutputStream fos = new FileOutputStream(tempMp3)) {
                fos.write(output.toByteArray());
            }
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(tempMp3.getAbsolutePath());

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.setOnCompletionListener((mp) -> {
                if (!Common.isBlank(callback)) {
                    mWebView.post(() -> {
                        mWebView.loadUrl("javascript:" + callback + "()");
                    });
                }
                boolean b = tempMp3.delete();
                if (!b) {
                    tempMp3.deleteOnExit();
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                LogUtil.e(TAG, "ErrorListener:%d, %d", what, extra);
                boolean b = tempMp3.delete();
                if (!b) {
                    tempMp3.deleteOnExit();
                }
                return true;
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "tts error. %s", LogUtil.getStackTraceString(e));
            UiUtils.showToast("TTS failed, error.");
            return "-1";
        }
        return "0";
    }

}
