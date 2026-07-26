package com.github.jsbxyyx.xplay.lib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jsbxyyx.xplay.lib.model.LibBookFormats;
import com.github.jsbxyyx.xplay.lib.model.LibBookSingle;
import com.github.jsbxyyx.xplay.lib.model.LibBooksExtensions;
import com.github.jsbxyyx.xplay.lib.model.LibBooksLanguages;
import com.github.jsbxyyx.xplay.lib.model.LibBooksListing;
import com.github.jsbxyyx.xplay.lib.model.LibDonationsListing;
import com.github.jsbxyyx.xplay.lib.model.LibFileListing;
import com.github.jsbxyyx.xplay.lib.model.LibHost;
import com.github.jsbxyyx.xplay.lib.model.LibHostsListing;
import com.github.jsbxyyx.xplay.lib.model.LibInfo;
import com.github.jsbxyyx.xplay.lib.model.LibMinBookListing;
import com.github.jsbxyyx.xplay.lib.model.LibServiceInfo;
import com.github.jsbxyyx.xplay.lib.model.LibStatus;
import com.github.jsbxyyx.xplay.lib.model.LibTypedBooksListing;
import com.github.jsbxyyx.xplay.lib.model.LibUserListing;
import com.github.jsbxyyx.xplay.lib.model.plans.LibPlanLimitListing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Typed Android client built from tmp/api.md.
 */
public class LibApiClient {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private final HttpUrl baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public LibApiClient(@NonNull String baseUrl) {
        this(baseUrl, (LibErrorListener) null);
    }

    public LibApiClient(@NonNull String baseUrl, @Nullable LibErrorListener errorListener) {
        this(baseUrl, defaultClient(errorListener), defaultMapper());
    }

    public LibApiClient(
            @NonNull String baseUrl,
            @NonNull OkHttpClient client,
            @NonNull ObjectMapper mapper
    ) {
        this.baseUrl = requireBaseUrl(baseUrl);
        this.client = Objects.requireNonNull(client, "client == null");
        this.mapper = Objects.requireNonNull(mapper, "mapper == null");
    }

    @NonNull
    public LibBooksListing searchBooks(@NonNull Auth auth, @NonNull SearchBooksRequest request) throws IOException {
        FormBody.Builder form = new FormBody.Builder();
        addOptionalField(form, "message", request.getMessage());
        addOptionalField(form, "yearFrom", request.getYearFrom());
        addOptionalField(form, "yearTo", request.getYearTo());
        addRepeatedFields(form, "languages[]", request.getLanguages());
        addRepeatedFields(form, "extensions[]", request.getExtensions());
        addOptionalField(form, "e", request.getExactMatching());
        addOptionalField(form, "page", request.getPage());
        addOptionalField(form, "limit", request.getLimit());
        addOptionalField(form, "order", request.getOrder());
        return execute(buildAuthedRequest(auth, "/eapi/book/search", "POST", form.build()), LibBooksListing.class);
    }

    @NonNull
    public LibBooksExtensions getExtensions() throws IOException {
        return execute(buildRequest("/eapi/info/extensions"), LibBooksExtensions.class);
    }

    @NonNull
    public LibBooksLanguages getLanguages() throws IOException {
        return execute(buildRequest("/eapi/info/languages"), LibBooksLanguages.class);
    }

    @NonNull
    public LibUserListing tokenSignIn(@NonNull String idToken, @NonNull String name) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("id_token", idToken)
                .add("name", name)
                .build();
        return execute(buildRequest("/eapi/user/token-sign-in", "POST", body), LibUserListing.class);
    }

    @NonNull
    public LibUserListing registration(@NonNull String email, @NonNull String password, @NonNull String nickname) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("name", nickname)
                .build();
        return execute(buildRequest("/eapi/user/registration", "POST", body), LibUserListing.class);
    }

    @NonNull
    public LibUserListing registrationResend(@NonNull String email, @NonNull String password, @NonNull String nickname) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("name", nickname)
                .build();
        return execute(buildRequest("/eapi/user/email/confirmation/resend", "POST", body), LibUserListing.class);
    }

    @NonNull
    public LibUserListing registrationConfirm(
            @NonNull String email,
            @NonNull String password,
            @NonNull String nickname,
            @NonNull String code
    ) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("name", nickname)
                .add("code", code)
                .build();
        return execute(buildRequest("/eapi/user/email/confirmation", "POST", body), LibUserListing.class);
    }

    @NonNull
    public LibUserListing login(@Nullable String email, @Nullable String password, @Nullable String token) throws IOException {
        FormBody.Builder body = new FormBody.Builder();
        addOptionalField(body, "email", email);
        addOptionalField(body, "password", password);
        addOptionalField(body, "auth_token", token);
        return execute(buildRequest("/eapi/user/login", "POST", body.build()), LibUserListing.class);
    }

    @NonNull
    public LibUserListing loginPersonal(
            @NonNull String url,
            @Nullable String action,
            @Nullable String password,
            @Nullable String token
    ) throws IOException {
        FormBody.Builder body = new FormBody.Builder();
        addOptionalField(body, "action", action);
        addOptionalField(body, "password", password);
        addOptionalField(body, "auth_token", token);
        Request request = new Request.Builder()
                .url(HttpUrl.get(url))
                .post(body.build())
                .build();
        return execute(request, LibUserListing.class);
    }

    @NonNull
    public LibUserListing recoverPassword(@NonNull String email, @Nullable String verifyCode) throws IOException {
        FormBody.Builder body = new FormBody.Builder()
                .add("email", email);
        addOptionalField(body, "verifyCode", verifyCode);
        return execute(buildRequest("/eapi/user/password-recovery", "POST", body.build()), LibUserListing.class);
    }

    @NonNull
    public LibMinBookListing booksMostPopular(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/most-popular"), LibMinBookListing.class);
    }

    @NonNull
    public LibMinBookListing booksRecentlyAdded(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/recently"), LibMinBookListing.class);
    }

    @NonNull
    public LibMinBookListing booksRecommendations(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/book/recommended"), LibMinBookListing.class);
    }

    @NonNull
    public LibMinBookListing singleBookRecommendations(@NonNull Auth auth, int id, @NonNull String hash) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/" + id + "/" + hash + "/similar"), LibMinBookListing.class);
    }

    @NonNull
    public LibBookSingle singleBook(@NonNull Auth auth, int id, @NonNull String hash) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/" + id + "/" + hash), LibBookSingle.class);
    }

    @NonNull
    public LibStatus saveBook(@NonNull Auth auth, int bookId) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/book/" + bookId + "/save"), LibStatus.class);
    }

    @NonNull
    public LibStatus deleteSavedBook(@NonNull Auth auth, int bookId) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/book/" + bookId + "/unsave"), LibStatus.class);
    }

    @NonNull
    public LibStatus deleteDownloadedBook(@NonNull Auth auth, int bookId) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/book/" + bookId + "/delete"), LibStatus.class);
    }

    @NonNull
    public LibUserListing userProfile(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/profile"), LibUserListing.class);
    }

    @NonNull
    public LibBookFormats fileFormats(@NonNull Auth auth, int id, @NonNull String hash) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/" + id + "/" + hash + "/formats"), LibBookFormats.class);
    }

    @NonNull
    public LibStatus sendToMail(@NonNull Auth auth, int id, @NonNull String hash, @NonNull String type) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/" + id + "/" + hash + "/send-to-" + type), LibStatus.class);
    }

    @NonNull
    public LibFileListing getDownloadLink(@NonNull Auth auth, int id, @NonNull String hash) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/book/" + id + "/" + hash + "/file"), LibFileListing.class);
    }

    @NonNull
    public LibTypedBooksListing getSavedBooks(@NonNull Auth auth, @Nullable PageRequest request) throws IOException {
        return execute(buildAuthedRequest(auth, buildPageUrl("/eapi/user/book/saved", request)), LibTypedBooksListing.class);
    }

    @NonNull
    public LibTypedBooksListing getDownloadedBooks(@NonNull Auth auth, @Nullable PageRequest request) throws IOException {
        return execute(buildAuthedRequest(auth, buildPageUrl("/eapi/user/book/downloaded", request)), LibTypedBooksListing.class);
    }

    @NonNull
    public LibPlanLimitListing getPlanLimitations() throws IOException {
        return execute(buildRequest("/eapi/info/plans"), LibPlanLimitListing.class);
    }

    @NonNull
    public LibUserListing updateProfile(
            @NonNull Auth auth,
            @Nullable String email,
            @Nullable String password,
            @Nullable String name,
            @Nullable String kindleEmail,
            @Nullable String verifyCode
    ) throws IOException {
        FormBody.Builder body = new FormBody.Builder();
        addOptionalField(body, "email", email);
        addOptionalField(body, "password", password);
        addOptionalField(body, "name", name);
        addOptionalField(body, "kindle_email", kindleEmail);
        addOptionalField(body, "verifyCode", verifyCode);
        return execute(buildAuthedRequest(auth, "/eapi/user/update", "POST", body.build()), LibUserListing.class);
    }

    @NonNull
    public LibDonationsListing getDonations(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/donations"), LibDonationsListing.class);
    }

    @NonNull
    public LibStatus resendConfMail(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/email/confirmation/resend", "POST", emptyJsonBody()), LibStatus.class);
    }

    @NonNull
    public LibUserListing sendConfirmationCode(
            @NonNull String verifyAction,
            @NonNull String email,
            @NonNull String name,
            @Nullable String verifyCode
    ) throws IOException {
        FormBody.Builder body = new FormBody.Builder()
                .add("verifyAction", verifyAction)
                .add("email", email)
                .add("name", name);
        addOptionalField(body, "verifyCode", verifyCode);
        return execute(buildRequest("/eapi/user/send-code", "POST", body.build()), LibUserListing.class);
    }

    @NonNull
    public LibHostsListing getHosts(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/info/domains"), LibHostsListing.class);
    }

    @NonNull
    public List<LibHost> getHostsByUrl(@NonNull Auth auth, @NonNull String url) throws IOException {
        Request request = new Request.Builder()
                .url(HttpUrl.get(url))
                .header("remix-userid", String.valueOf(auth.getUserId()))
                .header("remix-userkey", auth.getUserKey())
                .get()
                .build();
        return execute(request, new TypeReference<List<LibHost>>() { });
    }

    @NonNull
    public LibInfo getSiteInfo(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/info"), LibInfo.class);
    }

    @NonNull
    public LibStatus onBannerHide(@NonNull Auth auth) throws IOException {
        return execute(buildAuthedRequest(auth, "/eapi/user/hide-banner"), LibStatus.class);
    }

    public interface StreamDownloadCallback {
        void onStart(String filename, long contentLength) throws IOException;

        void onData(byte[] buffer, int length) throws IOException;

        void onComplete() throws IOException;

        void onError(Throwable e);
    }

    public static class FileDownload {
        public final byte[] data;
        public final String filename;

        public FileDownload(byte[] data, String filename) {
            this.data = data;
            this.filename = filename;
        }
    }

    @NonNull
    public byte[] downloadFile(@NonNull String fileUrl) throws IOException {
        return downloadFileWithMetadata(fileUrl).data;
    }

    @NonNull
    public FileDownload downloadFileWithMetadata(@NonNull String fileUrl) throws IOException {
        Request request = new Request.Builder()
                .url(HttpUrl.get(fileUrl))
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new LibApiException(response.code(), readBodySafely(response.body()));
            }
            ResponseBody body = response.body();
            return new FileDownload(
                    body == null ? new byte[0] : body.bytes(),
                    response.header("Content-Disposition")
            );
        }
    }

    public void downloadFileToStream(@NonNull String fileUrl,
                                     @NonNull StreamDownloadCallback callback) throws IOException {
        Request request = new Request.Builder()
                .url(HttpUrl.get(fileUrl))
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new LibApiException(response.code(), readBodySafely(response.body()));
            }
            ResponseBody body = response.body();
            callback.onStart(
                    response.header("Content-Disposition"),
                    body == null ? 0 : body.contentLength()
            );
            if (body != null) {
                try (InputStream input = body.byteStream()) {
                    byte[] buffer = new byte[8192];
                    int n;
                    while ((n = input.read(buffer)) != -1) {
                        callback.onData(buffer, n);
                    }
                }
            }
            callback.onComplete();
        } catch (Throwable e) {
            callback.onError(e);
        }
    }

    @NonNull
    public LibServiceInfo getServiceInfo() throws IOException {
        return execute(buildRequest("/eapi/info/service"), LibServiceInfo.class);
    }

    private Request buildRequest(@NonNull String relativePath) {
        return buildRequest(relativePath, "GET", null);
    }

    private Request buildRequest(@NonNull String relativePath, @NonNull String method, @Nullable RequestBody body) {
        HttpUrl url = resolve(relativePath);
        Request.Builder builder = new Request.Builder().url(url);
        if ("POST".equals(method)) {
            builder.post(body != null ? body : emptyJsonBody());
        } else {
            builder.get();
        }
        return builder.build();
    }

    private Request buildAuthedRequest(@NonNull Auth auth, @NonNull String relativePath) {
        return buildAuthedRequest(auth, relativePath, "GET", null);
    }

    private Request buildAuthedRequest(@NonNull Auth auth, @NonNull HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .header("remix-userid", String.valueOf(auth.getUserId()))
                .header("remix-userkey", auth.getUserKey())
                .get()
                .build();
    }

    private Request buildAuthedRequest(
            @NonNull Auth auth,
            @NonNull String relativePath,
            @NonNull String method,
            @Nullable RequestBody body
    ) {
        HttpUrl url = resolve(relativePath);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("remix-userid", String.valueOf(auth.getUserId()))
                .header("remix-userkey", auth.getUserKey());
        if ("POST".equals(method)) {
            builder.post(body != null ? body : emptyJsonBody());
        } else {
            builder.get();
        }
        return builder.build();
    }

    @NonNull
    private <T> T execute(@NonNull Request request, @NonNull Class<T> clazz) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String body = readBodySafely(response.body());
            if (!response.isSuccessful()) {
                throw new LibApiException(response.code(), body);
            }
            if (body == null || body.isBlank()) {
                throw new IOException("Empty response body for " + request.url());
            }
            return mapper.readValue(body, clazz);
        }
    }

    @NonNull
    private <T> T execute(@NonNull Request request, @NonNull TypeReference<T> typeReference) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String body = readBodySafely(response.body());
            if (!response.isSuccessful()) {
                throw new LibApiException(response.code(), body);
            }
            if (body == null || body.isBlank()) {
                throw new IOException("Empty response body for " + request.url());
            }
            return mapper.readValue(body, typeReference);
        }
    }

    private int executeCode(@NonNull Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return response.code();
        }
    }

    private static void addOptionalField(@NonNull FormBody.Builder builder, @NonNull String key, @Nullable Object value) {
        if (value != null) {
            builder.add(key, String.valueOf(value));
        }
    }

    private static void addRepeatedFields(@NonNull FormBody.Builder builder, @NonNull String key, @Nullable List<String> values) {
        if (values == null) {
            return;
        }
        for (String value : values) {
            if (value != null) {
                builder.add(key, value);
            }
        }
    }

    @NonNull
    private HttpUrl buildPageUrl(@NonNull String relativePath, @Nullable PageRequest request) {
        HttpUrl.Builder builder = resolve(relativePath).newBuilder();
        if (request != null) {
            addOptionalQuery(builder, "order", request.getOrder());
            addOptionalQuery(builder, "page", request.getPage());
            addOptionalQuery(builder, "limit", request.getLimit());
        }
        return builder.build();
    }

    private static void addOptionalQuery(@NonNull HttpUrl.Builder builder, @NonNull String name, @Nullable Object value) {
        if (value != null) {
            builder.addQueryParameter(name, String.valueOf(value));
        }
    }

    @NonNull
    private HttpUrl resolve(@NonNull String relativePath) {
        String normalized = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        HttpUrl resolved = baseUrl.resolve(normalized);
        if (resolved == null) {
            throw new IllegalArgumentException("Unable to resolve path: " + relativePath);
        }
        return resolved;
    }

    @NonNull
    private static RequestBody emptyJsonBody() {
        return RequestBody.create("{}", JSON_MEDIA_TYPE);
    }

    @Nullable
    private static String readBodySafely(@Nullable ResponseBody body) throws IOException {
        return body == null ? null : body.string();
    }

    @NonNull
    private static HttpUrl requireBaseUrl(@NonNull String baseUrl) {
        String normalized = Objects.requireNonNull(baseUrl, "baseUrl == null");
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        HttpUrl url = HttpUrl.get(normalized);
        if (url.pathSegments().isEmpty()) {
            throw new IllegalArgumentException("Invalid baseUrl: " + baseUrl);
        }
        return url;
    }

    @NonNull
    private static OkHttpClient defaultClient(@Nullable LibErrorListener errorListener) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new LibHeaderInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        if (errorListener != null) {
            builder.addInterceptor(new LibErrorInterceptor(errorListener));
        }
        return builder.build();
    }

    @NonNull
    private static ObjectMapper defaultMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static final class Auth {
        private final int userId;
        private final String userKey;

        public Auth(int userId, @NonNull String userKey) {
            this.userId = userId;
            this.userKey = Objects.requireNonNull(userKey, "userKey == null");
        }

        public int getUserId() {
            return userId;
        }

        @NonNull
        public String getUserKey() {
            return userKey;
        }
    }

    public static class PageRequest {
        @Nullable
        private String order;
        @Nullable
        private Integer page;
        @Nullable
        private Integer limit;

        @Nullable
        public String getOrder() {
            return order;
        }

        @NonNull
        public PageRequest setOrder(@Nullable String order) {
            this.order = order;
            return this;
        }

        @Nullable
        public Integer getPage() {
            return page;
        }

        @NonNull
        public PageRequest setPage(@Nullable Integer page) {
            this.page = page;
            return this;
        }

        @Nullable
        public Integer getLimit() {
            return limit;
        }

        @NonNull
        public PageRequest setLimit(@Nullable Integer limit) {
            this.limit = limit;
            return this;
        }
    }

    public static final class SearchBooksRequest extends PageRequest {
        @Nullable
        private String message;
        @Nullable
        private Integer yearFrom;
        @Nullable
        private Integer yearTo;
        @Nullable
        private List<String> languages;
        @Nullable
        private List<String> extensions;
        @Nullable
        private Integer exactMatching;

        @Nullable
        public String getMessage() {
            return message;
        }

        @NonNull
        public SearchBooksRequest setMessage(@Nullable String message) {
            this.message = message;
            return this;
        }

        @Nullable
        public Integer getYearFrom() {
            return yearFrom;
        }

        @NonNull
        public SearchBooksRequest setYearFrom(@Nullable Integer yearFrom) {
            this.yearFrom = yearFrom;
            return this;
        }

        @Nullable
        public Integer getYearTo() {
            return yearTo;
        }

        @NonNull
        public SearchBooksRequest setYearTo(@Nullable Integer yearTo) {
            this.yearTo = yearTo;
            return this;
        }

        @Nullable
        public List<String> getLanguages() {
            return languages;
        }

        @NonNull
        public SearchBooksRequest setLanguages(@Nullable List<String> languages) {
            this.languages = languages == null ? null : new ArrayList<>(languages);
            return this;
        }

        @NonNull
        public SearchBooksRequest addLanguage(@NonNull String language) {
            if (languages == null) {
                languages = new ArrayList<>();
            }
            languages.add(language);
            return this;
        }

        @Nullable
        public List<String> getExtensions() {
            return extensions;
        }

        @NonNull
        public SearchBooksRequest setExtensions(@Nullable List<String> extensions) {
            this.extensions = extensions == null ? null : new ArrayList<>(extensions);
            return this;
        }

        @NonNull
        public SearchBooksRequest addExtension(@NonNull String extension) {
            if (extensions == null) {
                extensions = new ArrayList<>();
            }
            extensions.add(extension);
            return this;
        }

        @Nullable
        public Integer getExactMatching() {
            return exactMatching;
        }

        @NonNull
        public SearchBooksRequest setExactMatching(@Nullable Integer exactMatching) {
            this.exactMatching = exactMatching;
            return this;
        }

        @Override
        @NonNull
        public SearchBooksRequest setOrder(@Nullable String order) {
            super.setOrder(order);
            return this;
        }

        @Override
        @NonNull
        public SearchBooksRequest setPage(@Nullable Integer page) {
            super.setPage(page);
            return this;
        }

        @Override
        @NonNull
        public SearchBooksRequest setLimit(@Nullable Integer limit) {
            super.setLimit(limit);
            return this;
        }
    }
}
