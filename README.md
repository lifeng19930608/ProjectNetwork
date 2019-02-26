# ProjectNetwork
# 封装的okhttp的网络请求框架

build.gradle中添加
allprojects {
    repositories {
        ...
        maven {
            url 'https://jitpack.io'
        }
    }
}

app.gradle中添加
implementation 'com.github.li123456789feng:ProjectNetwork:v1.0'
