package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Firebase {

//fire RealtimeDatabase
    fun WriteToStore(fileName:String){

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if(user!=null){
        val docRef = db.collection(user.uid)
        val date= Calendar.getInstance().getTime()
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH時mm分")
        val StrDate =dateFormat.format(date).toString()
        val data = hashMapOf(
            "friend" to false,
            "uri" to fileName,
            "date" to StrDate
        )

        docRef.document(StrDate).set(data)
            .addOnSuccessListener { Log.e("TAG", "動画作成日とURIの保存成功") }
            .addOnFailureListener { e -> Log.e("TAG", "動画作成日とURIの保存成功", e) }
    }
    }

}
