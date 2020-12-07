package com.github.zedlol.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.zedlol.R
import com.github.zedlol.view.CircleProgressbar
import com.github.zedlol.view.CircleProgressbar.OnCountdownProgressListener
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {
    private var isClick = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }

    private fun init() {
        initCircleCountDown()
        my_count_down.setOutLineColor(Color.TRANSPARENT)
        my_count_down.setInCircleColor(Color.parseColor("#505559"))
        my_count_down.setProgressColor(Color.parseColor("#1BB079"))
        my_count_down.setProgressLineWidth(5)
        my_count_down.setProgressType(CircleProgressbar.ProgressType.COUNT)
        my_count_down.setTimeMillis(5000)
        my_count_down.reStart()

        my_count_down.setCountdownProgressListener(1, progressListener)
        my_count_down.setOnClickListener {
            isClick = true
//            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
            pushFLuMain()
        }
    }

    private fun initCircleCountDown() {
        my_count_down.let {
            it.setOutLineColor(Color.TRANSPARENT)
            it.setInCircleColor(Color.parseColor("#505559"))
            it.setProgressColor(Color.parseColor("#1BB079"))
            it.setProgressLineWidth(5)
            it.setProgressType(CircleProgressbar.ProgressType.COUNT)
            it.setTimeMillis(5000)
            it.reStart()

            it.setCountdownProgressListener(1, progressListener)
            it.setOnClickListener {
                isClick = true
//                val intent = Intent(this@SplashActivity, MainActivity::class.java)
//                startActivity(intent)
//                finish()
                pushFLuMain()
            }
        }
    }

    private fun pushFLuMain() {
//        DStack.getInstance()
//            .pushFlutterPage("/", null, FlutterContainerActivity::class.java)
//        finish()
        val intent = Intent(
            this@SplashActivity,
            FluMainActivity::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private val progressListener: OnCountdownProgressListener =
        object : OnCountdownProgressListener {
            override fun onProgress(what: Int, progress: Int) {
                if (what == 1 && progress == 100 && !isClick) {
//                    val intent = Intent(
//                        this@SplashActivity,
//                        MainActivity::class.java
//                    )
//                    startActivity(intent)
//                    finish()
                    pushFLuMain()
                }
            }
        }
}