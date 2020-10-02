package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.TutorialCoachMarkActivity
import kotlinx.android.synthetic.main.activity_goal_setting.*
import java.util.*
import kotlin.concurrent.schedule

class GoalSettingActivity : AppCompatActivity(){

    private lateinit var prefs : SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val et3:EditText = findViewById(R.id.Edittext3)
        et3.setInputType( InputType.TYPE_CLASS_NUMBER)

        //このViewを開くと同時に、データを取得。　データがあれば各テキストに反映する。
        //ログインしてなければ、反映されない、ミッション時間は一度設定すれば、ログインなしでも使える。
        val user = mAuth.currentUser
        if(user!=null){
            val docRef = db.collection("Goals").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val goal = documentSnapshot.toObject(Goal::class.java)
                Edittext1.setText(goal?.goal)
                Edittext2.setText(goal?.task)
                Edittext3.setText(goal?.goaltime)

                TimeCal()

            }.addOnFailureListener { e -> Log.e("TAG", "データ取得に失敗", e) }
        }

        //設定ボタンを押したら、その時書いてある値をそのまま保存する。
        val Button=findViewById<Button>(R.id.button)
        Button.setOnClickListener(){
            DataSet()
            TimeCal()
        }
        val SetButton=findViewById<Button>(R.id.setbutton)
        SetButton.setOnClickListener(){
            val intent = Intent(this, CameraXActivity::class.java)
            startActivity(intent)

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
        val user = mAuth.currentUser

        if(goaltime.isEmpty()){
            Toast.makeText(this,"目標活動時間を入力してください",Toast.LENGTH_LONG).show()
        }
        val goal = Goal(
            mygoalmsg,
            taskmsg,
            goaltime,

        )
        if(user==null){
            Toast.makeText(this,"ログインでデータを保存しておけます",Toast.LENGTH_LONG).show()
        }else{
            goals.document(user.uid).set(goal)
                .addOnSuccessListener { Log.e("TAG", "ドキュメント作成・上書き成功") }
                .addOnFailureListener { e -> Log.e("TAG", "ドキュメントの作成・上書きエラー", e) }
        }

    }
    //
    fun TimeCal(){

        val goaltimeA =Edittext3.text.toString()//.toInt()
        if(goaltimeA.isNotEmpty()){
            val goaltime = goaltimeA.toInt()
            val Cal =goaltime *60 *3/100
            val seconds =Cal%60;
            val minite =(Cal/60)%60;
            if(Cal < 60){
                textView6.text = "今日は　$seconds　秒間やりましょう"
            }else{
                textView6.text = "今日は$minite 分 $seconds 秒間やりましょう"
            }
            Log.e("TAG","1日に行う秒数（タスク時間）は $Cal")
            val e : SharedPreferences.Editor = prefs.edit()
            e.putInt(getString(R.string.preferences_key_smalltime), Cal)
            e.commit()
        }

    }
    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        //Log.d("MainActivity", "onDestroy state:"+lifecycle.currentState)
    }
}