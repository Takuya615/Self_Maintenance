package jp.tsumura.takuya.self_maintenance.forGallery


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.VideoView
import jp.tsumura.takuya.self_maintenance.R


class GalleryActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val videoView: VideoView =findViewById(R.id.myvideoview)
        val value = intent.getStringExtra("selectedUri")
        Log.e("TAG","指定されたUriは$value")
        val convertedUri = Uri.parse(value)
        try {
            videoView.setVideoURI(convertedUri)
        }catch(e:Exception){
            Log.e("TAG","URIから動画の取り出しに失敗")
        }

        val replay = findViewById<ImageButton>(R.id.replay_button)
        replay.setOnClickListener{
            videoView.seekTo(0)
            videoView.start()
        }

        val play = findViewById<ImageButton>(R.id.play_button)
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