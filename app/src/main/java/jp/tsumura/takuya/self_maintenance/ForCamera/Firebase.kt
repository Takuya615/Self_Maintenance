package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Firebase {

//fire RealtimeDatabase
    fun WriteToRealtime(fileName:String){
        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        if(user!=null){
            val genreRef = dataBaseReference.child(user.uid)

            val data = HashMap<String, String>()
            //val uri = Uri.fromFile(file).toString()
            data["uri"] = fileName

            val date= Calendar.getInstance().getTime()
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH時mm分")
            val StrDate =dateFormat.format(date).toString()
            data["date"]=StrDate

            genreRef.push().setValue(data)
        }

    }

//Fire Store
    fun WriteToRealtime2(fileName:String){
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        if(user!=null){
            val docRef = db.collection("VideoURIs").document(user.uid)
            val data = HashMap<String, String>()
            //val uri = Uri.fromFile(file).toString()
            data["uri"] = fileName

            val date= Calendar.getInstance().getTime()
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日HH時mm分")
            val StrDate =dateFormat.format(date).toString()
            data["date"]=StrDate

            docRef.set(data, SetOptions.merge())
                .addOnSuccessListener { Log.e("TAG", "動画作成日とURIの保存成功") }
                .addOnFailureListener { e -> Log.e("TAG", "動画作成日とURIの保存成功", e) }
        }

    }

}