package jp.tsumura.takuya.self_maintenance.ForCamera

import android.graphics.Color
import android.os.Handler
import java.util.*

class CameraTimer {
    /**
    private var mTimer: Timer? = null
    private var mHandler = Handler()


    public fun TimeRecorder(){
        // タイマーの作成
        mTimer = Timer()
        var mTimerSec = 0

        // タイマーの始動
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mTimerSec += 1
                val seconds =mTimerSec%60;
                val minite =(mTimerSec/60)%60;
                mHandler.post {
                    timer.text = String.format("%02d:%02d",minite,seconds)
                    if(mTimerSec >= 5){
                        captureButton.setBackgroundColor(Color.BLUE)
                    }
                }
            }
        }, 1000, 1000)
    }
    */
}