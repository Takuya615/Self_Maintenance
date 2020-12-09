package jp.tsumura.takuya.self_maintenance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharaIntroDialogFragment
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharaIntroDialogFragment.Companion.showLevelUp
import jp.tsumura.takuya.self_maintenance.ForMedals.AchievementAdapter
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import kotlinx.android.synthetic.main.activity_goal_setting.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var prefs : SharedPreferences
    private lateinit var Auth: FirebaseAuth
    private lateinit var db :FirebaseFirestore

    //override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        db = FirebaseFirestore.getInstance()
        Auth = FirebaseAuth.getInstance()
        val user = Auth.currentUser

        if (user!=null){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (documentSnapshot.data != null && score != null) {
                    val totalPoint = score.totalPoint
                    val totalday = score.totalD

                    taskButton.visibility = View.VISIBLE
                    progressBar3.visibility = View.VISIBLE
                    showView(totalday)//メインビューの表示

                    showTask(totalday)//タスクボタンの表示

                    //タスクボタンを押したときのリスナー
                    taskButton.setOnClickListener{

                        val check = prefs.getInt(getString(R.string.pref_check_point),1)
                        val pointList = mutableListOf<Int>(0,500,900,1400,2000,1,2,3,4,5,6,7,8,9,11,22,33,44,55,66,77,88,99,111,222,333)
                        if(check*3 <= totalday){//           クリア条件を満たしているー＞　経験値獲得
                            val new = totalPoint + pointList[check]
                            docRef.update("totalPoint",new)

                            val Prelevel = CameraDialogFragment.calculate(totalPoint, 450, -450, 100)//以前までのレベル
                            val newlevel = CameraDialogFragment.calculate(new, 450, -450, 100)//今回のレベル
                            Log.e("tag","これまでのレベルは$Prelevel 新しいレベル$newlevel")
                            if(Prelevel<newlevel){
                                showLevelUp(
                                    requireContext(),
                                    "レベルアップ！！",
                                    "Lv.$newlevel\n経験値　＋${pointList[check]}"
                                )
                            }else{
                                showLevelUp(
                                    requireContext(),
                                    "経験値獲得！！",
                                    "経験値　＋${pointList[check]}"
                                )
                            }
                            val sp : SharedPreferences.Editor = prefs.edit()
                            sp.putInt(getString(R.string.pref_check_point),check+1)//
                            sp.apply()
                            showTask(totalday)//タスクとバーを再表示しなおす

                        }else{//                                            まだクリア条件を満たせていない　ー＞　重要性、クリア条件、目標。報酬を示す
                            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            alertDialogBuilder.setTitle("aisuoj")
                            alertDialogBuilder.setMessage("aaaaaaaaaaa")
                            alertDialogBuilder.setPositiveButton("閉じる"){dialog, which ->        }
                            val alertDialog = alertDialogBuilder.create()
                            alertDialog.show()




                            //テスト用　総日数分まで報酬を表示しきってしまったら、一つ前に戻す　　　テスト用　注意
                            val sp : SharedPreferences.Editor = prefs.edit()
                            sp.putInt(getString(R.string.pref_check_point),1)
                            sp.apply()
                        }
                    }

                }
            }
        }else{
            showView(0)
            taskButton.visibility = View.INVISIBLE
            progressBar3.visibility = View.INVISIBLE
        }

        if(wanwan(prefs)){
            wanwanImage.visibility = View.VISIBLE
        }
    }




    fun showTask(totalday: Int){
        val check = prefs.getInt(getString(R.string.pref_check_point),1)
        var Cal =  prefs.getInt(getString(R.string.preferences_key_smalltime),0)
        var progress = 0

        if(check*3 <= totalday) {
            Cal = GoalSettingActivity.taskTimeCaluculate(check*3-1, Cal)//GoalSettingActから、適切なタスク時間を計算する
            progress=3
            textView8.visibility=View.VISIBLE
            textView8.text="達成!"
            textView8.setTextColor(getColor(requireContext(),R.color.material_red))
        }else{
            Cal = GoalSettingActivity.taskTimeCaluculate(totalday, Cal)
            if(totalday>48){
                progress = (totalday-48) % 3
            }else{
                progress = totalday % 3
            }
            if(progress==0){
                textView8.visibility=View.VISIBLE
                textView8.text="New"
                textView8.setTextColor(getColor(requireContext(),R.color.colorPrimary))
            }else{
                textView8.visibility=View.INVISIBLE
            }
        }
        progressBar3.progress = progress
        progressBar3.max=3

        val seconds =Cal%60;
        val minite =(Cal/60)%60;
        if(Cal < 60){
            taskButton.text = "$seconds　秒間を３日間だけする。"
        }else{
            taskButton.text = "$minite 分 $seconds 秒間を３日間だけする。"
        }

    }

    fun showView(totalday:Int){
        val growthimage =view?.findViewById<ImageView>(R.id.growth_image)

        if(1<=totalday && totalday<3){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou02))
        }
        if(3<=totalday && totalday<5){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou03))
        }
        if(5<=totalday && totalday<7){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou04))
        }
        if(7<=totalday && totalday<12){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou05))
        }
        if(12<=totalday && totalday<17){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou06))
        }
        if(17<=totalday && totalday<23){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou07))
        }
        if(23<=totalday && totalday<30){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou08))
        }
        if(30<=totalday){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou09))
        }
    }


    fun wanwan(prefs : SharedPreferences):Boolean{
        val wanwan=prefs.getString("wanwan","")
        if(wanwan!=null&&wanwan.isNotEmpty()){
            val setday : String? ="2020-10-28"// prefs.getString("setDate", "2020-10-28")//前回利用した日
            val now = LocalDate.now() //2019-07-28T15:31:59.754
            val day1 = LocalDate.parse(setday)//2019-08-28T10:15:30.123
            val different = ChronoUnit.DAYS.between(day1, now).toInt() // diff: 30
            if(different!=0){
                val sdf = SimpleDateFormat("HH")
                val sdf2 = SimpleDateFormat("mm")
                val hour = sdf.format(System.currentTimeMillis())
                val min = sdf2.format(System.currentTimeMillis())

                Log.e("TAG","今の時間を表すと$hour:$min")
                Log.e("TAG","wanwanは$wanwan")
                val a = wanwan.toInt() - hour.toInt()*60 - min.toInt() + 15//設定した分時 －今の時（分表記）－今の分＋アソビ15分
                Log.e("TAG","与えられる式は${wanwan}-${hour.toInt()*60}-${min.toInt()}")
                Log.e("TAG","その差は$a")
                if(0<a&&30>a){//設定が１２時なら、１１：４５～１２：１５が、使える時間

                    return true
                }
            }
            return false
        }
        return false
    }

}