package jp.tsumura.takuya.self_maintenance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.shape.RoundedRectangle
import com.takusemba.spotlight.target.SimpleTarget
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import kotlinx.android.synthetic.main.content_main.*


class TutorialCoachMarkActivity(context:Context) {

    val prefs = context.getSharedPreferences( "preferences_key_sample", Context.MODE_PRIVATE)
    val Tuto1 : Boolean = false//prefs.getBoolean("Tuto1",false)
    val Tuto2 : Boolean = false//prefs.getBoolean("Tuto2",false)
    val Tuto3 : Boolean = false//prefs.getBoolean("Tuto3",false)
    val g : SharedPreferences.Editor = prefs.edit()

    //メイン画面でのコーチマーク
    fun CoachMark1(activity: Activity,context: Context){
        if(!Tuto1){
            g.putBoolean("Tuto1", true)
            g.commit()

            val target = activity.findViewById<FloatingActionButton>(R.id.fab)
            val Target = sreateCircleUI(target,activity,"ビデオカメラ","ココからカメラへ移動しましょう",0f,5f,1300f)

            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target)//firstTarget,
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
            // コーチマーク表示される時になんかする
            .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                override fun onStarted() {
                    //Toast.makeText(context, "spotlight is started", Toast.LENGTH_SHORT).show()
                }
                override fun onEnded() {
                    activity.progressbar.visibility = android.widget.ProgressBar.VISIBLE
                    val intent= Intent(context, GoalSettingActivity::class.java)
                    activity.startActivity(intent)
                }
            })
                .start()
        }
        /*
        else{
            activity.progressbar.visibility = android.widget.ProgressBar.VISIBLE
            val intent= Intent(context, CameraXActivity::class.java)
            activity.startActivity(intent)
        }

         */
    }


//設定画面でのコーチマーク
    fun CoachMark2(activity: Activity,context: Context){
        if(!Tuto2){
            g.putBoolean("Tuto2", true)
            g.commit()

            //コーチマークの実装    //引数（ボタン、アクティビティ、タイトル、本文、光X、光Y、文字列Y）
            val target1 = activity.findViewById<EditText>(R.id.Edittext1)
            val Target1 = sreateUI(target1,activity,"毎日つづけたいことを書いてください",
                "例）　筋トレ",
                0f,0f,600f)
            val target2 = activity.findViewById<EditText>(R.id.Edittext2)
            val Target2 = sreateUI(target2,activity,"具体的に毎日することを決めてください",
                "例）\n学校から帰り、カバンをおき、手を洗った後、そのままリビングのヨガマットの上で、腹筋・背筋・腕立て",
                0f,0f,0f)
            val target3 = activity.findViewById<TextView>(R.id.Edittext3)
            val Target3 = sreateUI(target3,activity,"1日何分くらいできるようになりたいのか？",
                "例）　６０分",1000f,0f,550f)
            val target4 = activity.findViewById<Button>(R.id.button)
            val Target4 = sreateUI(target4,activity,"設定後、ここをタップ","",0f,0f,550f)
            val target5 = activity.findViewById<TextView>(R.id.textView6)
            val Target5 = sreateUI(target5,activity,"今日のミッション",
                "あなたの今日のミッションが表示されます\nこのミッションをクリアするたびに、あなたのレベルが上がっていきます",0f,0f,550f)
            val target6 = activity.findViewById<Button>(R.id.setbutton)
            val Target6 = sreateUI(target6,activity,"始めてみましょう",
                "すべて設定が終わったら、ここをタップしてください",0f,0f,1200f)


            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target1,Target2,Target3,Target4,Target5,Target6)
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

            val target = activity.findViewById<FloatingActionButton>(R.id.fab)
            val Target = sreateCircleUI(target,activity,"ビデオカメラ","ココからカメラへ移動しましょう",0f,5f,1300f)

            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target)//firstTarget,
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                // コーチマーク表示される時になんかする
                .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                    override fun onStarted() {
                        //Toast.makeText(context, "spotlight is started", Toast.LENGTH_SHORT).show()
                    }
                    override fun onEnded() {
                    }
                })
                .start()
        }

    }





//引数（ボタン、アクティビティ、タイトル、本文、光X、光Y、文字列Y）　　四角の場合
    fun sreateUI(target:View,activity:Activity,title:String?,scrip:String,plusX:Float,plusY:Float,plusP:Float):SimpleTarget{

        val targetLocation = IntArray(2)
        target.getLocationInWindow(targetLocation)
        val targetX = targetLocation[0] + target.width/2f
        val targetY = targetLocation[1] + target.height/2f

        val UIwidth = target.width.toFloat() + plusX
        val UIheight = target.height.toFloat() + plusY

        // 注目されたいところを設定する
        val firstTarget = SimpleTarget.Builder(activity)
            .setPoint(targetX,targetY)//ハイライトの位置
            .setShape(RoundedRectangle(UIheight,UIwidth,25f))//ハイライトの大きさ
            .setTitle(title)
            .setDescription(scrip)
            .setOverlayPoint(50f, 200f + plusP)//文字列の位置
            .build()

        return firstTarget
    }


    //引数（ボタン、アクティビティ、タイトル、本文、光円半径、なし、文字列Y）　　四角の場合
    fun sreateCircleUI(target:View,activity:Activity,title:String?,scrip:String,plusX:Float,plusY:Float,plusP:Float):SimpleTarget{

        val targetLocation = IntArray(2)
        target.getLocationInWindow(targetLocation)
        val targetX = targetLocation[0] + target.width/2f
        val targetY = targetLocation[1] + target.height/2f
        val targetRadius = 78f + plusX

        // 注目されたいところを設定する
        val firstTarget = SimpleTarget.Builder(activity)
            .setPoint(targetX,targetY)//ハイライトの位置
            .setShape(Circle(targetRadius))//ハイライトの大きさ
            .setTitle(title)
            .setDescription(scrip)
            .setOverlayPoint(50f, 200f + plusP)//文字列の位置
            .build()

        return firstTarget
    }

    fun getLocationPoint(target: View):PointF {
        val targetLocation = IntArray(2)
        target.getLocationInWindow(targetLocation)
        val targetX = targetLocation[0] + target.width/2f //+ 700f//1000f
        val targetY = targetLocation[1] + target.height/2f //+ 150f//150f

        return PointF(targetX,targetY)
    }

}