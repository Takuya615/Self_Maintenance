package jp.tsumura.takuya.self_maintenance.forGallery


import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.content_main.*
import java.net.URL


class GalleryActivity : AppCompatActivity() {
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //val replay = findViewById<ImageButton>(R.id.replay_button)
        //val play = findViewById<ImageButton>(R.id.play_button)
        //progressbar2.visibility = android.widget.ProgressBar.VISIBLE

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
        videoView.setOnPreparedListener(object:MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp:MediaPlayer){
                mp.start()
                mp.setOnVideoSizeChangedListener(object:MediaPlayer.OnVideoSizeChangedListener {
                    override fun onVideoSizeChanged(mp:MediaPlayer , arg1:Int , arg2:Int ) {
                        progressbar2.visibility = android.widget.ProgressBar.GONE
                    }
                })
            }
        })

        videoView.setMediaController(mediaControls)
        val value = intent.getStringExtra("selectedName")
        Log.e("TAG","ギャラリで指定されたURIは$value")
        val convertedUri = Uri.parse(value)
        try {
            videoView.setVideoURI(convertedUri)
            videoView.start()
        }catch(e:Exception){
            Log.e("TAG","URIから動画の取り出しに失敗")
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
}