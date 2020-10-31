package jp.tsumura.takuya.self_maintenance.ForCamera

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_camera.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CameraDialogFragment(mTimerSec: Int): DialogFragment() {
    lateinit var customView :View
    val time = mTimerSec


    override fun onCreateDialog(savedInstanceState: Bundle?):Dialog {
        customView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_camera, null)
        //list = arrayOf<String>()

        //recordScore(requireContext(),time)
        val builder = AlertDialog.Builder(activity)
        //alertDialogBuilder.setTitle("活動の記録")
        //builder.setItems(list){ dialog, which -> Log.e("TAG", "${list[which]} が選択されました") }
        // 肯定ボタンに表示される文字列、押したときのリスナーを設定する
        builder.setPositiveButton("メイン画面"){ dialog, which ->
            val intent = Intent(requireContext(), MainActivity::class.java)
            requireContext().startActivity(intent)
        }
        builder.setView(customView)

        return builder.create()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView//inflater.inflate(R.layout.dialog_camera, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        var newCon:Int = 0//連続
        var newRec:Int = 0//復活
        var newtot:Int = 0//総日数

        val prefs = requireContext().getSharedPreferences( "preferences_key_sample",Context.MODE_PRIVATE)
        val save : SharedPreferences.Editor = prefs.edit()

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
                    val newnum=totalT + time//総活動時間
                    //save.putInt("totaltime" , newnum)

                    save.putInt("totalday", newtot)//総日数だけプレファレンスにも保存

                    Log.e("TAG","連続$newCon、復活$newRec、総日$newtot")
                    val data = Score(newCon,newRec,newtot,newnum)
                    docRef.set(data)
                }else{
                    //val docRef = db.collection("Scores").document(user.uid)
                    val data = Score(0,0,1,time)
                    docRef.set(data)
                    save.putInt("totalday", 1)
                }
                //save.putString("TEST",today)//設定日の更新
                save.putString("setDate",now.toString())
                save.apply()

                customView.text.text ="継続日数　　$newCon"
                customView.text2.text ="復活回数　　$newRec"


                val titT = mutableListOf("総日数1日達成","総日数2日達成","総日数5日達成","総日数7日達成","総日数10日達成","総日数15日達成",
                "総日数20日達成","総日数25日達成","総日数30日達成"
            )
                val titC = mutableListOf("継続日数1日達成","継続日数2日達成","継続日数5日達成","継続日数7日達成",
                    "継続日数10日達成","継続日数15日達成","継続日数20日達成","継続日数25日達成","継続日数30日達成"
                )
                val titR = mutableListOf("復活回数1回達成",
                    "復活回数2回達成","復活回数3回達成","復活回数4回達成","復活回数5回達成","復活回数6回達成","復活回数7回達成","復活回数8回達成",
                    "復活回数9回達成",
                )
                val maxT = mutableListOf(1,2,5,7,10,15,20,25,30)//総日数
                val maxC = mutableListOf(1,2,5,7,10,15,20,25,30)
                val maxR = mutableListOf(1,2,3,4,5,6,7,8,9)//復活
                val result = mutableListOf<String>()
                for(i in maxT){
                    if(i == newtot){
                        result.add(titT[i])
                    }
                }
                for(i in maxC){
                    if(i == newCon){
                        result.add(titC[i])
                    }
                }
                for(i in maxR){
                    if(i == newRec){
                        result.add(titR[i])
                    }
                }

                //makeResultList(resultList)
                customView.cameraDialogRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
                customView.cameraDialogRecyclerView.adapter = CameraDialogAdapter(result)
                customView.cameraDialogRecyclerView.setHasFixedSize(true)

            }
        }else{
            customView.text.text = "エラー"
            customView.text2.text ="ログインすればスコアを確認できます"
        }

    }

}


