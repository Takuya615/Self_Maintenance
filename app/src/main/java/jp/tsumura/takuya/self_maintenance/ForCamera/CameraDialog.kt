package jp.tsumura.takuya.self_maintenance.ForCamera

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.appcompat.app.AlertDialog
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.forGallery.UriListActivity
import java.text.SimpleDateFormat
import java.util.*

class CameraDialog(context: Context,mTimerSec:Int){

    private var mTimerSec:Int = mTimerSec
    private var mContext: Context = context
    private var prefs = mContext.getSharedPreferences( "preferences_key_sample",Context.MODE_PRIVATE)

    //ダイアログ
    fun showDialog(){

        val preference = mContext.getSharedPreferences("TEST", Context.MODE_PRIVATE)
        val memoday : Int = preference.getInt("TEST",19930615)
        val today =Calendar.getInstance()
        val date=today.getTime()

        //連続活動日数
        val memodayOnlyDay = memoday%100
        val dateFormatOnlyDay = SimpleDateFormat("dd")
        val IntDateOnlyDay =dateFormatOnlyDay.format(date).toInt()
        var ncd = 0
        val memo3 : Int = prefs.getInt("preferences_key3",0)
        Log.e("TAG","今日は、$IntDateOnlyDay　日")
        Log.e("TAG","設定日は、$memodayOnlyDay 日")

        val numb : Int = prefs.getInt("preferences_key_rev",-1)//復活回数
        var revival = 0//                                        復活回数

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
            revival = numb + 1//                                 　復活回数
            val j : SharedPreferences.Editor = prefs.edit()//　　　　復活回数
            j.putInt("preferences_key_rev", revival)//　　　　　　　　　　復活回数
            j.commit()// 　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　復活回数
        }
        val g : SharedPreferences.Editor = prefs.edit()
        g.putInt("preferences_key3", ncd)
        g.commit()

        //継続日数の最長値を保存する
        val MAX : Int = prefs.getInt("preferences_key_MAX",0)
        if(MAX < ncd){
            val updatedMAX = ncd
            val i : SharedPreferences.Editor = prefs.edit()
            i.putInt("preferences_key_MAX", updatedMAX)
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
            e.putInt("TEST" , IntDate)//　　　　　　　　　　　　　　　　　　　　　　　　設定日の更新
            e.commit()
            Dailytask = true
        }
        if(Dailytask){
            Log.e("TAG","総日数更新")
            val memo2 : Int = prefs.getInt("preferences_key2",0)
            daycounter = memo2 +1
            val g : SharedPreferences.Editor = prefs.edit()
            g.putInt("preferences_key2" , daycounter)
            g.commit()
        }else{
            Log.e("TAG","DailyTaskは遂行済み、総日数カウントなし")
            val memo2 : Int = prefs.getInt("preferences_key2",0)
            daycounter = memo2
        }

        //総活動時間
        val memo : Int = prefs.getInt("preferences_key",0)
        val newnum=memo + mTimerSec
        val seconds =newnum%60;
        val minite =(newnum/60)%60;
        val totaltime="$minite"+"分"+"$seconds"+"秒"
        val e : SharedPreferences.Editor = prefs.edit()
        e.putInt("preferences_key" , newnum)
        e.commit()

        Log.e("TAG","参照に保存してる秒数$newnum")

        //ダイアログに記録を表示
        val list = arrayOf("連続活動日数　　$ncd","復活回数　　$revival","総活動日数　　$daycounter")
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("活動の記録")
        alertDialogBuilder.setItems(list){ dialog, which ->
            Log.e("TAG", "${list[which]} が選択されました")
        }
        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("メイン画面"){dialog, which ->
            val intent = Intent(mContext,MainActivity::class.java)
            mContext.startActivity(intent)
        }
        alertDialogBuilder.setNegativeButton("動画リスト"){dialog, which ->
            val intent = Intent(mContext, UriListActivity::class.java)
            mContext.startActivity(intent)
        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }
}