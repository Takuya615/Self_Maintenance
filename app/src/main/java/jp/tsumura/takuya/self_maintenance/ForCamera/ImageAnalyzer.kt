package jp.tsumura.takuya.self_maintenance.ForCamera


/*
運動の強度を計測しようとしたけど、CameraXではビデオでアナリシスが使えないみたい？？
とりあえず保留。
 */

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

private class ImageAnalyzer : ImageAnalysis.Analyzer {

    //ポーズ検出のインスタンス
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()
    val poseDetector = PoseDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy,rotationDegrees:Int) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)//imageProxy.imageInfo.rotationDegrees
            // Pass image to an ML Kit Vision API
            // ...
             //val result: Task<Pose> =
            poseDetector.process(image)
                .addOnSuccessListener { results ->
                    // Task completed successfully
                    // ...
                    Log.e("TAG","resultの結果は$results")
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Log.e("TAG","resultの取得に失敗")
                }


        }
    }
}

