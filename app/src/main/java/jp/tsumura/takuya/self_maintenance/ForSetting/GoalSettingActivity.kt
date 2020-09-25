package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_goal_setting.*

class GoalSettingActivity : AppCompatActivity(){

    private lateinit var prefs : SharedPreferences
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val et3:EditText = findViewById(R.id.Edittext3)
        val et4:EditText = findViewById(R.id.Edittext4)
        et3.setInputType( InputType.TYPE_CLASS_NUMBER)
        et4.setInputType( InputType.TYPE_CLASS_NUMBER)

        //このViewを開くと同時に、データを取得。　データがあれば各テキストに反映する。
        val docRef = db.collection("Goals").document("goals")
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val goal = documentSnapshot.toObject(Goal::class.java)
            Edittext1.setText(goal?.goal)
            Edittext2.setText(goal?.task)
            Edittext3.setText(goal?.goaltime)
            Edittext4.setText(goal?.smalltime)
            TimeCal()

        }.addOnFailureListener { e -> Log.e("TAG", "データ取得に失敗", e) }

        //設定ボタンを押したら、その時書いてある値をそのまま保存する。
        val SetButton=findViewById<Button>(R.id.button)
        SetButton.setOnClickListener(){
            DataSet()
            TimeCal()
        }
    }
//それぞれの入力内容をFirebaseに保存する
    fun DataSet(){
        val mygoalmsg = Edittext1.text.toString()
        val taskmsg = Edittext2.text.toString()
        val goaltime = Edittext3.text.toString()//目標時間（じぶんで決めるとモチベーションに？）
        val smalltime =Edittext4.text.toString()//パーセンテージ
        val goals = db.collection("Goals")

        if(goaltime == null){
            Toast.makeText(this,"目標活動時間を入力してください",Toast.LENGTH_LONG).show()
        }
        if(smalltime == null){
            Toast.makeText(this,"一日にこなすタスク時間を決めてください",Toast.LENGTH_LONG).show()
        }

        val goal = Goal(
            mygoalmsg,
            taskmsg,
            goaltime,
            smalltime
        )
        goals.document("goals").set(goal)
            .addOnSuccessListener { Log.e("TAG", "ドキュメント作成・上書き成功") }
            .addOnFailureListener { e -> Log.e("TAG", "ドキュメントの作成・上書きエラー", e) }
    }
    //
    fun TimeCal(){
        val goaltime =Edittext3.text.toString().toInt()
        val pasentage = Edittext4.text.toString().toInt()
        val Cal =goaltime *60 *pasentage/100
        val seconds =Cal%60;
        val minite =(Cal/60)%60;
        if(Cal < 60){
            textView7.text = "%)　$seconds　秒"
        }else{
            textView7.text = "%)"+ "$minite"+"分"+"$seconds"+"秒"
        }
        Log.e("TAG","1日に行う秒数は $Cal")
        val e : SharedPreferences.Editor = prefs.edit()
        e.putInt(getString(R.string.preferences_key_smalltime), Cal)
        e.commit()
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
}