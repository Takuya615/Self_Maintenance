package jp.tsumura.takuya.self_maintenance.ForSetting

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_character_list_item.view.*

//ダイアログでお知らせだけする
class SettingDialog{
    //スモールステップ説明用ダイアログ
    fun showDialog(mContext:Context){

        val iv = ImageView(mContext)
        iv.setImageResource(R.drawable.chart)
        //ダイアログに記録を表示
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("少しずつ時間がのびます")
        alertDialogBuilder.setView(iv)
        alertDialogBuilder.setMessage(R.string.Tutorial1)

        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("閉じる",{dialog, which -> })
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //実績解除のお知らせ用
    fun showDialogForMedal(mContext:Context){

        val iv = ImageView(mContext)
        iv.setImageResource(R.drawable.medal_2)
        //ダイアログに記録を表示
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("実 績 更 新")
        alertDialogBuilder.setMessage("おめでとうございます")
        alertDialogBuilder.setView(iv)
        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("閉じる"){dialog, which ->        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    fun showTimePickerDialog(context:Context) {
        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                val prefs = context.getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
                val save : SharedPreferences.Editor = prefs.edit()
                val b=(hour*60+minute).toString()

                Log.e("tag","1wanwanに入れた値は$b")
                save.putString("wanwan", b)
                save.apply()
                Toast.makeText(context,"$hour 時$minute 分に設定しました",Toast.LENGTH_SHORT).show()
            },
            13, 0, true)
        timePickerDialog.show()
    }

}