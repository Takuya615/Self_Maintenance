package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CameraDialog(context: Context){
    /**
    private var mContext: Context = context

    lateinit var executeTime: Calendar

    val db = FirebaseFirestore.getInstance()
    val totalday = 1
    val ncd = 0
    val totaltime = 0
    val addtotalday = 1
    val addncd = 0
    val addtt= 0

    val resultdata = hashMapOf(
        "Totaldays" to addtotalday,
        "NCD" to addncd,
        "Totaltime" to addtt
    )
    // AlertDialog.Builderクラスを使ってAlertDialogの準備をする
    public fun showDialog(mTimerSec:Int){

        //Firebaseから値を取り出す
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }

        val totaltime= addtt+ mTimerSec
        val seconds =totaltime%60;
        val minite =(totaltime/60)%60;
        val total="$minite"+"分"+"$seconds"+"秒"


        //総日数を求める
        var daycounter = 1
        val today= Calendar.getInstance()//今日の日付
        Log.e("TAG","今日の日付は$today")

        var fla = true
        if(fla){
            val execute = Calendar.getInstance()
            executeTime = execute//前回写真を上げた時の日数
            Log.e("TAG","初めての利用")
            Log.e("TAG","前回の日付$executeTime")
            fla = false
        }
        val day = Calendar.getInstance().get(Calendar.DATE)
        val executeday = executeTime!!.get(Calendar.DATE)

        if(day==executeday){
            Log.e("TAG","デイリー終了")
        }else{
            Log.e("TAG","本日のデイリーはまだ")
            daycounter ++
            val execute = Calendar.getInstance()
            executeTime = execute//前回写真を上げた時の日数

        }



        val list = arrayOf("連続活動日数　　","総活動日数  ","総活動時間　　$total")
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("活動の記録")
        alertDialogBuilder.setItems(list){ dialog, which ->
            Log.e("TAG", "${list[which]} が選択されました")
        }


        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("メイン画面へ"){dialog, which ->
            Log.d("UI_PARTS", "肯定ボタン")
            val intent =Intent(mContext,MainActivity::class.java)
            startActivity(intent)
        }

        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()


        val resultdata = hashMapOf(
            "Totaldays" to totalday,
            "NCD" to ncd,
            "Totaltime" to totaltime
        )

        //Firebaseに保存
        db.collection("ResultDatas")
            .add(resultdata)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "総日数、連続日数、総時間のセーブ")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
    */
}