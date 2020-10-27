package jp.tsumura.takuya.self_maintenance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import kotlinx.android.synthetic.main.activity_achievement.*

class AchievementActivity : AppCompatActivity() {

    private lateinit var adapter : AchievementAdapter
    private lateinit var titleList:MutableList<String>
    private lateinit var maxList:MutableList<Int>
    private lateinit var progressList:MutableList<Int>
    private lateinit var hideButton:MutableList<Boolean>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title="実績"


        titleList = mutableListOf("総日数1日達成","総日数2日達成","総日数5日達成","総日数7達成","総日数10達成","総日数15達成")
        maxList = mutableListOf(1,2,5,7,10,15)
        //progressList = mutableListOf(0,0,0,0,0)
        hideButton = mutableListOf()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser

        if(user !=null ){
            val docRef =db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (score != null) {
                    val t = score.totalD
                    Log.d("TAG", "あたい　t は$t　です")

                    progressList = mutableListOf(t,t,t,t,t,t)
                    for(i in 0..maxList.size-1){
                        if(maxList[i]<=progressList[i]){
                            hideButton.add(false)
                        }else{
                            hideButton.add(true)
                        }
                    }
                    Log.e("tag","hideListはこうなった$hideButton")

                    adapter = AchievementAdapter(titleList,maxList,progressList,hideButton)
                    val layoutManager = LinearLayoutManager(this)
                    // アダプターとレイアウトマネージャーをセット
                    achieveRecyclerView.layoutManager = layoutManager//simpleRecyclerView
                    achieveRecyclerView.adapter = adapter
                    achieveRecyclerView.setHasFixedSize(true)

// インターフェースの実装
                    adapter.setOnItemClickListener(object: AchievementAdapter.OnItemClickListener {
                        override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                            when (view.getId()) {
                                R.id.get_button -> {
                                    if(maxList[position]<=progressList[position]){
                                        SettingDialog().showDialogForMedal(this@AchievementActivity)
                                    }
                                    Log.e("TAG","実績を獲得しました")
                                }
                            }
                        }
                    })
                }
            }
        }
    }
    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}