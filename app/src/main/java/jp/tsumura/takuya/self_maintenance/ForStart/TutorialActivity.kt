package jp.tsumura.takuya.self_maintenance.ForStart

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.stephentuso.welcome.*

class TutorialActivity : WelcomeActivity() {

    companion object {
        /**
         * まだ表示していなかったらチュートリアルを表示
         * SharedPreferencesの管理に関しては内部でよしなにやってくれているので普通に呼ぶだけで良い
         */
        fun showIfNeeded(activity: Activity, savedInstanceState: Bundle?) {
            WelcomeHelper(activity, TutorialActivity::class.java).show(savedInstanceState)
        }

        /**
         * 強制的にチュートリアルを表示したい時にはこちらを呼ぶ
         */
        fun showForcibly(activity: Activity) {
            WelcomeHelper(activity, TutorialActivity::class.java).forceShow()
        }
    }

    /**
     * 表示するチュートリアル画面を定義する
     */
    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(BackgroundColor(Color.RED))

            .page(BasicPage(
                android.R.drawable.ic_btn_speak_now,
                "ようこそ！！",
                "このアプリは、望ましい行動を習慣的に持続させやすくなるように、作られた習慣化アプリです。")
                .background(BackgroundColor(Color.GREEN))
            )
            .page(BasicPage(
                android.R.drawable.ic_btn_speak_now,
                "主な活動",
                "自分の習慣にしたい行動を毎日少しずつ自撮りし、それを継続することを手伝います。")
                .background(BackgroundColor(Color.GREEN))
            )
            .page(BasicPage(
                android.R.drawable.ic_btn_speak_now,
                "さあ、始めてみましょう",
                "さっそく始めてみましょう！！")
                .background(BackgroundColor(Color.GREEN))
            )

            .swipeToDismiss(true)
            .build()
    }
}