package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Firebase {


    fun WriteToRealtime(context:Context,file:File){
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val genreRef = dataBaseReference.child("URIlists")

        val data = HashMap<String, String>()
        val uri = Uri.fromFile(file).toString()
        data["uri"] = uri

        val date= Calendar.getInstance().getTime()
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH時mm分")
        val StrDate =dateFormat.format(date).toString()
        data["date"]=StrDate

        genreRef.push().setValue(data)
    }

    fun SaveVideoToStorage(file:File?){
        val storage = Firebase.storage
        val storageRef = storage.reference
        val photoRef = storageRef.child("images")
        val movieUri = Uri.fromFile(file)
        val uploadTask = photoRef.putFile(movieUri)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Log.e("TAG","ストレージへ保存失敗")
        }.addOnSuccessListener {
            Log.e("TAG","ストレージへ保存成功")
        }
    }
}