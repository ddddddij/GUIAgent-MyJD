package com.example.myjd

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application类
 * 使用@HiltAndroidApp注解启用Hilt依赖注入
 */
@HiltAndroidApp
class MyJDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("MyJDApplication", "Application created with Hilt")
    }
}
