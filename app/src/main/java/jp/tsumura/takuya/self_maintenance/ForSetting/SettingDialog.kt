package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import jp.tsumura.takuya.self_maintenance.R

class SettingDialog{
    //スモールステップ説明用ダイアログ
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

    //フレンドリクエスト用ダイアログ

    fun showDialogForRequest(mContext:Context,requestName:String){


        //val iv = ImageView(mContext)
        //iv.setImageResource(R.drawable.smallstep)
        //ダイアログに記録を表示
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("$requestName　さんからフレンドリクエストが届いてます")
        alertDialogBuilder.setMessage("フレンドになると、その人の日々のミッションを応援してあげることができます\n\nリクエストを承認しますか？")
        //alertDialogBuilder.setView(iv)
        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("承認する"){dialog, which ->
            Log.e("TAG","スモールステップの説明を閉じる")

        }
        alertDialogBuilder.setNegativeButton("見なかったことにする"){dialog, which ->
            Log.e("TAG","スモールステップの説明を閉じる")
        }
        // AlertDialogを作成して表示する
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }
}