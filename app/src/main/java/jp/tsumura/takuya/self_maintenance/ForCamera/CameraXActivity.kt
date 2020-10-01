package jp.tsumura.takuya.self_maintenance.ForCamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

import android.graphics.Color
import android.graphics.SurfaceTexture
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.MotionEvent
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.TutorialCoachMarkActivity
import kotlinx.android.synthetic.main.activity_camera_x.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi, ClickableViewAccessibility")

class CameraXActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var switchButton:ImageButton
    private lateinit var backView: ConstraintLayout
    private lateinit var videoCapture: VideoCapture

    private var mTimer: Timer? = null
    private var mTimerSec:Int = 0
    private var mHandler = Handler()
    private val mFormat = StringBuilder()
    private val formatter = Formatter(mFormat, Locale.getDefault())
    private var dataformat = SimpleDateFormat("mm:SS", Locale.JAPAN)

    private var lensFacing = CameraX.LensFacing.FRONT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)

        viewFinder = findViewById(R.id.view_finder1)
        captureButton = findViewById(R.id.capture_button1)
        backView = findViewById(R.id.backview)
        switchButton = findViewById(R.id.switch_button)

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
                    captureButton.setImageResource(R.drawable.ic_stop)
                    captureButton.setBackgroundColor(Color.WHITE)
                    backView.setBackgroundColor(Color.WHITE)
                    TimeRecorder()

                    val file = File(externalMediaDirs.first(),
                        "${System.currentTimeMillis()}.mp4")
                    videoCapture.startRecording(file,object:VideoCapture.OnVideoSavedListener{
                        override fun onVideoSaved(file: File?) {

                            val storage = Firebase.storage
                            val storageRef = storage.reference
                            val photoRef = storageRef.child("images/${System.currentTimeMillis()}.mp4")
                            val movieUri = Uri.fromFile(file)
                            val uploadTask = photoRef.putFile(movieUri)
                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener {
                                Log.e("TAG","ストレージへ保存失敗")
                            }.addOnSuccessListener {
                                Log.e("TAG","ストレージへ保存成功")
                            }.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                photoRef.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val downloadUri = task.result
                                    Firebase().WriteToRealtime(downloadUri.toString())//Uriと日付を保存する
                                    Log.e("TAG","URLの取得成功")
                                } else {
                                    Log.e("TAG","URLの取得に失敗")
                                }
                            }
                        }

                        override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
                            Log.e(tag, "Video Error: $message")
                        }
                    })
                }else if(flag==false){
                    flag = true
                    mTimer!!.cancel()
                    videoCapture.stopRecording()
                    Log.e(tag, "録画停止")

                    CameraDialog(this,mTimerSec).showDialog()
                }
            }
            false
        }

        switchButton.setOnClickListener{
            lensFacing = if (CameraX.LensFacing.FRONT == lensFacing) {
                CameraX.LensFacing.BACK
            } else {
                CameraX.LensFacing.FRONT
            }
            try {
                // Only bind use cases if we can query a camera with this orientation
                CameraX.getCameraWithLensFacing(lensFacing)
                startCamera()
            } catch (exc: Exception) {
                Log.e("TAG","カメラの切り替えに失敗")
            }
        }
        
        //コーチマーク
        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark3(this,this)
        }, 1000)
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
        // Make sure that there are no other use cases bound to CameraX
        CameraX.unbindAll()

        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)//内・外カメラの使い分け用
            setTargetResolution(screenSize)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)

        }.build()
        val preview = AutoFitPreviewBuilder.build(previewConfig, viewFinder)
// Build the viewfinder use case
        //val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)
            val surfaceTexture: SurfaceTexture = it.surfaceTexture
            viewFinder.setSurfaceTexture(surfaceTexture)
        }
        //   Create a configuration object for the video use case
        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setLensFacing(lensFacing)//内・外カメラの使い分け用
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        videoCapture = VideoCapture(videoCaptureConfig)

// Bind use cases to lifecycle
        CameraX.bindToLifecycle(this, preview,videoCapture)
    }

    //ここからタイマー用　　
    private fun TimeRecorder(){
        val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val taskSec: Int = prefs.getInt(getString(R.string.preferences_key_smalltime),0)
        Log.e("TAG","タスク所要時間の初期値は$taskSec")

        val totalday : Int = prefs.getInt("preferences_key2",0)//総日数の値を取得
        val times =totalday/2 //総日数が、2日更新されるごとに、強度を上げる場合。（totalday=1なら、1/2で、times=0となる）

        if(times!=0) {//　　　割り算の演算子は整数までしか計算しないので、少数点以下は無視して出力される。
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
                        backView.setBackgroundColor(Color.GREEN)
                        captureButton.setBackgroundColor(Color.GREEN)
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
