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
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.InputImage.*
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_gallery.*
import java.io.File
import com.google.mlkit.vision.common.InputImage.fromFilePath as fromFilePath1


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
        Log.e("TAG", "ギャラリで指定されたURIは$value")
        val convertedUri = Uri.parse(value)
        try {
            videoView.setVideoURI(convertedUri)
            videoView.start()
            videoView.setOnCompletionListener{
                if(friendUid!=null&&documentName!=null){
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("この動画に いいね！ を送りますか？")
                    alertDialogBuilder.setMessage("")
                    //alertDialogBuilder.setView(iv)
                    // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
                    alertDialogBuilder.setPositiveButton("いいね！"){ dialog, which ->
                        val coll = db.collection(friendUid).document(documentName)
                        coll.get().addOnSuccessListener { document ->
                            like = document["like"].toString().toInt()
                            Log.e("TAG", "like  の値は　$like")

                        }
                        val liked = like +1
                       coll.update("like",liked)
                           .addOnSuccessListener { Log.e("TAG", "likeの保存成功") }
                           .addOnFailureListener { e -> Log.e("TAG", "likeの保存失敗", e) }

                        Log.e("TAG", "documentName$documentName")
                    }
                    alertDialogBuilder.setNegativeButton("skip"){ dialog, which ->
                        Log.e("TAG", "閉じる")
                    }
                    // AlertDialogを作成して表示する
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        }catch (e: Exception){
            Log.e("TAG", "URIから動画の取り出しに失敗")
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

        //ポーズ検出のインスタンス
/*
        try {
            val options = PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build()
            val poseDetector = PoseDetection.getClient(options)
            val uri = myUri(convertedUri)
            val image : InputImage= InputImage.fromFilePath(this,uri)
            // result:Task<Pose> =
            poseDetector.process(image)
                .addOnSuccessListener { results ->
                    Log.e("TAG","成功結果は、、、$results")
                    // Task completed successfully
                    // ...
                }
                .addOnFailureListener { e ->
                    Log.e("TAG","分析に失敗")
                    // Task failed with an exception
                    // ...
                }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG","分析に失敗なぜじゃー")
        }

 */


    }

    fun myUri(originalUri:Uri):Uri{
        var returnedUri:Uri = originalUri
        if (originalUri.getScheme() == null){
            returnedUri = Uri.fromFile(File(originalUri.getPath()));
            // or you can just do -->
            // returnedUri = Uri.parse("file://"+camUri.getPath());
        }else{
            returnedUri = originalUri;
        }
        return returnedUri
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