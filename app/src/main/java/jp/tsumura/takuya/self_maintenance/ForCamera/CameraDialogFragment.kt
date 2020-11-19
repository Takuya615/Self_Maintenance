package jp.tsumura.takuya.self_maintenance.ForCamera

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_camera.view.*
import java.lang.Math.sqrt
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
            requireActivity().finish()
            //val intent = Intent(requireContext(), MainActivity::class.java)
            //requireContext().startActivity(intent)
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
        var point = 0.0//スコア
        var newTotP=0//総スコア
        val prefs = requireContext().getSharedPreferences(
            "preferences_key_sample",
            Context.MODE_PRIVATE
        )
        val save : SharedPreferences.Editor = prefs.edit()

        val setday : String? = prefs.getString("setDate", "2020-10-28")//前回利用した日
        val now = LocalDate.now() //2019-07-28T15:31:59.754
        val day1 = LocalDate.parse(setday)//2019-08-28T10:15:30.123
        val different = ChronoUnit.DAYS.between(day1, now).toInt() // diff: 30


        if(user!=null){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)

                if(documentSnapshot.data != null&&score!=null){
                    val continuous = score.continuous
                    val recover = score.recover
                    val totalD = score.totalD
                    val totalT = score.totalT
                    var DoNot= score.DoNot
                    val totalPoint = score.totalPoint

                    val listRandam = arrayOf(1.5, 1.25, 1.0, 0.75, 0.5)//スコアのランダム要素
                    val ran = listRandam.random()

                    if(different == 1){
                        newCon = continuous + 1//継続日数
                        newRec = recover//復活数
                        newtot = totalD + 1//総日数
                        point=100*newtot*ran//その日のスコア値

                    }else if(different >= 2){
                        newCon = 0//継続リセット
                        newRec = recover + 1//復活数
                        newtot = totalD + 1//総日数
                        DoNot = DoNot + different-1
                        point=100*newtot*ran//その日のスコア値

                    }else if(different==0){
                        newCon = continuous//継続日数
                        newRec = recover//復活数
                        newtot = totalD//総日数


                    }

                    //継続日数の最長値を保存する
                    val MAX : Int = prefs.getInt("preferences_key_MAX", 0)
                    if(MAX < newCon){
                        val updatedMAX = newCon
                        save.putInt("preferences_key_MAX", updatedMAX)
                    }

                    //トータル経験値の算出
                    newTotP = totalPoint + point.toInt()

                    Log.e("TAG","ランダムは$ran")
                    Log.e("TAG","総日数はは$newtot")
                    Log.e("TAG","経験値は$point")
                    Log.e("TAG","とーたる経験値は$newTotP")
                    val newnum=totalT + time//総活動時間
                    save.putInt("totalday", newtot)//総日数だけプレファレンスにも保存

                    val data = Score(newCon, newRec, newtot, newnum, DoNot, newTotP)
                    docRef.set(data)
                }else{

                    point=100.0
                    val data = Score(0, 0, 1, time, 0, 100)
                    docRef.set(data)
                    save.putInt("totalday", 1)
                }
                //save.putString("TEST",today)//設定日の更新
                save.putString("setDate", now.toString())
                save.apply()

                val level =calculate(newTotP,450,-450,100)

                customView.revel.text = "                             Lv. $level"
                customView.text.text ="     継続日数　  　$newCon 日"
                customView.text2.text ="     復活回数　　 $newRec 回"
                customView.score.text = "     経験値              ${point.toInt()}"


                //ココからメダルの表示
                val maxT = mutableListOf(1, 2, 5, 7, 10, 15, 20, 25, 30)//総日数
                val maxC = mutableListOf(1, 2, 5, 7, 10, 15, 20, 25, 30)//継続
                val maxR = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)//復活
                val result = mutableListOf<String>()
                for(i in maxT){
                    if(i == newtot){
                        result.add("総日数 $i 日")
                    }
                }
                for(i in maxC){
                    if(i == newCon){
                        result.add("継続日数 $i 日")
                    }
                }
                if(different >= 2) {//復活回数が更新されているときにのみ表示する
                    for(i in maxR){
                        if(i == newRec){
                            result.add("復活数　$i 日")
                        }
                    }
                }

                customView.cameraDialogRecyclerView.layoutManager = GridLayoutManager(
                    requireContext(),
                    3
                )
                customView.cameraDialogRecyclerView.adapter = CameraDialogAdapter(result)
                customView.cameraDialogRecyclerView.setHasFixedSize(true)

            }
        }else{
            customView.revel.text = ""
            customView.text.text = "エラー"
            customView.text2.text ="ログインすればスコアを確認できます"
            customView.score.text = ""
        }

    }

    fun calculate(y:Int,a:Int,b:Int,c:Int):Int{
        //二次関数の解の公式
        val x = (-(b) + sqrt((b*b - 4 * a * (c - y)).toDouble())) / (2 * a)
        return x.toInt()
    }
}


