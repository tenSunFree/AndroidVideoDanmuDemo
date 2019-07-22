package com.home.androidvideodanmudemo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener
import com.dueeeke.videoplayer.player.VideoView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var danmukuVideoView: DanmukuVideoView? = null
    private val URL_VOD = "http://vfx.mtime.cn/Video/2019/01/15/mp4/190115180834604595.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) // 隐藏状态栏
        setContentView(R.layout.activity_main)
        initializeDanmukuVideoView()
        initializeEditText()
    }

    private fun initializeDanmukuVideoView() {
        danmukuVideoView = findViewById(R.id.danmukuVideoView)
        val standardVideoController = StandardVideoController(this)
        standardVideoController.setTitle("androidvideodanmudemo")
        danmukuVideoView!!.setVideoController(standardVideoController)
        danmukuVideoView!!.setUrl(URL_VOD)
        danmukuVideoView!!.addOnVideoViewStateChangeListener(object : OnVideoViewStateChangeListener {
            override fun onPlayerStateChanged(playerState: Int) {}
            override fun onPlayStateChanged(playState: Int) {
                if (playState == VideoView.STATE_PREPARED) {
                    simulateDanmu()
                }
            }
        })
        danmukuVideoView!!.start()
    }

    private fun initializeEditText() {
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { // 得到焦点时
                editText.setHintTextColor(resources.getColor(R.color.activity_main_edit_text_has_focus))
                frameLayout.background = getDrawable(R.drawable.background_activity_main_send_danmu_white)
            } else { // 失去焦点时
                editText.setHintTextColor(resources.getColor(R.color.activity_main_edit_text_no_focus))
                frameLayout.background = getDrawable(R.drawable.background_activity_main_send_danmu_gray)
            }
        }
        editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) { // 判断是否actionDone
                    if (editText.text.isNotEmpty()) {
                        danmukuVideoView!!.addDanmaku(editText.text.toString(), true)
                        editText.clearFocus()
                        editText.text.clear()
                        hideSoftKeyboard(textView)
                        editText.setHintTextColor(resources.getColor(R.color.activity_main_edit_text_no_focus))
                        frameLayout.background = getDrawable(R.drawable.background_activity_main_send_danmu_gray)
                    }
                    return true
                }
                return false
            }
        })
    }

    /**
     * 隐藏软键盘
     */
    private fun hideSoftKeyboard(view: View?) {
        val imm =
            view!!.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        danmukuVideoView!!.pause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        danmukuVideoView!!.release()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        if (!danmukuVideoView!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private val mHandler = Handler()

    private val list = mutableListOf<String>()

    /**
     * 模拟弹幕
     */
    private fun simulateDanmu() {
        Log.d("more", "DanmakuActivity, simulateDanmu()")
        list.add("雖然騙那些小屁孩上沙雷姆很容易")
        list.add("住在卡內基廳樓上跟傲嬌有什麼關係？？")
        list.add("這部真的超推")
        list.add("我還蠻不喜歡他最後亮槍那一段的。")
        list.add("談著白人才能彈的音樂 然後又是個甲 所以到哪都格格不入")
        list.add("整部電影其實對黑人的心態跟Shirley本人看待同族的方式一模一樣，都有一種詭異的逃避心理？？")
        list.add("還滿好看的啊")
        list.add("我喜歡三重奏的部分")
        list.add("我覺得是針對膚色阿，只是他在改變。")
        list.add("太愛這部電影了")
        mHandler.post(object : Runnable {
            override fun run() {
                danmukuVideoView!!.addDanmaku(list[(Math.random() * 10).toInt()], false)
                mHandler.postDelayed(this, 1000)
            }
        })
    }
}
