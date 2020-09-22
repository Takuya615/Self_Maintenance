package jp.tsumura.takuya.self_maintenance.ForCamera
/*
import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*

import android.hardware.camera2.*

import android.media.ImageReader
import android.media.MediaRecorder
import android.net.Uri
import android.os.*

import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R

import kotlinx.android.synthetic.main.activity_camera2.*
import java.io.File

import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.US

/*
TextureView に surfaceTextureListener を設定する
↓
コールバック関数が呼び出され、関数内のオーバーライドメソッド（onSurfaceTextureAvailable）が呼ばれる
↓
openCamera() を呼び出す → requestPermission() を呼び出す
↓
Cameraの状態をコールバックする関数を設定する（stateCallback）
↓
↓ カメラの準備ができたら
↓
Cameraから取得したデータをプレビューする（createCameraPreviewSession）
 */

class Camera2Activity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var textureView: AutoFitTextureView
    private lateinit var previewRequest: CaptureRequest
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var captureButton: ImageButton
    private lateinit var ResultButton: Button
    private lateinit var SwichButton: ImageButton
    private lateinit var ChandeBack: ConstraintLayout
    private lateinit var cameraId: String   //      camera裏表用
    private lateinit var storage: FirebaseStorage
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var captureSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private var previewSize: Size = Size(300, 300)
    private var imageReader: ImageReader? = null

    private var cameraCaptureSession: CameraCaptureSession? = null
    private var cameraFacing = CameraCharacteristics.LENS_FACING_BACK
    private var recorder: MediaRecorder? = null

    private val ASPECT_VERTICAL = 16
    private val ASPECT_HORIZONTAL = 9

    private lateinit var prefs : SharedPreferences
    //タイマー用
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    private var mTimerSec:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        textureView = findViewById(R.id.camera_view)
        storage = Firebase.storage

        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId= manager.getCameraIdList()[0]
        prefs = getSharedPreferences(getString(R.string.preferences_key_sample), Context.MODE_PRIVATE)

        captureButton = findViewById(R.id.capture_button)
        ResultButton = findViewById(R.id.result_button)
        SwichButton = findViewById(R.id.camera_switch_button)
        ChandeBack = findViewById(R.id.backview)

        ResultButton.setOnClickListener {
            Log.e("TAG", "リザルト画面")
        }
        //撮影開始ボタン、2度目のクリックで停止後、すぐにFirebaseへ保存される。
        var flag = true
        captureButton.setOnClickListener {
            if (flag) {
                flag = false
                captureButton.setBackgroundColor(Color.RED)
                ChandeBack.setBackgroundColor(Color.GRAY)
                Log.e("TAG", "録画開始")
                startRecording()
                TimeRecorder()

            } else {
                flag = true
                mTimer!!.cancel()
                captureButton.setBackgroundColor(Color.GRAY)
                ChandeBack.setBackgroundColor(Color.GRAY)
                Log.e("TAG", "録画停止")
                stopRecording()
                showDialog()
            }
        }
        SwichButton.setOnClickListener{
            Log.e("TAG", "画面の切り替えボタンが押されました")
            switchCamera()
        }
    }

    //ボタンを押したときのアクション　　録画スタートとストップ
    private fun startRecording() {
        // Write a message to the database
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val genreRef = dataBaseReference.child("URIlists")

        //val file= getAppSpecificAlbumStorageDir(this,"${System.currentTimeMillis()}.mp4")
        val file = File(this.getExternalFilesDir( Environment.DIRECTORY_MOVIES),
            "${System.currentTimeMillis()}.mp4")

        val data = HashMap<String, String>()
        val uri =Uri.fromFile(file).toString()
        data["uri"] = uri

        val date=Calendar.getInstance().getTime()
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日")
        val StrDate =dateFormat.format(date).toString()
        data["date"]=StrDate

        genreRef.push().setValue(data)

        Log.e("TAG","カメラのアウトプットファイルには$file")
        recorder = MediaRecorder().apply {

            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(file)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncoder(MediaRecorder.VideoEncoder.VP8)
            prepare()
            start()
        }
        //FirebaseStorageへ　アップロード
        val storageRef = storage.reference
        val photoRef = storageRef.child("images/${System.currentTimeMillis()}.mp4")
        Log.e("TAG","ストレージに保存されるのは$file")
        val movieUri = Uri.fromFile(file)
        val uploadTask = photoRef.putFile(movieUri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.e("TAG","ストレージへ保存失敗")
        }.addOnSuccessListener {
            Log.e("TAG","ストレージへ保存成功")
            Log.e("TAG",photoRef.toString())
        }
    }
    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
    // This method switches Camera Lens Front or Back then restarts camera.
    fun switchCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if(cameraFacing == CameraCharacteristics.LENS_FACING_BACK){
            cameraFacing = CameraCharacteristics.LENS_FACING_FRONT
            for (Id in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(Id)
                // バックカメラを利用しない
                val aAA =characteristics.get(CameraCharacteristics.LENS_FACING)
                if(aAA == CameraCharacteristics.LENS_FACING_BACK){
                    continue
                }
                // ストリーム制御をサポートしていない場合、セットアップを中断する
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP )
                if(map == null){
                    continue
                }
                Log.e("TAG","フロントカメラのCameraIdは$Id")
                cameraId = Id
            }

        }else{
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK
            cameraId= manager.getCameraIdList()[0]
        }
        close()
        onResume()
    }

    private fun prepareBackgroundHandler() {
        backgroundThread = HandlerThread("backgroundThread")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.looper)
    }
    //textureViewの初期化メソッド。　回転とか撮影画面サイズ変更時の再調整などもする
    //textureViewがすぐに開けばiaAvailableで開く、そうでなければリスナー引っ張って、開くまで待つ
    override fun onResume() {
        super.onResume()

        prepareBackgroundHandler()

        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int
                ) {
                    imageReader = ImageReader.newInstance(
                        width, height,
                        ImageFormat.JPEG, /*maxImages*/ 2
                    )
                    openCamera(width, height)
                }

                override fun onSurfaceTextureSizeChanged(
                    p0: SurfaceTexture?,
                    p1: Int,
                    p2: Int
                ) {
                    configureTransform(p1, p2)
                }

                override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
                }

                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
                    Log.e("TAG", "onSurfaceTextureDestroyedの関数が仕事してます")
                    if(cameraDevice != null){
                        closeCamera()

                        cameraDevice = null
                    }
                    return false
                }
            }
        }
    }


    fun openCamera(width: Int, height: Int) {
        val manager: CameraManager =
            getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //CameraIdListのゼロ番目は多くの場合、背面カメラの1倍

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
            return
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            requestRECORD_AUDIOPermission()
            return
        }
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            requestWRITEStoragePermission()
            return
        }
        if (permission4 != PackageManager.PERMISSION_GRANTED) {
            requestREADStoragePermission()
            return
        }

        setUpCameraOptions()
        configureTransform(width, height)
        //ここにオプションやコンフィグトランスフォームの設定関数入れる？

        manager.openCamera(cameraId, stateCallback, backgroundHandler)

    }
    //　　　　　　　　　　　↑　　上のopenCameraの第2引数として利用　　↑
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            this@Camera2Activity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@Camera2Activity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            finish()
        }
    }
    //         ↑　　上のstateCallback関数のonOpendで使われるｌ関数　　　↑
    private fun createCameraPreviewSession() {

        val texture = textureView.surfaceTexture
        texture.setDefaultBufferSize(previewSize.width, previewSize.height)
        val surface = Surface(texture)//TextureViewからの撮影データの受取先
        val testsurface = imageReader!!.surface

        previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(surface)
        previewRequestBuilder.addTarget(testsurface)

        //       CameraCaptureSession はカメラから送られてくる画像を受け取ったり、
        //       同一セッションで前に受け取った画像の再処理などをするクラスです
        cameraDevice?.createCaptureSession(Arrays.asList(surface, imageReader?.surface),
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    if (cameraDevice == null) return
                    captureSession = cameraCaptureSession

                    previewRequestBuilder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                    )

                    //プレビュー画像を TextureView に送ります
                    previewRequest = previewRequestBuilder.build()
                    captureSession?.setRepeatingRequest(
                        previewRequest,
                        null,
                        backgroundHandler //Handler(backgroundThread?.looper)
                    )
                }
                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, null
        )
    }




    //camera　横向き対策　カメラを傾けた際に向きを合わせる関数
    //OpenCameraメソッドに入れる関数　１
    private fun setUpCameraOptions() {
        previewSize = getPreviewSize()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            textureView.setAspectRatio(previewSize.width, previewSize.height)
        } else {
            textureView.setAspectRatio(previewSize.height, previewSize.width)
        }
    }
    //　　　OpenCameraメソッドに加える関数　２
    private fun configureTransform(width: Int, height: Int) {
        val rectView = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val rectPreview =
            RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = rectView.centerX()
        val centerY = rectView.centerY()
        val matrix = Matrix()
        val rotation = windowManager.defaultDisplay.rotation

        when (rotation) {
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                rectPreview.offset(
                    centerX - rectPreview.centerX(),
                    centerY - rectPreview.centerY()
                )

                val scale = Math.max(
                    height.toFloat() / previewSize.height,
                    width.toFloat() / previewSize.width
                )

                with(matrix) {
                    setRectToRect(rectView, rectPreview, Matrix.ScaleToFit.FILL)
                    postScale(scale, scale, centerX, centerY)
                    postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
                }
            }
            Surface.ROTATION_180 -> {
                matrix.postRotate(180f, centerX, centerY)
            }
        }
        textureView.setTransform(matrix)
    }
    //ココからアスペクト比を求める 1-1 (SetupCameraOptionに加えてる)
    private fun getPreviewSize(): Size {
        val manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //val cameraId = manager.cameraIdList[0]

        val characteristic = manager.getCameraCharacteristics(cameraId)
        val scm = characteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val mPreviewSize = scm?.getOutputSizes(SurfaceTexture::class.java) ?: return Size(1, 1)
        var maxSize: Size? = null

        for (ps in mPreviewSize) {
            val gcd = getGCD(ps.width, ps.height)
            val width = ps.width / gcd
            val height = ps.height / gcd

            if ((height == ASPECT_VERTICAL && width == ASPECT_HORIZONTAL) || (width == ASPECT_VERTICAL && height == ASPECT_HORIZONTAL)) {
                if (maxSize == null) {
                    maxSize = ps
                } else {
                    if (ps.height > maxSize.height && ps.width > maxSize.width) {
                        maxSize = ps
                    }
                }
            }
        }
        return maxSize ?: mPreviewSize[0]
    }
    private fun getGCD(a: Int, b: Int): Int {
        if (a < b) return getGCD(b, a)
        if (b == 0) return a
        return getGCD(b, a % b)
    }


    //ココから、カメラ閉じますよ　関数セット
    private fun close() {
        closeCamera()
    }
    private fun closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession!!.close()
            cameraCaptureSession = null
            //   cameraSessionClosed = true
        }
        if (cameraDevice != null) {
            cameraDevice!!.close()
            cameraDevice = null
        }
        if(backgroundThread !=null){
            backgroundThread!!.quitSafely()
            backgroundThread = null
        }
    }

    //ココからパーミッション系
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(baseContext)
                .setMessage("Permission Here")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        100
                    )
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .create()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        }
    }
    private fun requestRECORD_AUDIOPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            AlertDialog.Builder(baseContext)
                .setMessage("Permission Here")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        200
                    )
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .create()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 200)
        }
    }
    private fun requestWRITEStoragePermission() {
        /**
         * 書き込み権限
         */
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(baseContext)
                .setMessage("Permission Here")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        300
                    )
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .create()
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 300)
        }
    }
    private fun requestREADStoragePermission() {
        /**
         * 読み込み権限
         */
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(baseContext)
                .setMessage("Permission Here")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        400
                    )
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .create()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 400)
        }
    }



    //ダイアログ
    private fun showDialog(){

        val preference = getSharedPreferences("TEST", Context.MODE_PRIVATE)
        val memoday : Int = preference.getInt("TEST",19930615)
        val today =Calendar.getInstance()
        val date=today.getTime()

        //連続活動日数
        val memodayOnlyDay = memoday%100
        val dateFormatOnlyDay = SimpleDateFormat("dd")
        val IntDateOnlyDay =dateFormatOnlyDay.format(date).toInt()
        var ncd = 0
        val memo3 : Int = prefs.getInt(getString(R.string.preferences_key3),0)
        Log.e("TAG","今日は、$IntDateOnlyDay　日")
        Log.e("TAG","設定日は、$memodayOnlyDay 日")

        if(IntDateOnlyDay - memodayOnlyDay == 1){
            Log.e("TAG","連続日数更新！")
            ncd = memo3 +1
        }else if(IntDateOnlyDay == 1 && 28<=memodayOnlyDay){
            Log.e("TAG","連続日数更新！")
            ncd = memo3 +1
        }else if( IntDateOnlyDay == memodayOnlyDay){
            Log.e("TAG","連続日数カウントなし")
            ncd = memo3
        }else{
            Log.e("TAG","連続日数リセット")
            ncd = 0
        }

        val g : SharedPreferences.Editor = prefs.edit()
        g.putInt(getString(R.string.preferences_key3) , ncd)
        g.commit()

        //復活数
        val numb : Int = prefs.getInt(getString(R.string.preferences_key_rev),0)
        var revival = 0
        if(ncd==1){
            if( IntDateOnlyDay != memodayOnlyDay){
                revival = numb + 1
                val j : SharedPreferences.Editor = prefs.edit()
                j.putInt(getString(R.string.preferences_key_rev) , revival)
                j.commit()
            }
        }

        //継続日数の最長値を保存する
        val MAX : Int = prefs.getInt(getString(R.string.preferences_key_MAX),0)
        if(MAX < ncd){
            val updatedMAX = ncd
            val i : SharedPreferences.Editor = prefs.edit()
            i.putInt(getString(R.string.preferences_key_MAX), updatedMAX)
            i.commit()
            Log.e("TAG","最長記録更新$updatedMAX")
        }


        //総活動日数
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        val IntDate =dateFormat.format(date).toInt()//今日の日付
        var Dailytask = false
        var daycounter = 0
        Log.e("TAG","今日は、$IntDate")
        Log.e("TAG","設定日は、$memoday")
        if(memoday >= IntDate){
            Dailytask = false
        }else{
            val e : SharedPreferences.Editor = preference.edit()
            e.putInt("TEST" , IntDate)//　　　　　　　　　　　　　　　　　　　　　ここで日付の更新をしている
            e.commit()
            Dailytask = true
        }
        if(Dailytask){
            Log.e("TAG","総日数更新")
            val memo2 : Int = prefs.getInt(getString(R.string.preferences_key2),0)
            daycounter = memo2 +1
            val g : SharedPreferences.Editor = prefs.edit()
            g.putInt(getString(R.string.preferences_key2) , daycounter)
            g.commit()
        }else{
            Log.e("TAG","DailyTaskは遂行済み、総日数カウントなし")
            val memo2 : Int = prefs.getInt(getString(R.string.preferences_key2),0)
            daycounter = memo2
        }

        //総活動時間
        val memo : Int = prefs.getInt(getString(R.string.preferences_key),0)
        val newnum=memo + mTimerSec
        val seconds =newnum%60;
        val minite =(newnum/60)%60;
        val totaltime="$minite"+"分"+"$seconds"+"秒"
        val e : SharedPreferences.Editor = prefs.edit()
        e.putInt(getString(R.string.preferences_key) , newnum)
        e.commit()

        Log.e("TAG","参照に保存してる秒数$newnum")


        //復活数の調整
        var editrevaival = revival - 1
        if(editrevaival == -1){
            editrevaival = 0
        }
        //ダイアログに記録を表示
        val list = arrayOf("連続活動日数　　$ncd","復活回数　　$editrevaival","総活動日数　　$daycounter","総活動時間　　$totaltime")
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("活動の記録")
        alertDialogBuilder.setItems(list){ dialog, which ->
            Log.e("TAG", "${list[which]} が選択されました")
        }


        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("メイン画面へ"){dialog, which ->
            Log.d("UI_PARTS", "肯定ボタン")
            val intent = Intent(this,
                MainActivity::class.java)
            startActivity(intent)
        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

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
                        ChandeBack.setBackgroundColor(Color.GREEN)
                        captureButton.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }, 1000, 1000)
    }
}

 */