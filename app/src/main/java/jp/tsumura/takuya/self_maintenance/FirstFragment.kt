package jp.tsumura.takuya.self_maintenance

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharaIntroDialogFragment.Companion.showLevelUp
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import kotlinx.android.synthetic.main.activity_camera_x.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val a = (activity as AppCompatActivity?)!!.frameLayout.layoutParams as ViewGroup.MarginLayoutParams
        a.setMargins(0,5,0,100)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        val a = (activity as AppCompatActivity?)!!.frameLayout.layoutParams as ViewGroup.MarginLayoutParams
        a.setMargins(0,110,0,100)
    }

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
        val checkMini = prefs.getInt(getString(R.string.prefs_check_mini),0)
        
        if(checkMini<5){
            showView(1)//メインビューの表示
            if(user!=null){firstTasks()}
        }else{
            val docRef = db.collection("Scores").document(user!!.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (documentSnapshot.data != null && score != null) {
                    val totalPoint = score.totalPoint
                    val totalday = score.totalD

                    showView(totalday)//メインビューの表示

                    taskButton.visibility = View.VISIBLE
                    progressBar3.visibility = View.VISIBLE
                    showTask(totalday)//タスクボタンの表示

                    //レベルと通り名の設定
                    val Prelevel = CameraDialogFragment.calculate(totalPoint, 450, -450, 100)//以前までのレベル
                    levelText.text = "Lv. $Prelevel"
                    val check = prefs.getInt(getString(R.string.pref_check_point),1)
                    strName.text = StreetName(check)

                    //タスクボタンを押したときのリスナー
                    taskButton.setOnClickListener{
                        val pointList = 100*check*3*2//mutableListOf<Int>(501,100,300,00,2000,1,2,3,4,5,6,7,8,9,11,22,33,44,55,66,77,88,99,111,222,333)
                        if(check*3 <= totalday){//           クリア条件を満たしているー＞　経験値獲得
                            val new = totalPoint + pointList
                            docRef.update("totalPoint",new)


                            val newlevel = CameraDialogFragment.calculate(new, 450, -450, 100)//今回のレベル
                            Log.e("tag","これまでのレベルは$Prelevel 新しいレベル$newlevel")
                            if(Prelevel<newlevel){
                                showLevelUp(
                                    requireContext(),
                                    "レベルアップ！！",
                                    "Lv.$newlevel\n経験値　＋${pointList}"
                                )
                                levelText.text="Lv. $newlevel"
                            }else{
                                showLevelUp(
                                    requireContext(),
                                    "経験値獲得！！",
                                    "経験値　＋${pointList}"
                                )
                            }
                            val sp : SharedPreferences.Editor = prefs.edit()
                            sp.putInt(getString(R.string.pref_check_point),check+1)//
                            sp.commit()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, FirstFragment())
                                .commit()
                            //showTask(totalday)//タスクとバーを再表示しなおす
                            //strName.text=StreetName(check)

                        }else{//                                            まだクリア条件を満たせていない　ー＞　重要性、クリア条件、目標。報酬を示す

                            val title = taskButton.text.toString()
                            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            alertDialogBuilder.setTitle(title)
                            alertDialogBuilder.setMessage("クリア報酬\n経験値　　${100*(check+1)*3*2}")
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

        }


        if(wanwan(prefs)){
            wanwanImage.visibility = View.VISIBLE
        }
    }




    //　　　　　　　　　　　　　　　　　タスクボタンの表示
    fun showTask(totalday: Int){
        val check = prefs.getInt(getString(R.string.pref_check_point),1)
        var Cal =  prefs.getInt(getString(R.string.preferences_key_smalltime),0)
        var progress = 0
        if(check*3 <= totalday) {
            Cal = GoalSettingActivity.taskTimeCaluculate(check*3-1, Cal)//GoalSettingActから、適切なタスク時間を計算する
            progress=3
            textView8.visibility=View.VISIBLE
            textView8.text="達成!"
            //textView8.setTextColor(getColor(requireContext(),R.color.material_red))
        }else{
            Cal = GoalSettingActivity.taskTimeCaluculate(totalday, Cal)
            progress = totalday % 3
            if(progress==0){
                textView8.visibility=View.VISIBLE
                textView8.text="New"
                //textView8.setTextColor(getColor(requireContext(),R.color.colorPrimary))
            }else{
                textView8.visibility=View.INVISIBLE
            }
        }
        progressBar3.progress = progress
        progressBar3.max=3

        val streetName = StreetName(check+1)

        val seconds =Cal%60;
        val minite =(Cal/60)%60;
        if(Cal < 60){
            taskButton.text = "$seconds　秒をあと${3 - progress}日すると\n「$streetName」に昇格"
        }else{
            taskButton.text = "$minite 分 $seconds 秒をあと${3 - progress}日すると\n「$streetName」に昇格"
        }
        if(progressBar3.max==progressBar3.progress){
            taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
        }
    }

    fun showView(totalday:Int){
        val checkMini = prefs.getInt(getString(R.string.prefs_check_mini),0)
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
        if(checkMini<1){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou01))
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

    fun StreetName(checkPoint:Int ):String{

        val list = mutableListOf<String>(
            "","ただの三日ボウズ","ビギナー","決意をかためる者","かけだしプレイヤー",//4
            "ルーキー","期待の新生","中級者","実力派プレイヤー","上級者","全国自己メンテナンス教会　役員", "全国自己メンテナンス教会　常務",//7
            "全国自己メンテナンス教会　専務","全国自己メンテナンス教会　理事長", "全国自己メンテナンス教会　名誉会長",//3
            "習慣の成功者！！","ベテラン","習慣化の職人", "習慣化の熟練技術師","習慣化博士","習慣自動化機能付き 次世代型ヒューマン","一流メンテリスト","カリスマ メンテリスト","習慣マスター",//9
            "超一流","習慣の匠","救世主","伝説のセルフメンテリスト"//4
        )
        return  list[checkPoint]

    }

    fun firstTasks(){//初期状態のときに表示する
        val checkMini = prefs.getInt(getString(R.string.prefs_check_mini),0)
        val user = Auth.currentUser
        val docRef = db.collection("Scores").document(user!!.uid)
        progressBar3.max=100
        if (checkMini==0){
            taskButton.text="アカウントをつくる / ログインする"
            progressBar3.progress=100
            taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
        }
        if (checkMini==1){
            val small=prefs.getInt(getString(R.string.preferences_key_smalltime),0)
            taskButton.text="カメラを使ってみる（目標$small 秒間）"
            progressBar3.progress=1
            docRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.data != null ) {
                    progressBar3.progress=100
                    taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
                }
            }
        }
        if (checkMini==2){
            taskButton.text="撮影した動画を確認する"
            progressBar3.progress=1
            val TutoForGallery : Boolean = prefs.getBoolean("TutoForGallery",false)
            if(TutoForGallery){
                progressBar3.progress=100
                taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
            }
        }
        if (checkMini==3){
            taskButton.text="フレンドリストのチュートリアルを見る"
            progressBar3.progress=1
            val Tuto4 : Boolean = prefs.getBoolean("Tuto4",false)
            if(Tuto4){
                progressBar3.progress=100
                taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
            }
        }
        if (checkMini==4){
            taskButton.text="あなたの助けになってくれる\nスケットたちのことを知る"
            progressBar3.progress=1
            val Tuto5 : Boolean = prefs.getBoolean("Tuto5",false)
            if(Tuto5){
                progressBar3.progress=100
                taskButton.setBackgroundColor(getColor(requireContext(),R.color.colorPrimary))
            }
        }

        taskButton.setOnClickListener {
            if (progressBar3.progress==100){
                val script = mutableListOf<String>("メイン画面が更新されました!","動画リストがアンロックされました","フレンドリストがアンロックされました","スケットが呼べるようになりました","新たに「レベル」と「あなたの通り名」が表示されるようになりました")

                val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("ミッション達成!")//title[checkMini]
                alertDialogBuilder.setMessage(script[checkMini])
                alertDialogBuilder.setPositiveButton("閉じる"){dialog, which ->    }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

                //ミニミッションの更新
                val sp : SharedPreferences.Editor = prefs.edit()
                sp.putInt(getString(R.string.prefs_check_mini),checkMini+1)//
                sp.apply()

                //画面の再描画
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, FirstFragment())
                    .commit()
            }
        }
    }


}
