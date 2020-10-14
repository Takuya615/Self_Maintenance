package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.forGallery.VideoListActivity
import java.text.SimpleDateFormat
import java.util.*

class CameraDialog(){

    //ダイアログ
    fun showDialog(mContext: Context,mTimerSec:Int){

        val prefs = mContext.getSharedPreferences( "preferences_key_sample",Context.MODE_PRIVATE)
        val save : SharedPreferences.Editor = prefs.edit()
        var newCon:Int = 0
        var newRec:Int = 0

        val fromday : String? = prefs.getString("TEST","1993/06/15")//前回利用した日
        val date =Calendar.getInstance().getTime()
        val dateFormatOnlyDay = SimpleDateFormat("yyyy/MM/dd")
        val today =dateFormatOnlyDay.format(date).toString()
        val different = dateDiff(today,fromday)
        if(different == 1){
            //継続日数
            val Continue : Int = prefs.getInt("continue",0)
            newCon = Continue + 1
            save.putInt("continue", newCon)
            //復活数
            val recover: Int = prefs.getInt("recover",0)
            newRec = recover
            //総日数
            val total: Int = prefs.getInt("totalday",0)
            val newtot = total + 1
            save.putInt("totalday", newtot)

        }else if(different < 2){
            //継続リセット
            newCon = 0
            save.putInt("continue", newCon)
            //復活数
            val recover : Int = prefs.getInt("recover",-1)
            newRec = recover + 1
            save.putInt("recover", newRec)
            //総日数
            val total: Int = prefs.getInt("totalday",0)
            val newtot = total + 1
            save.putInt("totalday", newtot)

        }else if(different==0){
            //継続日数
            val Continue : Int = prefs.getInt("continue",0)
            newCon = Continue
            //復活数
            val recover: Int = prefs.getInt("recover",0)
            newRec = recover
        }

        //継続日数の最長値を保存する
        val MAX : Int = prefs.getInt("preferences_key_MAX",0)
        if(MAX < newCon){
            val updatedMAX = newCon
            save.putInt("preferences_key_MAX", updatedMAX)
        }

        //総活動時間
        val totaltime : Int = prefs.getInt("totaltime",0)
        val newnum=totaltime + mTimerSec
        save.putInt("totaltime" , newnum)

        save.apply()

        //ダイアログに記録を表示
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
        alertDialogBuilder.setNegativeButton("動画リスト"){dialog, which ->
            val intent = Intent(mContext, VideoListActivity::class.java)
            mContext.startActivity(intent)
        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //差が０ー＞クリア済み、１－＞継続日数と総日数、2以上ー＞復帰回数と総日数（サボった日数更新する）
    fun dateDiff(dateToStr:String,dateFromStr:String?):Int{
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        var dateFrom:Date? =null
        var dateTo:Date? = null
        dateFrom = sdf.parse(dateFromStr)
        dateTo = sdf.parse(dateToStr)

        val To = dateTo.time
        val From = dateFrom.time
        val dayDiff = (To - From)
        Log.e("TAG","dayDiffは${dayDiff}")
        return dayDiff.toInt()
    }



}