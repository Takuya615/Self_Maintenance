package jp.tsumura.takuya.self_maintenance.forGallery


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.ViaForChart

import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.ViaStrItem
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.saveVia

import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.takeAtRandom
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_gallery.*
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class GalleryActivity : AppCompatActivity() {


    private lateinit var db : FirebaseFirestore
    private var like = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //画面をオンのままにしておく
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //val replay = findViewById<ImageButton>(R.id.replay_button)
        //val play = findViewById<ImageButton>(R.id.play_button)
        //progressbar2.visibility = android.widget.ProgressBar.VISIBLE


        db = FirebaseFirestore.getInstance()


        var mediaControls: MediaController? = null
        //リストから指定されたfileNameを取得する
        val videoView: VideoView =findViewById(R.id.myvideoview)
        val main = findViewById<Button>(R.id.mainbutton)

        if (mediaControls == null) {
            // creating an object of media controller class
            mediaControls = MediaController(this)
            // set the anchor view for the video view
            mediaControls.setAnchorView(videoView)
        }

        progressbar2.visibility = android.widget.ProgressBar.VISIBLE
        videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer) {
                mp.start()
                mp.setOnVideoSizeChangedListener(object : MediaPlayer.OnVideoSizeChangedListener {
                    override fun onVideoSizeChanged(mp: MediaPlayer, arg1: Int, arg2: Int) {
                        progressbar2.visibility = android.widget.ProgressBar.GONE
                    }
                })
            }
        })

        videoView.setMediaController(mediaControls)
        val value = intent.getStringExtra("selectedName")
        val friendUid = intent.getStringExtra("friendUid")
        val documentName = intent.getStringExtra("date")
        val convertedUri = Uri.parse(value)

        videoView.setVideoURI(convertedUri)
        videoView.start()
        videoView.setOnCompletionListener{
            if(friendUid!=null&&documentName!=null){
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("${mRealm().UidToName(friendUid)}さんの強みはどちらですか？")
                alertDialogBuilder.setMessage("")
                //alertDialogBuilder.setView(iv)
                // 肯定ボタンに表示される文字列、押したときのリスナーを設定する

                val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
                val item = ViaStrItem.takeAtRandom(2, Random())//2つの要素だけを取り出す
                alertDialogBuilder.setPositiveButton(item[0]){ dialog, which ->
                    val newList = addPoint(item[0],prefs)
                    saveVia(prefs,newList)
                }
                alertDialogBuilder.setNegativeButton(item[1]){ dialog, which ->
                    val newList = addPoint(item[1],prefs)
                    saveVia(prefs,newList)
                }
                // AlertDialogを作成して表示する
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }

        //取得したfileNameで、Storageから、そのURIを取得
        /*
        val storageRef = Firebase.storage.reference
        storageRef.child("images/$value").downloadUrl.addOnSuccessListener {
            uri = it
            Log.e("TAG","URIは$uri")
            //ここに追加してた

        }.addOnFailureListener {
            Log.e("TAG","URIの取得に失敗")
        }


        replay.setOnClickListener{
            videoView.seekTo(0)
            videoView.start()
        }


        play.setOnClickListener {
            if (videoView.isPlaying()) {
                // 動画を一時停止する
                videoView.pause()
                play.setImageResource(R.drawable.ic_play_button)
            } else {
                // 動画を再生再開する
                videoView.start()
                play.setImageResource(R.drawable.ic_pause_button)
            }
        }

         */
        main.setOnClickListener{
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun addPoint(item:String,prefs:SharedPreferences):ArrayList<Int>{

        val ViaList = JSONArray(prefs.getString("VIA","[100,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]"))
        val num = ViaForChart.indexOf(item)//ランダムにとった値の要素数（順番数）を取得

        val newList = arrayListOf<Int>()
        for(i in 0..5){
            val via = ViaList[i].toString().toInt()
            if(i!=num){
                newList.add(via)
            }else{
                newList.add(via + 1)
            }
        }
        return newList
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