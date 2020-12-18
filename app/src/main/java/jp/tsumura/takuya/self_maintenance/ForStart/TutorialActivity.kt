package jp.tsumura.takuya.self_maintenance.ForStart

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.stephentuso.welcome.*
import jp.tsumura.takuya.self_maintenance.R

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
            .defaultBackgroundColor(BackgroundColor(Color.DKGRAY))

            .page(BasicPage(
                R.drawable.tree_seichou02,
                "ようこそ！！",
                "このアプリは、望ましい行動を継続しやすくなるように、作られた習慣化アプリです。")
                //.background(BackgroundColor(Color.TRANSPARENT))
            )

            .page(BasicPage(
                R.drawable.self_movie2,
                "毎日・その場で・少しだけ",
                "スマホカメラを使って、フィットネス・ヨガ・勉強など、あなたにとって望ましい行動を、「自撮り」することで、習慣づくりをお手伝いします。")
                //.background(BackgroundColor(Color.CYAN))
            )

                /*
            .page(BasicPage(
                R.drawable.smallstep,
                "スモールステップ",
                "たとえば、いきなり「１時間勉強する」ではなく、「２分だけする」を継続し、徐々にその時間を伸ばしていきます。\n" +
                        "こうすることで心的負担が減り、より科学的に習慣化しやすくなることが分かっています。\nこのアプリでは適切なタイミングで" +
                        "その難易度が上がっていけるようあなたをサポートします。")
                //.background(BackgroundColor(Color.DKGRAY))
            )

                 */

                /*
                "{自分の習慣にしたい行動を毎日少しずつ自撮りし、それを継続することを手伝います。\n" +
                        "習慣作りの科学的テクニックとして、スモールステップがあります。いきなり大きなミッションを自分に課すのではなく、\n" +
                        "初めは、ちいさなミッションを毎日こなしていき、身体に「クセ」がついてから、徐々に大きなミッションへと成長させていきます。\\n\n" +
                        "例）　今日から１日１０ｋｍ走る!! ➡ ✖\n" +
                        "１日１回　玄関から出て１０歩あるく　➡　◎\\n大事なのは死ぬほど疲れた時でさえ続けられることです。}"
                 */
                //

            .swipeToDismiss(true)
            .build()
    }
}