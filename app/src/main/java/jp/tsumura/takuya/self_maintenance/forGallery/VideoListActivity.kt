package jp.tsumura.takuya.self_maintenance.forGallery

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.collect.Lists.reverse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.recycler_view.*

class VideoListActivity: AppCompatActivity() {

    private lateinit var adapter : VideoListAdapter
    private lateinit var mdateList:MutableList<String>
    private lateinit var muriList:MutableList<String>
    private lateinit var mlikeList:MutableList<String>
    private lateinit var mpathList:MutableList<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var coll :CollectionReference
    private lateinit var userId:String
    private lateinit var UriString:String
    var isfriend:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        muriList = mutableListOf<String>()
        mdateList = mutableListOf<String>()
        mlikeList = mutableListOf<String>()
        mpathList = mutableListOf<String>()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser

        //フレンドリスト由来のビデオリストなら、そのフレンドの名前が送られてくる
        val value = intent.getStringExtra("friendName")
        if(value ==null){
            if(user==null){
                title = "動画リスト"
            }else{
                title = "あなたの動画リスト"
            }
        }else{
            title = "$value さんの動画リスト"
        }

        val friendUid = intent.getStringExtra("friendUid")
        if(friendUid == null){
            coll =db.collection(user!!.uid)
            userId = user.uid
            isfriend = false
        }else{
            coll =db.collection(friendUid)
            userId = friendUid
            isfriend = true
        }

        coll.whereEqualTo("friend", false).get()//.orderBy("date", Query.Direction.DESCENDING)
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val date = document.data["date"].toString()
                    val uri = document.data["uri"].toString()
                    val like = document.data["like"].toString()
                    val path = document.data["path"].toString()
                    mdateList.add(date)
                    muriList.add(uri)
                    mlikeList.add(like)
                    mpathList.add(path)
                }
                mdateList.reverse()
                muriList.reverse()
                mlikeList.reverse()
                mpathList.reverse()
                adapter.notifyDataSetChanged()
            }
            //.addOnFailureListener { exception -> Log.w("TAG", "Error getting documents: ", exception) }

        adapter = VideoListAdapter(mdateList, mlikeList,isfriend)
        val layoutManager = LinearLayoutManager(this)

        // アダプターとレイアウトマネージャーをセット
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        // インターフェースの実装
        adapter.setOnItemClickListener(object : VideoListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                when (view.getId()) {
                    R.id.itemTextView -> {
                        UriString = muriList[position]
                        val intent = Intent(this@VideoListActivity, GalleryActivity::class.java)
                        intent.putExtra("selectedName", UriString)
                        intent.putExtra("friendUid", friendUid)
                        intent.putExtra("date", mdateList[position])
                        startActivity(intent)
                        //Toast.makeText(applicationContext, "${clickedText}がタップされました", Toast.LENGTH_LONG).show()
                    }
                    R.id.itemdeleate -> {
                        
                        coll.document(mdateList[position]).delete()

                        Toast.makeText(applicationContext, "${clickedText}を削除しました", Toast.LENGTH_LONG).show()

                        val aaa = mpathList[position]
                        val storage = Firebase.storage.reference
                        val desertRef = storage.child("$userId/$aaa.mp4")//
                        desertRef.delete()

                        mdateList.remove(mdateList[position])
                        muriList.remove(muriList[position])
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, mdateList.size)
                        //adapter.notifyDataSetChanged()

                    }
                }
            }
        })
    }


    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
