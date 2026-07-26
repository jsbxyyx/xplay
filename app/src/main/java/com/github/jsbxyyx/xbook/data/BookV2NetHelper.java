package com.github.jsbxyyx.xbook.data;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.ContentDispositionParser;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.ThreadUtils;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xplay.lib.LibApiClient;
import com.github.jsbxyyx.xplay.lib.model.LibBook;
import com.github.jsbxyyx.xplay.lib.model.LibBookSingle;
import com.github.jsbxyyx.xplay.lib.model.LibFileListing;
import com.github.jsbxyyx.xplay.lib.model.LibMinBook;
import com.github.jsbxyyx.xplay.lib.model.LibMinBookListing;
import com.github.jsbxyyx.xplay.lib.model.LibUserListing;
import com.github.jsbxyyx.xplay.lib.LibApiClient.SearchBooksRequest;
import com.github.jsbxyyx.xplay.lib.model.LibBooksListing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 新版书籍网络层。
 *
 * 使用 lib eapi，不修改旧 BookNetHelper，数据库模型继续复用旧 Book。
 */
public class BookV2NetHelper {

    private final LibApiClient libApiClient = new LibApiClient(Common.getXurlv2());

    private LibApiClient.Auth requireAuth() {
        java.util.Map<String, String> kv = Common.parseKv(SessionManager.getSession());
        String userId = kv.get(Common.serv_userid);
        String userKey = kv.get(Common.serv_userkey);
        if (Common.isBlank(userId) || Common.isBlank(userKey)) {
            throw new IllegalStateException("未登录");
        }
        return new LibApiClient.Auth(Integer.parseInt(userId), userKey);
    }

    public void login(String email, String password, DataCallback<LibUserListing> callback) {
        ThreadUtils.submit(() -> {
            try {
                LibUserListing listing = libApiClient.login(email, password, null);
                saveUserSession(listing);
                callback.call(listing, null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    public void register(String email, String password, String nickname, DataCallback<LibUserListing> callback) {
        ThreadUtils.submit(() -> {
            try {
                LibUserListing listing = libApiClient.registration(email, password, nickname);
                saveUserSession(listing);
                callback.call(listing, null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    public void sendCode(String action, String email, String password, String nickname,
                         String verifyCode, DataCallback<LibUserListing> callback) {
        ThreadUtils.submit(() -> {
            try {
                callback.call(libApiClient.sendConfirmationCode(action, email, nickname, verifyCode), null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    private void saveUserSession(LibUserListing listing) {
        if (listing == null || listing.user == null) {
            return;
        }
        String session = "remix_userid=" + listing.user.id + "; remix_userkey=" + listing.user.remix_userkey + ";";
        SessionManager.setSession(session);
        SPUtils.putData(Common.profile_nickname_key, listing.user.name);
        SPUtils.putData(Common.profile_email_key, listing.user.email);
    }

    public void profile(DataCallback<LibUserListing> callback) {
        ThreadUtils.submit(() -> {
            try {
                LibUserListing listing = libApiClient.userProfile(requireAuth());
                callback.call(listing, null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    public void detail(int id, String hash, DataCallback<Book> callback) {
        ThreadUtils.submit(() -> {
            try {
                LibBookSingle single = libApiClient.singleBook(requireAuth(), id, hash);
                callback.call(toBook(single == null ? null : single.book), null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    public void detailSuggest(int id, String hash, DataCallback<List<Book>> callback) {
        ThreadUtils.submit(() -> {
            try {
                LibMinBookListing listing = libApiClient.singleBookRecommendations(requireAuth(), id, hash);
                List<Book> result = new ArrayList<>();
                if (listing != null && listing.books != null) {
                    for (LibMinBook item : listing.books) {
                        result.add(toBook(item));
                    }
                }
                callback.call(result, null);
            } catch (Exception e) {
                callback.call(new ArrayList<>(), e);
            }
        });
    }

    public void download(int id, String hash, String destDir, String uid, DataCallback<File> callback, ProgressListener progressListener) {
        ThreadUtils.submit(() -> {
            try {
                LibFileListing listing =
                        libApiClient.getDownloadLink(requireAuth(), id, hash);
                if (listing == null || listing.file == null || listing.file.dlLink == null) {
                    throw new IllegalStateException("download link unavailable");
                }
                File dir = new File(destDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                final File[] resultFile = new File[1];
                libApiClient.downloadFileToStream(listing.file.dlLink, new LibApiClient.StreamDownloadCallback() {
                    private FileOutputStream output;
                    private long downloaded = 0;
                    private long total;

                    @Override
                    public void onStart(String contentDisposition, long contentLength) throws IOException {
                        this.total = contentLength;
                        String filename = extractFilename(contentDisposition, listing.file.dlLink);
                        File file = new File(dir, Common.isEmpty(uid) ? filename : uid + "-" + filename);
                        resultFile[0] = file;
                        output = new FileOutputStream(file);
                        
                        long m = Common.MAGIC ^ Common.MG_XOR;
                        ByteBuffer buf = ByteBuffer.allocate(8);
                        buf.putLong(m);
                        output.write(buf.array());
                    }

                    @Override
                    public void onData(byte[] buffer, int length) throws IOException {
                        downloaded += length;
                        if (progressListener != null) {
                            progressListener.onProgress(downloaded, total);
                        }
                        output.write(Common.xor(buffer, length, Common.MG_XOR));
                    }

                    @Override
                    public void onComplete() throws IOException {
                        closeOutput();
                    }

                    @Override
                    public void onError(Throwable e) {
                        closeOutput();
                    }

                    private void closeOutput() {
                        if (output != null) {
                            try {
                                output.close();
                            } catch (IOException ignored) {
                            }
                            output = null;
                        }
                    }
                });
                callback.call(resultFile[0], null);
            } catch (Exception e) {
                callback.call(null, e);
            }
        });
    }

    private String extractFilename(String contentDisposition, String url) {
        String filename = "";
        if (Common.isBlank(contentDisposition)) {
            int idx = url == null ? -1 : url.lastIndexOf("/");
            if (idx > -1) {
                filename = url.substring(idx + 1);
                int query = filename.indexOf('?');
                if (query > -1) {
                    filename = filename.substring(0, query);
                }
            }
        } else {
            filename = ContentDispositionParser.parse(contentDisposition);
        }
        return Common.isBlank(filename)
                ? "tmp-" + UUID.randomUUID().toString()
                : filename;
    }

    public void search(String keyword, int page, List<String> languages, List<String> extensions, DataCallback<List<Book>> callback) {
        ThreadUtils.submit(() -> {
            try {
                SearchBooksRequest request = new SearchBooksRequest()
                        .setMessage(keyword)
                        .setPage(page)
                        .setLanguages(languages)
                        .setExtensions(extensions);
                LibBooksListing listing = libApiClient.searchBooks(requireAuth(), request);
                List<Book> result = new ArrayList<>();
                if (listing != null && listing.books != null) {
                    for (LibBook item : listing.books) {
                        result.add(toBook(item));
                    }
                }
                callback.call(result, null);
            } catch (Exception e) {
                callback.call(new ArrayList<>(), e);
            }
        });
    }

    private Book toBook(LibBook src) {
        Book book = new Book();
        if (src == null) {
            return book;
        }
        book.setBid(String.valueOf(src.id));
        book.setHash(src.hash);
        book.setIsbn(src.identifier);
        book.setAuthors(src.author);
        book.setTitle(src.title);
        book.setDescription(src.description);
        book.setCoverImage(src.cover);
        book.setLanguage(src.language);
        book.setFile(src.extension + " " + src.filesizeString);
        book.setContent_type(Book.content_type_book);
        book.setPublisher(src.publisher);
        book.setYear(String.valueOf(src.year));
        book.setDownloadUrl(src.dl);
        return book;
    }

    private Book toBook(LibMinBook src) {
        Book book = new Book();
        if (src == null) {
            return book;
        }
        book.setBid(String.valueOf(src.id));
        book.setTitle(src.title);
        book.setAuthors(src.author);
        book.setCoverImage(src.cover);
        book.setContent_type(Book.content_type_book);
        return book;
    }
}
