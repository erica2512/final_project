package com.landvibe.commalk.common

import android.app.Application
import com.landvibe.commalk.common.AppDatabase.Companion.instance

/*
    안드로이드의 기본 앱 state를 관리하는 객체
    Manifest에 <application> 태그 안에 name에 클래스를 지정하여 사용
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}