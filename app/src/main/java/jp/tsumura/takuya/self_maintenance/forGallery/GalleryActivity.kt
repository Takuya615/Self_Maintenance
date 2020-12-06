package jp.tsumura.takuya.self_maintenance.forGallery


import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharaIntroDialogFragment
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.ViaStrItem
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.takeAtRandom
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_gallery.*
import java.util.*
import kotlin.collections.ArrayList


class GalleryActivity : AppCompatActivity() {


    private lateinit var db : FirebaseFirestore
    //private var like = 0
    //private var nameList:MutableList<String> = mutableListOf()

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
                val coll = db.collection(friendUid).document(documentName)
                coll.get().addOnSuccessListener { document ->
                    val myid =  FirebaseAuth.getInstance().currentUser!!.uid
                    val nameList = document[myid]

                    //その動画を、初めて閲覧したなら、強みを評価できる
                    if(nameList==null){

                        GalleryDialogFragment(coll,friendUid).show(supportFragmentManager,"StrengthsEvaliation")

                        /*
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.setTitle("${mRealm().UidToName(friendUid)}さんの強みはどちらですか？")
                        alertDialogBuilder.setMessage("")
                        //alertDialogBuilder.setView(iv)
                        val item = ViaStrItem.takeAtRandom(2, Random())//2つの要素だけを取り出す
                        alertDialogBuilder.setPositiveButton(item[0]){ dialog, which ->
                            addPoint(item[0],friendUid)//強みの評価
                            saveLikeAndNamelist(coll)//いいね＋１、　動画に署名
                            finish()

                        }
                        alertDialogBuilder.setNeutralButton(item[1]){ dialog, which ->
                            addPoint(item[1],friendUid)//強みの評価
                            saveLikeAndNamelist(coll)//いいね＋１、　動画に署名
                            finish()
                        }
                        // AlertDialogを作成して表示する
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()

                         */
                    }
                }
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

    fun addPoint(item:String,friendUid:String){

        val saveScore = db.collection("Scores").document(friendUid)
        saveScore.get().addOnSuccessListener { document ->
            val score = document.toObject(Score::class.java)
            if (document.data != null && score != null) {
                val viaList = score.vialist
                val num = ViaStrItem.indexOf(item)//ランダムにとった値の要素数（順番数）を取得
                val newList = mutableListOf<Int>()
                for(i in 0..23){
                    if(i!=num){
                        newList.add(viaList[i])
                    }else{
                        newList.add(viaList[i] + 1)
                    }
                }
                saveScore.update( "vialist",newList)
            }
        }
    }

    fun saveLikeAndNamelist(coll:DocumentReference){
        coll.get().addOnSuccessListener { document ->
            val myid =  FirebaseAuth.getInstance().currentUser!!.uid
            var like = document["like"].toString().toInt()
            like = like + 1
            coll.update("like",like)
            coll.update("$myid","true")
        }
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