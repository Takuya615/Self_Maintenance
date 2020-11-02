package jp.tsumura.takuya.self_maintenance.ForCamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.common.io.ByteStreams.toByteArray
import com.google.common.io.Files.toByteArray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import jp.tsumura.takuya.self_maintenance.ForSetting.FriendSearchActivity

import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_camera_x.*
import kotlinx.android.synthetic.main.dialog_camera.view.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("RestrictedApi, ClickableViewAccessibility")

class CameraXActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var switchButton:ImageButton
    private lateinit var backButton:ImageButton
    private lateinit var backView: ConstraintLayout
    private lateinit var videoCapture: VideoCapture
    private lateinit var mAuth: FirebaseAuth
    //private lateinit var sound:Sounds

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
        //画面をオンのままにしておく
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        viewFinder = findViewById(R.id.view_finder)
        captureButton = findViewById(R.id.capture_button1)
        backView = findViewById(R.id.backview)
        switchButton = findViewById(R.id.switch_button)
        backButton = findViewById(R.id.back_button)
        mAuth = FirebaseAuth.getInstance()


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
        val sound = MediaActionSound()//シャッター音
        captureButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if(flag){
                    flag = false
                    switchButton.visibility = View.INVISIBLE
                    captureButton.setImageResource(R.drawable.ic_stop)
                    captureButton.setBackgroundColor(Color.WHITE)
                    backView.setBackgroundColor(Color.WHITE)
                    TimeRecorder(this)
                    sound.play(MediaActionSound.START_VIDEO_RECORDING)//シャッター音

                    val file = File(
                        externalMediaDirs.first(),
                        "${System.currentTimeMillis()}.mp4"
                    )

                    videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
                        override fun onVideoSaved(file: File?) {
                            val path = System.currentTimeMillis()
                            val user = mAuth.currentUser
                            if (user != null) {//ログインしてたら、画像の保存と、URLが取得できる
                                val storage = Firebase.storage
                                val storageRef = storage.reference
                                val photoRef = storageRef.child("${user.uid}/$path.mp4")
                                val movieUri = Uri.fromFile(file)
                                val uploadTask = photoRef.putFile(movieUri)
                                // Register observers to listen for when the download is done or if it fails
                                uploadTask.addOnFailureListener {
                                    Log.e("TAG", "ストレージへ保存失敗")
                                }.addOnSuccessListener {
                                    Log.e("TAG", "ストレージへ保存成功")
                                }.continueWithTask { task ->
                                    if (!task.isSuccessful) {
                                        task.exception?.let {
                                            throw it
                                        }
                                    }
                                    photoRef.downloadUrl
                                }.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val downloadUri:Uri? = task.result
                                        Firebase().WriteToStore(downloadUri.toString(),path)//Uriと日付を保存する
                                        Log.e("TAG", "URLの取得成功")
                                    } else {
                                        Log.e("TAG", "URLの取得に失敗")
                                    }
                                }

                            }

                        }

                        override fun onError(
                            useCaseError: VideoCapture.UseCaseError?,
                            message: String?,
                            cause: Throwable?
                        ) {
                            Log.e(tag, "Video Error: $message")
                        }
                    })
                }else if(flag==false){
                    flag = true
                    mTimer!!.cancel()
                    videoCapture.stopRecording()
                    sound.play(MediaActionSound.STOP_VIDEO_RECORDING)//シャッター音
                    Log.e(tag, "録画停止")

                    //CameraDialog().showDialog(this, mTimerSec,this)

                    CameraDialogFragment(mTimerSec).show(supportFragmentManager,"sample")
                    mTimerSec=0
                    timer.text = "00:00"
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
                Log.e("TAG", "カメラの切り替えに失敗")
            }
        }

        backButton.setOnClickListener{
            finish()
        }
        
        //コーチマーク
        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark3(this, this)
        }, 1000)
    }

    //ココからパーミッション系
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {

        if (requestCode == CameraXActivity.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "許可がないと動作しません",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED) {
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
        //val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        //val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)//内・外カメラの使い分け用
            //setTargetResolution(screenSize)//　　　Size(1280, 720)Size(metrics.widthPixels, metrics.heightPixels)
            //setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        val preview = AutoFitPreviewBuilder.build(previewConfig, viewFinder)//
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
            setTargetResolution(Size(metrics.widthPixels/100, metrics.heightPixels/100))//　　　　　ここで画質を下げている
            }.build()
        videoCapture = VideoCapture(videoCaptureConfig)


// Bind use cases to lifecycle
        CameraX.bindToLifecycle(this, preview, videoCapture)//,imageAnalyzer
    }

    //ここからタイマー用　　
    private fun TimeRecorder(context:Context){
        val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val taskSec: Int = prefs.getInt(getString(R.string.preferences_key_smalltime), 0)//習慣の初期値
        Log.e("TAG", "タスク所要時間の初期値は$taskSec")
        var totalday : Int = prefs.getInt("totalday", 0)//総日数の値を取得

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        if(user!=null){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (score != null) {
                    totalday = score.totalD
                }
            }
        }

        var times =0 //総日数が、2日更新されるごとに、強度を上げる場合。（totalday=1なら、1/2で、times=0となる）
        if(totalday>65){
            val ab = 11
            val bc = (totalday-66)/2
            times =ab+bc
        }else{
            times=totalday/6
        }

        if(times!=0 ) {//　　　割り算の演算子は整数までしか計算しないので、少数点以下は無視して出力される。
            val A = taskSec * times
            taskSec + A
            Log.e("TAG", "現在のタスク所要時間は$taskSec")
        }else{
            Toast.makeText(this,"時間設定がされていません",Toast.LENGTH_LONG).show()
        }

        // タイマーの作成
        mTimer = Timer()

        // タイマーの始動
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mTimerSec += 1
                val seconds = mTimerSec % 60;
                val minite = (mTimerSec / 60) % 60;
                mHandler.post {
                    timer.text = String.format("%02d:%02d", minite, seconds)
                    if(mTimerSec==taskSec){
                        Sounds.getInstance(context).playSound(Sounds.SOUND_DRUMROLL)
                    }
                    if (mTimerSec >= taskSec && taskSec != 0) {
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
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

}
