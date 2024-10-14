package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;

/**
 * @author jsbxyyx
 * @since
 */
public class BookJavascript {

    private Context mContext;
    private BookDbHelper bookDbHelper;

    public BookJavascript(Context context) {
        mContext = context;
        bookDbHelper = new BookDbHelper(context);
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

}
