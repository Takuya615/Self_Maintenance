package jp.tsumura.takuya.self_maintenance

import android.content.Context
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
import org.json.JSONArray

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


        titleList = mutableListOf("総日数1日達成","総日数2日達成","総日数5日達成","総日数7日達成","総日数10日達成","総日数15日達成",
            "総日数20日達成","総日数25日達成","総日数30日達成","継続日数1日達成","継続日数2日達成","継続日数5日達成","継続日数7日達成",
            "継続日数10日達成","継続日数15日達成","継続日数20日達成","継続日数25日達成","継続日数30日達成"
        )
        val titleListR = mutableListOf("復活回数1回達成",
            "復活回数2回達成","復活回数3回達成","復活回数4回達成","復活回数5回達成","復活回数6回達成","復活回数7回達成","復活回数8回達成",
            "復活回数9回達成",
        )
        maxList = mutableListOf(1,2,5,7,10,15,20,25,30,//総日数
            1,2,5,7,10,15,20,25,30//継続
        )//復活
        val maxListR = mutableListOf(1,2,3,4,5,6,7,8,9)//復活

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
                    val t = score.totalD//総
                    val c = score.continuous//継続
                    val r = score.recover//復活
                    Log.d("TAG", "あたい　t は$t　です")
                    progressList = mutableListOf(t,t,t,t,t,t,t,t,t,c,c,c,c,c,c,c,c,c)
                    val progressListR = mutableListOf(r,r,r,r,r,r,r,r,r)

                    titleListR.forEach{
                        titleList.add(it)
                    }
                    maxListR.forEach{
                        maxList.add(it)
                    }
                    progressListR.forEach{
                        progressList.add(it)
                    }
                    for(i in 0..maxList.size-1){
                        if(maxList[i]<=progressList[i]){
                            hideButton.add(false)
                        }else{
                            hideButton.add(true)
                        }
                    }

                    adapter = AchievementAdapter(titleList,maxList,progressList,hideButton)
                    val layoutManager = LinearLayoutManager(this)
                    // アダプターとレイアウトマネージャーをセット
                    achieveRecyclerView.layoutManager = layoutManager//simpleRecyclerView
                    achieveRecyclerView.adapter = adapter
                    achieveRecyclerView.setHasFixedSize(true)

// インターフェースの実装
                    /*
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

                     */
                }
            }
        }
    }

    // リストの保存
    fun saveArrayList(key: String, arrayList: ArrayList<String>) {

        val shardPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val shardPrefEditor = shardPreferences.edit()

        val jsonArray = JSONArray(arrayList)
        shardPrefEditor.putString(key, jsonArray.toString())
        shardPrefEditor.apply()
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