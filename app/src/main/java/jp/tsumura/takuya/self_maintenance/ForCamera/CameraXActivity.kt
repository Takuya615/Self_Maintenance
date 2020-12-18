package jp.tsumura.takuya.self_maintenance.ForCamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Size
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity.Companion.taskTimeCaluculate
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_camera_x.*
import java.io.File

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


    private var mTimer: Timer? = null
    private var mTimerSec:Int = 0
    private var taskSec:Int = 0
    private var totalday : Int = 0
    private var mHandler = Handler()
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

        captureButton.visibility = View.INVISIBLE

        Sounds.getInstance(this)//音の初期化

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


        val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        taskSec = prefs.getInt(getString(R.string.preferences_key_smalltime), 0)//習慣の初期値
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        if(user!=null){

            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)

                if(score != null){ totalday = score.totalD }

                taskSec = taskTimeCaluculate(totalday,taskSec)//GoalSettingActから、適切なタスク時間を計算する

                captureButton.visibility = View.VISIBLE
                progress.visibility = android.widget.ProgressBar.INVISIBLE
            }

        }else{ Toast.makeText(this,"ログインすれば時間を記録できます",Toast.LENGTH_LONG).show()
            captureButton.visibility = View.VISIBLE
            progress.visibility = android.widget.ProgressBar.INVISIBLE
        }


        //撮影開始ボタン、2度目のクリックで停止後、すぐにFirebaseへ保存される。
        var flag = true
        val sound = MediaActionSound()//シャッター音
        captureButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if(flag){
                    flag = false
                    switchButton.visibility = View.INVISIBLE
                    backButton.visibility = View.INVISIBLE
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
                            //val user = mAuth.currentUser
                            val storage = Firebase.storage
                            val storageRef = storage.reference
                            val photoRef = storageRef.child("${user!!.uid}/$path.mp4")
                            val movieUri = Uri.fromFile(file)
                            val uploadTask = photoRef.putFile(movieUri)
                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener {
                                //Log.e("TAG", "ストレージへ保存失敗")
                            }.addOnSuccessListener {
                                //Log.e("TAG", "ストレージへ保存成功")
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
                                }
                            }
                        }

                        override fun onError(useCaseError: VideoCapture.UseCaseError?,message: String?, cause: Throwable?) { }
                    })
                }else if(flag==false){
                    flag = true
                    backButton.visibility = View.VISIBLE
                    mTimer!!.cancel()
                    videoCapture.stopRecording()
                    sound.play(MediaActionSound.STOP_VIDEO_RECORDING)//シャッター音

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
            CameraX.getCameraWithLensFacing(lensFacing)
            startCamera()
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
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mTimerSec += 1
                val seconds = mTimerSec % 60;
                val minite = (mTimerSec / 60) % 60;
                mHandler.post {
                    timer.text = String.format("%02d:%02d", minite, seconds)
                    if(mTimerSec==taskSec){
                        Sounds.getInstance(this@CameraXActivity).playSound(Sounds.SOUND_DRUMROLL)
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
