package jp.tsumura.takuya.self_maintenance

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.shape.RoundedRectangle
import com.takusemba.spotlight.target.SimpleTarget

class TutorialCoachMarkActivity(context:Context) {


    val prefs = context.getSharedPreferences( "preferences_key_sample", Context.MODE_PRIVATE)
    val Tuto1 : Boolean = prefs.getBoolean("Tuto1",false)
    val Tuto2 : Boolean = prefs.getBoolean("Tuto2",false)
    val Tuto3 : Boolean = prefs.getBoolean("Tuto3",false)
    val g : SharedPreferences.Editor = prefs.edit()

    fun CoachMark1(activity: Activity,context: Context){

        if(!Tuto1){
            g.putBoolean("Tuto1", true)
            g.commit()

            //コーチマークの実装
            val target = activity.findViewById<View>(R.id.target)
            val targetLocation = IntArray(2)
            target.getLocationInWindow(targetLocation)
            val targetX = targetLocation[0] + target.width/2f + 700f//1000f
            val targetY = targetLocation[1] + target.height/2f + 150f//150f

            // 円の大きさ
            val targetRadius = 100f

            // 注目されたいところを設定する
            val firstTarget = SimpleTarget.Builder(activity)
                .setPoint(targetX, targetY)
                .setShape(Circle(targetRadius))
                .setTitle("まずはここをタップ")
                .setDescription("「目標設定」へ移動し\nあなたが習慣にしたい行動を決めてください。")
                .setOverlayPoint(20f, 250f)
                .build()

            val secondTarget = SimpleTarget.Builder(activity)
                .setPoint(600f,1400f)//920f, 1930f
                .setShape(Circle(150f))//78f
                .setTitle("撮影する")
                .setDescription("このアプリでは、あなたが習慣にしたい\n活動を毎日記録します。　\n　ここから撮影画面へ移動できます。")
                .setOverlayPoint(50f, 700f)
                .build()



            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(2f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(secondTarget,firstTarget)

                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                /*
            // コーチマーク表示される時になんかする
            .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                override fun onStarted() {
                    //Toast.makeText(context, "spotlight is started", Toast.LENGTH_SHORT).show()

                }

                override fun onEnded() {
                    Toast.makeText(context, "spotlight is ended", Toast.LENGTH_SHORT).show()
                    closeSpotlight()
                }
            })

                 */

                .start()
        }
    }



    fun CoachMark2(activity: Activity,context: Context){

        if(!Tuto2){
            g.putBoolean("Tuto2", true)
            g.commit()
            //コーチマークの実装
            val target = activity.findViewById<View>(R.id.goalsetting)
            val targetLocation = IntArray(2)
            target.getLocationInWindow(targetLocation)
            val targetX = targetLocation[0] + target.width/2f + 450f//480f
            val targetY = targetLocation[1] + target.height/2f + 1050f//1450f

            // 円の大きさ
            val targetRadius = 200f
            // 四角いコーチマークの高さと幅を追加
            val targetWidth = 1000f
            val targetHeight = 350f

            // 注目されたいところを設定する
            val firstTarget = SimpleTarget.Builder(activity)
                .setPoint(targetX, targetY)//ハイライトの位置
                .setShape(RoundedRectangle(targetHeight,targetWidth,25f))//ハイライトの大きさ
                //.setTitle("タイトル")
                .setDescription(context.getString(R.string.Tutorial))
                .setOverlayPoint(10f, 200f)//文字列の位置
                .build()

            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(2f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(firstTarget)
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                /*
            // コーチマーク表示される時になんかする
            .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                override fun onStarted() {
                    Toast.makeText(context, "spotlight is started", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onEnded() {
                    Toast.makeText(context, "spotlight is ended", Toast.LENGTH_SHORT).show()
                }
            })
                 */
                .start()
        }


    }



    fun CoachMark3(activity: Activity,context: Context){

        if(!Tuto3){
            g.putBoolean("Tuto3", true)
            g.commit()
            //コーチマークの実装
            val target = activity.findViewById<View>(R.id.target)
            val targetLocation = IntArray(2)
            target.getLocationInWindow(targetLocation)
            val targetX = targetLocation[0] + target.width/2f + 920f
            val targetY = targetLocation[1] + target.height/2f + 1930f

            // 円の大きさ
            val targetRadius = 78f

            // 注目されたいところを設定する
            val firstTarget = SimpleTarget.Builder(activity)
                .setPoint(targetX, targetY)
                .setShape(Circle(targetRadius))
                .setTitle("さっそく撮影してみましょう")
                .setDescription("1日にするタスク時間は決まりましたか？　\n　さっそく撮影してみましょう")
                .setOverlayPoint(10f, 250f)
                .build()

            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(firstTarget)
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                /*
            // コーチマーク表示される時になんかする
            .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                override fun onStarted() {
                    Toast.makeText(context, "spotlight is started", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onEnded() {
                    Toast.makeText(context, "spotlight is ended", Toast.LENGTH_SHORT).show()
                }
            })

                 */
                .start()
        }


    }
}