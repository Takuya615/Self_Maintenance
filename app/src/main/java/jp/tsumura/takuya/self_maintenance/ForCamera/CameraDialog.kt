package jp.tsumura.takuya.self_maintenance.ForCamera




//このコードは消さずにおいておくこと。　　テストプレーしてもらう時に使える


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListAdapter
import jp.tsumura.takuya.self_maintenance.forGallery.VideoListActivity
import kotlinx.android.synthetic.main.activity_achievement.*
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_friend_list.view.*
import kotlinx.android.synthetic.main.dialog_camera.*
import kotlinx.android.synthetic.main.dialog_camera.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class CameraDialog(){
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()


    //ダイアログ
    fun showDialog(mContext: Context,mTimerSec:Int,activity:Activity){


/*
        val fromday : String? = prefs.getString("TEST","1993/06/15")//前回利用した日
        val date =Calendar.getInstance().getTime()
        val dateFormatOnlyDay = SimpleDateFormat("yyyy/MM/dd")
        val today =dateFormatOnlyDay.format(date).toString()
        val different = dateDiff(today,fromday)

 */


        val prefs = mContext.getSharedPreferences( "preferences_key_sample",Context.MODE_PRIVATE)
        val save : SharedPreferences.Editor = prefs.edit()
        var newCon:Int = 0
        var newRec:Int = 0
        var newtot:Int = 0

        val setday : String? = prefs.getString("setDate","2020-10-28")//前回利用した日
        val now = LocalDate.now() //2019-07-28T15:31:59.754
        val day1 = LocalDate.parse(setday)//2019-08-28T10:15:30.123
        val different = ChronoUnit.DAYS.between(day1, now).toInt() // diff: 30
        Log.e("TAG","今日は$now 設定日は$day1 dayDiffは${different}")



        if(user!=null){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if(documentSnapshot.data != null&&score!=null){
                    val continuous = score.continuous
                    val recover = score.recover
                    val totalD = score.totalD
                    val totalT = score.totalT

                    if(different == 1){
                        newCon = continuous + 1//継続日数
                        newRec = recover//復活数
                        newtot = totalD + 1//総日数
                        Log.e("TAG", "連続日数更新！")
                        //val Continue : Int = prefs.getInt("continue",0)
                        //save.putInt("continue", newCon)
                        //val recover: Int = prefs.getInt("recover",0)
                        //val total: Int = prefs.getInt("totalday",0)
                    }else if(different >= 2){
                        newCon = 0//継続リセット
                        newRec = recover + 1//復活数
                        newtot = totalD + 1//総日数
                        Log.e("TAG", "復帰！")

                        //save.putInt("continue", newCon)
                        //val recover : Int = prefs.getInt("recover",-1)
                        //save.putInt("recover", newRec)
                        //val total: Int = prefs.getInt("totalday",0)
                        //save.putInt("totalday", newtot)
                    }else if(different==0){
                        newCon = continuous//継続日数
                        newRec = recover//復活数
                        newtot = totalD//総日数
                        Log.e("TAG", "デイリー達成済み")
                        //val Continue : Int = prefs.getInt("continue",0)
                        //val recover: Int = prefs.getInt("recover",0)
                    }

                    //継続日数の最長値を保存する
                    val MAX : Int = prefs.getInt("preferences_key_MAX",0)
                    if(MAX < newCon){
                        val updatedMAX = newCon
                        save.putInt("preferences_key_MAX", updatedMAX)
                    }

                    //val totaltime : Int = prefs.getInt("totaltime",0)
                    val newnum=totalT + mTimerSec//総活動時間
                    //save.putInt("totaltime" , newnum)

                    save.putInt("totalday", newtot)//総日数だけプレファレンスにも保存

                    Log.e("TAG","連続$newCon、復活$newRec、総日$newtot")
                    val data = Score(newCon,newRec,newtot,newnum)
                    docRef.set(data)
                }else{
                    //val docRef = db.collection("Scores").document(user.uid)
                    val data = Score(0,0,1,mTimerSec)
                    docRef.set(data)
                    save.putInt("totalday", 1)
                }

                //save.putString("TEST",today)//設定日の更新
                save.putString("setDate",now.toString())
                save.apply()
                //ダイアログに記録を表示
                // アダプターとレイアウトマネージャーをセット
                val list = arrayOf("継続日数　　$newCon","復活回数　　$newRec")
                val alertDialogBuilder = AlertDialog.Builder(mContext)
                //alertDialogBuilder.setTitle("活動の記録")
                alertDialogBuilder.setItems(list){ dialog, which ->
                    Log.e("TAG", "${list[which]} が選択されました")
                }
                // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
                alertDialogBuilder.setPositiveButton("メイン画面"){dialog, which ->
                    val intent = Intent(mContext,MainActivity::class.java)
                    mContext.startActivity(intent)
                }
                /*
                alertDialogBuilder.setNegativeButton("動画リスト"){dialog, which ->
                    val intent = Intent(mContext, VideoListActivity::class.java)
                    mContext.startActivity(intent)
                }

                 */
                // AlertDialogを作成して表示する
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }
        }else{
            val alertDialogBuilder = AlertDialog.Builder(mContext)
            alertDialogBuilder.setTitle("ログインすれば、スコアを記録できます")
            alertDialogBuilder.setPositiveButton("メイン画面"){dialog, which ->
                val intent = Intent(mContext,MainActivity::class.java)
                mContext.startActivity(intent)
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
/*
    //差が０ー＞クリア済み、１－＞継続日数と総日数、2以上ー＞復帰回数と総日数（サボった日数更新する）
    fun dateDiff(dateToStr:String,dateFromStr:String?):Int{
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        var dateFrom:Date? =null
        var dateTo:Date? = null
        dateFrom = sdf.parse(dateFromStr)
        dateTo = sdf.parse(dateToStr)

        val To = dateTo.time//今日20201030
        val From = dateFrom.time//設定日20201029
        val dayDiff = (To - From)
        Log.e("TAG","今日は$To 設定日は$From dayDiffは${dayDiff}")
        return dayDiff.toInt()
    }

 */



}

