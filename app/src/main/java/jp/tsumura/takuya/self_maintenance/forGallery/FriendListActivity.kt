package jp.tsumura.takuya.self_maintenance.forGallery
/*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.view.View

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForSetting.FriendSearchActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.Goal
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_camera_x.*
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_goal_setting.*

class FriendListActivity : AppCompatActivity() {

    //private lateinit var friendsadapter : ArrayAdapter<String>
    private lateinit var adapter : FriendListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var mnameList:MutableList<String>
    private lateinit var muidList:MutableList<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //コーチマーク
        val Coach = TutorialCoachMarkActivity(this)
        Coach.CoachMark5(this,this)

        muidList = mutableListOf<String>()
        mnameList = mutableListOf<String>()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        //フレンドリクエストが来ている場合、ダイアログを表示するYesなら、相手のUidをドキュメントとしてFirebaseに追加する
        val user = mAuth.currentUser

        if(user!=null){
            title = "${mRealm().UidToName(user.uid)}さんのフレンドリスト"
            val docRef = db.collection(user.uid).document("friendRequest")//whereEqualTo("request",true)
            docRef.get().addOnSuccessListener { document ->
                if(document.data != null){
                    val friendUid = document.data!!["friendUid"]
                    if(friendUid !=null){
                        val requestName = mRealm().UidToName(friendUid.toString())
                        //ダイアログ
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.apply {
                            setTitle("$requestName　さんからフレンドリクエストが届いてます")
                            setMessage("フレンドになると、その人の日々のミッションを応援してあげることができます\n\nリクエストを承認しますか？")
                            setPositiveButton("承認する"){dialog, which ->
                                val docRef2 = db.collection(user.uid).document(requestName)
                                val map = hashMapOf(
                                    "friend" to true,
                                    "name" to requestName,
                                    "uid" to friendUid//.toString()
                                )
                                docRef2.set(map)
                                    .addOnSuccessListener {

                                        mnameList.add(requestName)
                                        muidList.add(friendUid.toString())
                                        adapter.notifyDataSetChanged()
                                    }
                                //保存されているデータをnullに戻さないと。。。
                                docRef.set(hashMapOf("friendUid" to null ))
                            }
                            setNegativeButton("見なかったことにする"){dialog, which ->
                                //保存されているデータをnullに戻さないと。。。
                                docRef.set(hashMapOf("friendUid" to null ))
                            }
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }

                }
            }//.addOnFailureListener { e -> Log.e("TAG", "データ取得に失敗", e) }


            db.collection(user.uid).whereEqualTo("friend", true).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.data["name"].toString()
                        val uid = document.data["uid"].toString()
                        mnameList.add(name)
                        muidList.add(uid)
                        adapter.notifyDataSetChanged()

                    }
                }
                //.addOnFailureListener { exception -> Log.w("TAG", "Error getting documents: ", exception) }

            adapter = FriendListAdapter(mnameList)
            layoutManager = LinearLayoutManager(this)
            // アダプターとレイアウトマネージャーをセット
            simpleRecyclerView.layoutManager = layoutManager
            simpleRecyclerView.adapter = adapter
            simpleRecyclerView.setHasFixedSize(true)

            // インターフェースの実装
            adapter.setOnItemClickListener(object:FriendListAdapter.OnItemClickListener{
                override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                    when(view.getId()){
                        R.id.itemTextView -> {
                            val friendName = mnameList[position]
                            val friendUid = muidList[position]
                            val intent = Intent(this@FriendListActivity, VideoListActivity::class.java)
                            intent.putExtra("friendName",friendName)
                            intent.putExtra("friendUid",friendUid)
                            startActivity(intent)
                            //Toast.makeText(applicationContext, "${clickedText}がタップされました", Toast.LENGTH_LONG).show()
                        }
                        R.id.itemdeleate -> {
                            db.collection(user.uid).document(clickedText).delete()
                            //Toast.makeText(applicationContext, "${clickedText}を削除しました", Toast.LENGTH_LONG).show()
                            mnameList.remove(mnameList[position])
                            muidList.remove(muidList[position])
                            adapter.notifyItemRemoved(position)
                            adapter.notifyItemRangeChanged(position, mnameList.size)

                        }
                    }
                }
            })


        }

/*
        val itemLayoutId = R.layout.fragment_urilist_item
        val textViewId = R.id.label
        friendsadapter = ArrayAdapter(this,itemLayoutId,textViewId,mnameList)
        val listview = findViewById<ListView>(R.id.list2)
        listview.adapter = friendsadapter
        friendsadapter.notifyDataSetChanged()

        listview.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, mnameList[position], Toast.LENGTH_SHORT).show()
            //ユーザーIdを送り、そのユーザーの動画リストへ遷移する
            val value = muidList[position]
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra("selectedName",value)
            startActivity(intent)

        }

 */
    }

    //メニューバーのレイアウトを設定する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_friend_list, menu)
        return true
    }
    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                finish()
            }
            R.id.action_search -> {

                startActivity(Intent(this, FriendSearchActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

 */
