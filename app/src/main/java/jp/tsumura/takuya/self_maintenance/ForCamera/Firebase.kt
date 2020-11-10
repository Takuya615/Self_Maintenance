package jp.tsumura.takuya.self_maintenance.ForCamera

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class Firebase {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    fun WriteToStore(fileName:String,path:Long){
    if(user!=null){
        val docRef = db.collection(user.uid)
        val date= Calendar.getInstance().getTime()
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH時mm分")
        val StrDate =dateFormat.format(date).toString()
        val data = hashMapOf(
            "friend" to false,
            "uri" to fileName,
            "date" to StrDate,
            "like" to 0,
            "path" to path
        )

        docRef.document(StrDate).set(data)
            //.addOnSuccessListener { Log.e("TAG", "動画作成日とURIの保存成功") }
            //.addOnFailureListener { e -> Log.e("TAG", "保存失敗", e) }
    }
    }






}
