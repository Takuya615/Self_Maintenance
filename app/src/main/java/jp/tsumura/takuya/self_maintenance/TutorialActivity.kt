package jp.tsumura.takuya.self_maintenance

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
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
            .page(TitlePage(R.mipmap.ic_launcher, "Title"))
            .page(BasicPage(
                android.R.drawable.ic_delete,
                "Basic page 1",
                "hogehoge")
                .background(BackgroundColor(Color.GREEN)))
            .page(BasicPage(
                android.R.drawable.ic_btn_speak_now,
                "Basic page 2",
                "fugafuga")
                .background(BackgroundColor(Color.BLUE))
            )
            .swipeToDismiss(true)
            .build()
    }
}