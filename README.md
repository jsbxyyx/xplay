# XPlay

> A comprehensive multimedia Android app for searching, downloading, and reading digital content

**Languages**: English | [中文](README_CN.md)

[![Stars](https://img.shields.io/github/stars/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/stargazers)
[![Forks](https://img.shields.io/github/forks/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/network)
[![Issues](https://img.shields.io/github/issues/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/issues)
[![License](https://img.shields.io/github/license/jsbxyyx/xplay)](https://github.com/jsbxyyx/xplay/blob/main/LICENSE)
[![Version](https://img.shields.io/badge/version-4.9-blue)](https://github.com/jsbxyyx/xplay/releases)

## ⚠️ Disclaimer

**Please read the following disclaimer carefully:**

Some content published by this project includes: articles, images, books, audio, videos, etc., which are collected from the Internet and are for personal appreciation and learning purposes only. No organization or individual may publicly distribute or use them for any commercial profit purposes, otherwise all consequences shall be borne by the organization or individual. This site and the developers do not assume any legal and joint liability.

**Important Reminders:**
- 🔴 **Please consciously delete within 24 hours after downloading**
- 💡 **If you like the content, please purchase the genuine version**
- ⚖️ **This app only provides technical implementation and does not provide any content resources**
- 📝 **Any legal issues arising from users' use of this app are not related to the developers**

---

## 📝 Description

XPlay is a feature-rich Android multimedia application that provides comprehensive functionality for:

- 📚 **eBook Reading** - Support for EPUB, PDF and other eBook formats
- 🔍 **Content Search** - Powerful search capabilities to find desired content
- 📥 **Download Manager** - Convenient download management system
- 🖼️ **Image Viewer** - Elegant image browsing experience
- 📺 **Video Player** - Support for multiple video formats and TV series

## 🌟 Key Features

- ✅ Multi-format support: EPUB, PDF, images, videos
- ✅ Modern Material Design UI
- ✅ Multi-architecture support: ARM64, ARM32, x86, x86_64
- ✅ Built-in GeckoView browser engine
- ✅ HTTP server functionality
- ✅ WebSocket support
- ✅ Free and open source

## 🚀 Getting Started

### System Requirements

- **Android Version**: Android 8.1 (API 27) and above
- **Target Version**: Android 13 (API 33)
- **Architecture Support**: ARM64, ARM32, x86, x86_64

### Development Environment

- Android Studio 2023.1.1+
- JDK 17
- Android Gradle Plugin 8.4.1
- Gradle 8.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/jsbxyyx/xplay.git
   cd xplay
   ```

2. **Configure signing (optional)**
   ```bash
   # Set environment variables or use default configuration
   export KEY_STORE_FILE=your_keystore.jks
   export KEY_ALIAS=your_alias
   export KEY_STORE_PASSWORD=your_store_password
   export KEY_PASSWORD=your_key_password
   ```

3. **Build the project**
   ```bash
   ./gradlew assembleRelease
   ```

4. **Install to device**
   ```bash
   ./gradlew installRelease
   ```

## 📱 Screenshots

*Screenshots will be added soon*

## 🔧 Project Structure

```
xplay/
├── app/                    # Main application module
│   ├── src/main/java/     # Java source code
│   └── build.gradle       # App build configuration
├── gradle/                # Gradle Wrapper files
├── .github/               # GitHub Actions configuration
├── build.gradle           # Project-level build configuration
├── settings.gradle        # Project settings
└── README.md             # Project documentation
```

## 🛠️ Tech Stack

- **Language**: Java 17
- **UI Framework**: Android Material Design Components
- **Networking**: OkHttp 4.12.0
- **JSON Parsing**: Jackson 2.16.1
- **Image Loading**: Picasso 2.8
- **Web Server**: NanoHTTPD 2.3.1
- **Browser Engine**: GeckoView 121.0
- **WebSocket**: Java-WebSocket 1.5.3
- **Permissions**: XXPermissions 20.0

## 📖 Usage Guide

### First Launch
1. Install the APK on your Android device
2. Grant necessary storage and network permissions
3. Open the app and start exploring

### eBook Reading
1. Search for books or browse local files
2. Select EPUB or PDF files
3. Enjoy a smooth reading experience

### Content Search
1. Enter keywords in the search box
2. Choose search type (books/images/videos/TV series)
3. Browse and preview search results

### Media Playback
1. Select image or video files
2. Use the built-in player to enjoy media content
3. Support gesture controls and fullscreen playback

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards
- Use Java 17 syntax
- Follow Android development best practices
- Add appropriate comments and documentation

## 📄 License

This project is licensed under the [GPL-2.0 License](LICENSE).

## 🔗 Links

- **Homepage**: [https://123571.xyz](https://123571.xyz)
- **Issue Tracker**: [Issues](https://github.com/jsbxyyx/xplay/issues)
- **Releases**: [Releases](https://github.com/jsbxyyx/xplay/releases)

## ⭐ Star History

If this project helps you, please consider giving it a star!

[![Star History Chart](https://api.star-history.com/svg?repos=jsbxyyx/xplay&type=Date)](https://star-history.com/#jsbxyyx/xplay&Date)

## 📞 Contact

- **Author**: [jsbxyyx](https://github.com/jsbxyyx)
- **GitHub**: [@jsbxyyx](https://github.com/jsbxyyx)

---

**Thank you for using XPlay! If you like this project, don't forget to give it a ⭐**
