package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.forGallery.UriListActivity
import java.text.SimpleDateFormat
import java.util.*

class SettingDialog{
    //ダイアログ
    fun showDialog(mContext:Context){

        val iv = ImageView(mContext)
        iv.setImageResource(R.drawable.smallstep)
        //ダイアログに記録を表示
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("スモールステップについて")
        alertDialogBuilder.setMessage(R.string.Tutorial)
        alertDialogBuilder.setView(iv)
        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("閉じる"){dialog, which ->
            Log.e("TAG","スモールステップの説明を閉じる")
        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }
}