package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isInvisible
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import kotlinx.android.synthetic.main.activity_goal_setting.*

class GoalSettingActivity : AppCompatActivity(){

    private lateinit var prefs : SharedPreferences
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val user = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "目標設定"

        //mAuth = FirebaseAuth
        prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val et3:EditText = findViewById(R.id.Edittext3)
        et3.setInputType( InputType.TYPE_CLASS_NUMBER)

        //このViewを開くと同時に、データを取得。　データがあれば各テキストに反映する。
        //ログインしてなければ、反映されない、ミッション時間は一度設定すれば、ログインなしでも使える。

        if(user!=null){
            val docRef = db.collection("Goals").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val goal = documentSnapshot.toObject(Goal::class.java)
                Edittext1.setText(goal?.goal)
                Edittext2.setText(goal?.task)
                Edittext3.setText(goal?.goaltime)

                TimeCal()

            }.addOnFailureListener { }
        }


        //設定ボタンを押したら、その時書いてある値をそのまま保存する。
        //val Button=findViewById<Button>(R.id.button)
        button.setOnClickListener(){
            DataSet()
            TimeCal()
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark4(this,this)
        }
        //val SetButton=findViewById<Button>(R.id.setbutton)
        setbutton.setOnClickListener(){
            val intent = Intent(this, CameraXActivity::class.java)
            startActivity(intent)
            finish()
        }
        help.setOnClickListener(){
            SettingDialog().showDialog(this)
        }


        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark2(this,this)
        }, 1000)

    }
//それぞれの入力内容をFirebaseに保存する
    fun DataSet(){
        val mygoalmsg = Edittext1.text.toString()
        val taskmsg = Edittext2.text.toString()
        val goaltime = Edittext3.text.toString()//目標時間（じぶんで決めるとモチベーションに？）
        val goals = db.collection("Goals")
        //val user = mAuth.currentUser

        if(goaltime.isEmpty()){
            Toast.makeText(this,"目標の時間を入力してください",Toast.LENGTH_LONG).show()
        }
        val goal = Goal(
            mygoalmsg,
            taskmsg,
            goaltime,

        )
        if(user==null){ Toast.makeText(this,"ログインすればデータを保存できます",Toast.LENGTH_LONG).show()
        }else{ goals.document(user.uid).set(goal)
        }

    }

    fun TimeCal(){
        val goaltimeA =Edittext3.text.toString()//.toInt()
        val e : SharedPreferences.Editor = prefs.edit()

        if(user!=null){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                var totalday = 0
                if (score != null) {
                    totalday = score.totalD
                }
                if(goaltimeA.isNotEmpty()){
                    val goaltime = goaltimeA.toInt()//最終目標時間
                    var Cal =goaltime *60 *1/100//　　　　　　　初期値は 0.5％からスタート
                    e.putInt(getString(R.string.preferences_key_smalltime), Cal)
                    e.apply()

                    Cal = taskTimeCaluculate(totalday,Cal)//総日数から適切なタスク時間を産出する

                    val seconds =Cal%60
                    if(Cal < 60){
                        textView6.text = "今日は　$seconds　秒間やりましょう"
                    }else{
                        val minite =(Cal/60)%60
                        textView6.text = "今日は$minite 分 $seconds 秒間やりましょう"
                    }

                }else{
                    //数字が設定されなければ、値0に戻る。
                    val Empty = 0
                    e.putInt(getString(R.string.preferences_key_smalltime), Empty)
                    e.commit()
                    textView6.text = "今日は　０　秒間\n（ ０%）やりましょう"
                }

            }

        }
    }
    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    companion object{
        fun taskTimeCaluculate(totalday:Int,oneParsent:Int):Int{
            var times =0//        総日数が、2日更新されるごとに、強度を上げる場合。（totalday=1なら、1/2で、times=0となる）
            var cal = oneParsent
            if(totalday>48){
                val ab = 16
                val bc = (totalday-48)/3
                times =ab+bc
            }else{
                times=totalday/3

            }
            if(times!=0 ) {//　　　割り算の演算子は整数までしか計算しないので、少数点以下は無視して出力される。
                val A = oneParsent * times
                cal = oneParsent + A
            }
            return cal
        }

    }
}