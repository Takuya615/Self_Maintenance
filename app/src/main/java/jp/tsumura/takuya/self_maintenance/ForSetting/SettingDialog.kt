package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import jp.tsumura.takuya.self_maintenance.R
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

}