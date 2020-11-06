package jp.tsumura.takuya.self_maintenance.ForStart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.shape.RoundedRectangle
import com.takusemba.spotlight.target.SimpleTarget
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.content_main.*


class TutorialCoachMarkActivity(context:Context) {

    val prefs = context.getSharedPreferences( "preferences_key_sample", Context.MODE_PRIVATE)
    val Tuto0 : Boolean = prefs.getBoolean("Tuto0",false)
    val Tuto1 : Boolean = prefs.getBoolean("Tuto1",false)
    val Tuto2 : Boolean = prefs.getBoolean("Tuto2",false)
    val Tuto3 : Boolean = prefs.getBoolean("Tuto3",false)
    val Tuto4 : Boolean = prefs.getBoolean("Tuto4",false)
    val g : SharedPreferences.Editor = prefs.edit()

    //ログイン画面でのコーチマーク
    fun CoachMark0(activity: Activity,context: Context){

        if(!Tuto0){
            g.putBoolean("Tuto0", true)
            g.commit()

            val logout = activity.findViewById<Button>(R.id.logoutButton)
            logout.setText("スキップする")
            val target = activity.findViewById<Button>(R.id.createButton)
            val Target = sreateUI(target,activity,"ようこそ",
                "まずはココからあなたのアカウントを\n作ってください",0f,0f,-3f)

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

    //メイン画面でのコーチマーク
    fun CoachMark1(activity: Activity,context: Context){
        if(!Tuto1){
            g.putBoolean("Tuto1", true)
            g.commit()

            val target = activity.findViewById<FloatingActionButton>(R.id.fab)
            val Target = sreateCircleUI(target,activity,"目標を立てましょう","ココから目標を立て、カメラへ移動します",0f,0f,-2f)

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
                    activity.recreate()
                }
            })
                .start()
        }
    }


//設定画面でのコーチマーク
    fun CoachMark2(activity: Activity,context: Context){
        if(!Tuto2){
            //g.putBoolean("Tuto2", true)
            //g.commit()

            //コーチマークの実装    //引数（ボタン、アクティビティ、タイトル、本文、光X、光Y、文字列Y）
            val target1 = activity.findViewById<EditText>(R.id.Edittext1)
            val Target1 = sreateUI(target1,activity,"毎日つづけたいことを書いてください",
                "例）　筋トレ",
                0f,0f,2f)
            val target2 = activity.findViewById<EditText>(R.id.Edittext2)
            val Target2 = sreateUI(target2,activity,"具体的に毎日することを決めてください",
                "例）\n学校から帰り、カバンをおき、手を洗った後、そのままリビングのヨガマットの上で、腹筋・背筋・腕立て",
                0f,0f,1f)
            val target3 = activity.findViewById<TextView>(R.id.Edittext3)
            val Target3 = sreateUI(target3,activity,"1日何分くらいできるようになりたいのか？",
                "例）　６０分",1000f,0f,1f)
            val target4 = activity.findViewById<Button>(R.id.button)
            val Target4 = sreateUI(target4,activity,"設定後、ここをタップ","",0f,0f,-1f)
            val target5 = activity.findViewById<TextView>(R.id.textView6)
            val Target5 = sreateUI(target5,activity,"今日のミッション",
                "このミッションをクリアするたび、あなたのレベルが上がっていきます",0f,0f,-2f)
            val target6 = activity.findViewById<Button>(R.id.setbutton)
            val Target6 = sreateUI(target6,activity,"最後に",
                "最後に、ここをタップ",0f,0f,-3f)


            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target1,Target3,Target2,Target4)//,Target5,Target6
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

    fun CoachMark4(activity: Activity,context: Context){
        if(!Tuto2){
            g.putBoolean("Tuto2", true)
            g.commit()

            //コーチマークの実装    //引数（ボタン、アクティビティ、タイトル、本文、光X、光Y、文字列Y）
            val target5 = activity.findViewById<TextView>(R.id.textView6)
            val Target5 = sreateUI(target5,activity,"今日のミッション",
                "毎日続けていくと、少しずつこの時間が長くなります。" +
                        "詳細は　？をタップ",250f,0f,-2f)
            val target6 = activity.findViewById<Button>(R.id.setbutton)
            val Target6 = sreateUI(target6,activity,"始めてみましょう",
                "さっそく今日のミッション達成のため、自撮りしてみましょう！\nこの設定でよければ、ここをタップしてください",0f,0f,-3f)


            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target5,Target6)
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

//撮影画面でのコーチマーク
    fun CoachMark3(activity: Activity,context: Context){
        if(!Tuto3){
            g.putBoolean("Tuto3", true)
            g.commit()

            val target = activity.findViewById<TextView>(R.id.timer)
            val Target = sreateUI(target,activity,"ミッションの自撮り",
                "設定した時間がくるまで、ミッションをし、\nその様子を自撮りしてください\n時間になると画面が緑色に変わり、ミッション達成です",0f,0f,3f)
            val target2 = activity.findViewById<ImageButton>(R.id.capture_button1)
            val Target2 = sreateCircleUI(target2,activity,"ミッションスタート",
                "自撮り後は以下のことが確認できます\n継続日数 ➡ 何日間連続で継続できているか\n" +
                        "復活回数 ➡ １度休んだとしても何度そこから立ち直ったか",0f,0f,-3f)

            // コーチマークを作成
            Spotlight.with(activity)
                // コーチマーク表示される時の背景の色
                .setOverlayColor(R.color.colorCoachMark)
                // 表示する時間
                .setDuration(1000L)
                // 表示するスピード
                .setAnimation(DecelerateInterpolator(1f))
                // 注目されたいところ（複数指定も可能）
                .setTargets(Target,Target2)
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                // コーチマーク表示される時になんかする
                .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                    override fun onStarted() {
                    }
                    override fun onEnded() {
                    }
                })
                .start()
        }

    }
    //フレンド　リストの
    fun CoachMark5(activity: Activity,context: Context){
        if(!Tuto4){
            g.putBoolean("Tuto4", true)
            g.commit()

            // 注目されたいところを設定する
            val firstTarget = SimpleTarget.Builder(activity)
                .setShape(Circle(0f))//ハイライトの大きさ
                .setTitle("友達の動画をチェック")
                .setDescription("友人からの承認リクエストを受け取れば、このリスト内に追加されます。\n相手の撮影した動画がチェックでき、いいね！を送ることもできます。")
                .setOverlayPoint(2f,500f )//文字列の位置
                .build()

            val target:View = activity.findViewById(R.id.friendview)
            val targetLocation = IntArray(2)
            target.getLocationInWindow(targetLocation)
            val targetX =1000f// targetLocation[0] + target.width.toFloat()//2f
            val targetY = 0f//targetLocation[1] + target.height/2f

            val secondTarget = SimpleTarget.Builder(activity)
                .setPoint(targetX,targetY)//ハイライトの位置
                .setShape(Circle(0f))//ハイライトの大きさ
                .setTitle("フレンドリクエストを送る")
                .setDescription("右上の検索アイコン🔍から、友人へリクエストを送ることもできます。")
                .setOverlayPoint(2f,500f )//文字列の位置
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
                .setTargets(firstTarget,secondTarget)
                // 注目されたいところ以外をタップする時に閉じられるかどうか
                .setClosedOnTouchedOutside(true)
                // コーチマーク表示される時になんかする
                .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                    override fun onStarted() {
                    }
                    override fun onEnded() {
                    }
                })
                .start()
        }


    }









//引数（ボタン、アクティビティ、タイトル、本文、光X軸より広く、光Y軸より広く、文字列下なら正、文字列上なら負のFloat）　　四角の場合
    fun sreateUI(target:View,activity:Activity,title:String?,scrip:String,plusX:Float,plusY:Float,plusP:Float):SimpleTarget{

        val targetLocation = IntArray(2)
        target.getLocationInWindow(targetLocation)
        val targetX = targetLocation[0] + target.width/2f
        val targetY = targetLocation[1] + target.height/2f

        val UIwidth = target.width.toFloat() + plusX
        val UIheight = target.height.toFloat() + plusY

        //val OverLayPointY = targetLocation[1] + target.height * plusP

        // 注目されたいところを設定する
        val firstTarget = SimpleTarget.Builder(activity)
            .setPoint(targetX,targetY)//ハイライトの位置
            .setShape(RoundedRectangle(UIheight,UIwidth,25f))//ハイライトの大きさ
            .setTitle(title)
            .setDescription(scrip)
            .setOverlayPoint(2f,targetLocation[1] + target.height * plusP )//文字列の位置
            .build()

        return firstTarget
    }

    //引数（ボタン、アクティビティ、タイトル、本文、光円半径、なし、文字列下なら正、文字列上なら負のFloat）　　円の場合
    fun sreateCircleUI(target:View,activity:Activity,title:String?,scrip:String,plusX:Float,plusY:Float,plusP:Float):SimpleTarget{

        val targetLocation = IntArray(2)
        target.getLocationInWindow(targetLocation)
        val targetX = targetLocation[0] + target.width/2f
        val targetY = targetLocation[1] + target.height/2f
        val targetRadius = 78f + plusX
        val firstTarget = SimpleTarget.Builder(activity)
            .setPoint(targetX,targetY)//ハイライトの位置
            .setShape(Circle(targetRadius))//ハイライトの大きさ
            .setTitle(title)
            .setDescription(scrip)
            .setOverlayPoint(2f, targetLocation[1] + target.height * plusP)//文字列の位置
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

    fun reset(){
        //g.putBoolean("Tuto0", false)
        g.putBoolean("Tuto1", false)
        g.putBoolean("Tuto2", false)
        g.putBoolean("Tuto3", false)
        g.putBoolean("Tuto4", false)
        g.commit()
    }
}