# lib

基于 `tmp/api.md` 中接口定义整理出来的 Android Library 模块。

## 功能范围

- 覆盖 `api.md` 中列出的主要 `eapi` 接口
- 提供认证头 `remix-userid` / `remix-userkey` 的统一封装
- 提供图书搜索、登录注册、个人资料、下载链接、站点信息等调用入口
- 提供 `Lib*` 命名的 model，便于上层直接使用强类型结果

## 使用示例

```java
LibApiClient client = new LibApiClient("https://your-lib-host/");
LibApiClient.Auth auth = new LibApiClient.Auth(123456, "user-key");

LibApiClient.SearchBooksRequest request = new LibApiClient.SearchBooksRequest()
        .setMessage("android")
        .addLanguage("english")
        .addExtension("epub")
        .setPage(1)
        .setLimit(10);

LibBooksListing result = client.searchBooks(auth, request);
```

## 说明

- 当前库优先保证接口覆盖面和可集成性
- model 已按 `api.md` 中已知字段创建，统一使用 `Lib` 前缀
- 对于 `api.md` 未展开的字段，model 会通过 `extra` 容纳额外返回内容
