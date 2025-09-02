# XPlay

> 一个集成搜索、下载、阅读功能的多媒体 Android 应用

**Languages**: [English](README.md) | 中文

[![Stars](https://img.shields.io/github/stars/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/stargazers)
[![Forks](https://img.shields.io/github/forks/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/network)
[![Issues](https://img.shields.io/github/issues/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/issues)
[![License](https://img.shields.io/github/license/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-4.9-blue)](https://github.com/jsbxyyx/xplay/releases)

## ⚠️ 免责声明

**请仔细阅读以下免责声明：**

本项目发布的部分内容包括：文章、图片、书籍、音频、视频等搜集于互联网，仅供个人欣赏、学习之用。任何组织和个人不得公开传播或用于任何商业盈利用途，否则一切后果由该组织或个人承担。本站和制作者不承担任何法律及连带责任。

**重要提醒：**
- 🔴 **请自觉于下载后24小时内删除**
- 💡 **如果喜欢相关内容，请购买正版**
- ⚖️ **本应用仅提供技术实现，不提供任何内容资源**
- 📝 **用户使用本应用产生的任何法律问题与开发者无关**

---

## 📝 项目简介

XPlay 是一个基于 Android 平台的多功能媒体应用，提供以下核心功能：

- 📚 **电子书阅读** - 支持 EPUB、PDF 等格式的电子书阅读
- 🔍 **资源搜索** - 强大的搜索功能，帮你找到想要的内容
- 📥 **内容下载** - 便捷的下载管理
- 🖼️ **图片查看** - 优雅的图片浏览体验
- 📺 **视频播放** - 支持多种视频格式播放

## 🌟 主要特性

- ✅ 多格式支持：EPUB、PDF、图片、视频
- ✅ 现代化 Material Design UI
- ✅ 支持多架构：ARM64、ARM32、x86、x86_64
- ✅ 内置 GeckoView 浏览器引擎
- ✅ HTTP 服务器功能
- ✅ WebSocket 支持
- ✅ 免费开源

## 🚀 快速开始

### 系统要求

- **Android 版本**: Android 8.1 (API 27) 及以上
- **目标版本**: Android 13 (API 33)
- **架构支持**: ARM64、ARM32、x86、x86_64

### 开发环境

- Android Studio 2023.1.1+
- JDK 17
- Android Gradle Plugin 8.4.1
- Gradle 8.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/jsbxyyx/xplay.git
   cd xplay
   ```

2. **配置签名（可选）**
   ```bash
   # 设置环境变量或使用默认配置
   export KEY_STORE_FILE=your_keystore.jks
   export KEY_ALIAS=your_alias
   export KEY_STORE_PASSWORD=your_store_password
   export KEY_PASSWORD=your_key_password
   ```

3. **构建项目**
   ```bash
   ./gradlew assembleRelease
   ```

4. **安装到设备**
   ```bash
   ./gradlew installRelease
   ```

## 📱 应用截图

*待添加应用截图*

## 🔧 项目结构

```
xplay/
├── app/                    # 主应用模块
│   ├── src/main/java/     # Java 源代码
│   └── build.gradle       # 应用构建配置
├── gradle/                # Gradle Wrapper 文件
├── .github/               # GitHub Actions 配置
├── build.gradle           # 项目级构建配置
├── settings.gradle        # 项目设置
└── README.md             # 项目说明文档
```

## 🛠️ 技术栈

- **开发语言**: Java 17
- **UI 框架**: Android Material Design Components
- **网络库**: OkHttp 4.12.0
- **JSON 解析**: Jackson 2.16.1
- **图片加载**: Picasso 2.8
- **Web 服务**: NanoHTTPD 2.3.1
- **浏览器引擎**: GeckoView 121.0
- **WebSocket**: Java-WebSocket 1.5.3
- **权限管理**: XXPermissions 20.0

## 📖 使用指南

### 首次启动
1. 安装 APK 到 Android 设备
2. 授予必要的存储和网络权限
3. 打开应用开始使用

### 电子书阅读
1. 点击搜索或浏览本地文件
2. 选择 EPUB 或 PDF 文件
3. 享受流畅的阅读体验

### 资源搜索
1. 在搜索框输入关键词
2. 选择搜索类型（电子书/图片/视频/电视剧）
3. 浏览和预览搜索结果

### 媒体播放
1. 选择图片或视频文件
2. 使用内置播放器享受媒体内容
3. 支持手势控制和全屏播放

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

### 代码规范
- 使用 Java 17 语法
- 遵循 Android 开发最佳实践
- 添加适当的注释和文档

## 📄 开源协议

本项目采用 [GPL-2.0 License](LICENSE) 开源协议。

## 🔗 相关链接

- **项目主页**: [https://123571.xyz](https://123571.xyz)
- **问题反馈**: [Issues](https://github.com/jsbxyyx/xplay/issues)
- **发布版本**: [Releases](https://github.com/jsbxyyx/xplay/releases)

## ⭐ Star History

如果这个项目对你有帮助，请考虑给它一个 Star！

[![Star History Chart](https://api.star-history.com/svg?repos=jsbxyyx/xplay&type=Date)](https://star-history.com/#jsbxyyx/xplay&Date)

## 📞 联系方式

- **作者**: [jsbxyyx](https://github.com/jsbxyyx)
- **GitHub**: [@jsbxyyx](https://github.com/jsbxyyx)

---

**感谢使用 XPlay！如果你喜欢这个项目，别忘了给它一个 ⭐**
