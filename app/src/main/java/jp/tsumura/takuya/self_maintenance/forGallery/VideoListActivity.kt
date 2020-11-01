package jp.tsumura.takuya.self_maintenance.forGallery

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.video_list_item.*
import java.util.jar.Manifest


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
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
        }else{
            coll =db.collection(friendUid)
            userId = friendUid
        }

        coll.whereEqualTo("friend", false).get()//.orderBy("date", Query.Direction.DESCENDING)
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.e("TAG", "${document.id} => ${document.data}")

                    val date = document.data["date"].toString()
                    val uri = document.data["uri"].toString()
                    val like = document.data["like"].toString()
                    val path = document.data["path"].toString()
                    mdateList.add(date)
                    muriList.add(uri)
                    mlikeList.add(like)
                    mpathList.add(path)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }

        adapter = VideoListAdapter(mdateList, mlikeList)
        val layoutManager = LinearLayoutManager(this)

        // アダプターとレイアウトマネージャーをセット
        simpleRecyclerView.layoutManager = layoutManager
        simpleRecyclerView.adapter = adapter
        simpleRecyclerView.setHasFixedSize(true)

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
                        
                        coll.document(clickedText)
                            .delete()
                            .addOnSuccessListener {
                                Log.e(
                                    "TAG",
                                    "DocumentSnapshot successfully deleted!"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "TAG",
                                    "Error deleting document",
                                    e
                                )
                            }
                        Toast.makeText(
                            applicationContext,
                            "${clickedText}を削除しました",
                            Toast.LENGTH_LONG
                        ).show()

                        val aaa = mpathList[position]
                        val storage = Firebase.storage.reference
                        val desertRef = storage.child("$userId/$aaa.mp4")//
                        desertRef.delete().addOnSuccessListener {
                            // File deleted successfully
                            Log.e("TAG", "storageの削除に成功")
                        }.addOnFailureListener {
                            // Uh-oh, an error occurred!
                            Log.e("TAG", "storageの削除に失敗")
                        }

                        mdateList.remove(mdateList[position])
                        muriList.remove(muriList[position])
                        adapter.notifyItemRemoved(position)
                    }
                }
            }
        })
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