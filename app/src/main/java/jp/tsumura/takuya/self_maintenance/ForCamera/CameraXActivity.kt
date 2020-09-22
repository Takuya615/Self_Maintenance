package jp.tsumura.takuya.self_maintenance.ForCamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.TextureView
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_camera_x.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi, ClickableViewAccessibility")

class CameraXActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var backView: ConstraintLayout
    private lateinit var videoCapture: VideoCapture
    private lateinit var prefs:SharedPreferences

    private var mTimer: Timer? = null
    private var mTimerSec:Int = 0
    private var mHandler = Handler()
    private val mFormat = StringBuilder()
    private val formatter = Formatter(mFormat, Locale.getDefault())
    private var dataformat = SimpleDateFormat("mm:SS", Locale.JAPAN)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)

        prefs = getSharedPreferences(getString(R.string.preferences_key_sample), Context.MODE_PRIVATE)
        viewFinder = findViewById(R.id.view_finder1)
        captureButton = findViewById(R.id.capture_button1)
        backView = findViewById(R.id.backview)

        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post {
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(
                this, CameraXActivity.REQUIRED_PERMISSIONS, CameraXActivity.REQUEST_CODE_PERMISSIONS
            )
        }

        //撮影開始ボタン、2度目のクリックで停止後、すぐにFirebaseへ保存される。
        var flag = true
        captureButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if(flag){
                    flag = false
                    captureButton.setBackgroundColor(Color.RED)
                    backView.setBackgroundColor(Color.GRAY)
                    TimeRecorder()

                    val file = File(externalMediaDirs.first(),
                        "${System.currentTimeMillis()}.mp4")
                    Firebase().WriteToRealtime(this,file)//Uriと日付を保存する
                    videoCapture.startRecording(file,object:VideoCapture.OnVideoSavedListener{
                        override fun onVideoSaved(file: File?) {
                            Firebase().SaveVideoToStorage(file)
                        }
                        override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
                            Log.e(tag, "Video Error: $message")
                        }
                    })
                }else if(flag==false){
                    flag = true
                    mTimer!!.cancel()
                    captureButton.setBackgroundColor(Color.RED)
                    videoCapture.stopRecording()
                    Log.e(tag, "録画停止")
                    CameraDialog(this,mTimerSec).showDialog()
                }
            }
            false
        }
    }

    //ココからパーミッション系
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == CameraXActivity.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "許可がないと動作しません",
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }

    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    //private fun allPermissionsGranted() = Camera.REQUIRED_PERMISSIONS.all {
    //    ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    //}

//ここからカメラ

    private fun startCamera(){
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().build()
// Build the viewfinder use case
        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            viewFinder.surfaceTexture = it.surfaceTexture
        }
        //   Create a configuration object for the video use case
        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        videoCapture = VideoCapture(videoCaptureConfig)

// Bind use cases to lifecycle
        CameraX.bindToLifecycle(this, preview,videoCapture)
    }

    //ここからタイマー用
    private fun TimeRecorder(){
        val taskSec: Int = prefs.getInt(getString(R.string.preferences_key_smalltime),0)
        Log.e("TAG","タスク所要時間の初期値は$taskSec")

        val memo2 : Int = prefs.getInt(getString(R.string.preferences_key2),0)//総日数の値を取得
        val times =memo2/3 //総日数が、3日更新されるごとに、強度を上げる場合

        if(times!=0) {
            val A = taskSec * times
            taskSec + A
            Log.e("TAG","現在のタスク所要時間は$taskSec")
        }

        // タイマーの作成
        mTimer = Timer()

        // タイマーの始動
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mTimerSec += 1
                val seconds =mTimerSec%60;
                val minite =(mTimerSec/60)%60;
                mHandler.post {
                    timer.text = String.format("%02d:%02d",minite,seconds)
                    if(mTimerSec >= taskSec){
                        backView.setBackgroundColor(Color.BLUE)
                        captureButton.setBackgroundColor(Color.BLUE)
                    }
                }
            }
        }, 1000, 1000)
    }


    companion object{
        private const val tag = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}
