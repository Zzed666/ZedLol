package com.github.zedlol

import android.content.Intent
import io.flutter.app.FlutterApplication
import tal.com.d_stack.DStack

class MyApplication : FlutterApplication() {
    override fun onCreate() {
        super.onCreate()
        DStack.getInstance().init(
            this
        ) { routerUrl: String, params: Map<String?, Any?>? ->

        }
    }
}