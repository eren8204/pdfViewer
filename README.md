# PDF Viewer Android Library

[![GitHub](https://img.shields.io/badge/github-eren8204/pdfViewer-blue?style=for-the-badge)](https://github.com/eren8204/pdfViewer) 
[![JitPack](https://img.shields.io/badge/JitPack-v1.0.0-blue?style=for-the-badge)](https://jitpack.io/#eren8204/pdfViewer/v1.0.0)
[![License](https://img.shields.io/github/license/eren8204/pdfViewer?style=for-the-badge)](LICENSE)

A simple and easy-to-use Android library to render PDF files in your app. Just add the dependency, and you're ready to display PDFs seamlessly.

---

## Features

- 📄 Render PDF files from assets or URL
- ⚡ Lightweight and fast
- 🛠 Easy integration
- 🎨 Fully customizable appearance

---

## Installation

### Step 1: Add JitPack repository

Add the JitPack repository in your root `settings.gradle`:
```
gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

Add this in your module-level build.gradle:
```
dependencies {
    implementation 'com.github.eren8204:pdfViewer:v1.0.0'
}
```

## Usage
# Display PDF from assets
```
PDFViewer pdfViewer = findViewById(R.id.pdfViewer);
pdfViewer.fromAsset("sample.pdf").load();
```

# Display PDF from URL
```
PDFViewer pdfViewer = findViewById(R.id.pdfViewer);
pdfViewer.fromUrl("https://www.example.com/sample.pdf").load();
```
# Contributing
Contributions are welcome! Please feel free to submit issues or pull requests.
