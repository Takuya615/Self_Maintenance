package jp.tsumura.takuya.self_maintenance.ForMedals

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.recycler_view.*

class AchievementFragment : Fragment(){

    private lateinit var adapter : AchievementAdapter
    private lateinit var titleList:MutableList<String>
    private lateinit var maxList:MutableList<Int>
    private lateinit var progressList:MutableList<Int>
    private lateinit var hideButton:MutableList<Boolean>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.recycler_view, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleList = mutableListOf()
        progressList = mutableListOf()
        hideButton = mutableListOf()
        maxList = mutableListOf(1,2,5,7,10,15,20,25,30)//総日数
        val maxListC = mutableListOf(1,2,5,7,10,15,20,25,30)//継続
        val maxListR = mutableListOf(1,2,3,4,5,6,7,8,9)//復活

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser

        if(user !=null ){
            val docRef =db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (score != null) {
                    val t = score.totalD//総
                    val c = score.continuous//継続
                    val r = score.recover//復活

                    //titleリストの作成
                    for(i in 0..maxList.size-1){
                        titleList.add("総日数 ${maxList[i]}日           ${calculate(t,maxList[i])}")
                        progressList.add(t)
                        if(maxList[i]<=t){ hideButton.add(false) }
                        else{ hideButton.add(true)}
                    }
                    for(i in 0..maxListC.size-1) {
                        titleList.add("継続 ${maxListC[i]}日　           ${calculate(c,maxListC[i])}")
                        progressList.add(c)
                        if(maxListC[i]<=c){ hideButton.add(false) }
                        else{ hideButton.add(true)}
                    }
                    for(i in 0..maxListR.size-1){
                        titleList.add("復活 ${maxListR[i]}回　           ${calculate(r,maxListR[i])}")
                        progressList.add(r)
                        if(maxListR[i]<=r){ hideButton.add(false) }
                        else{ hideButton.add(true)}
                    }

                    //それぞれのパラメータを1つのmaxListにまとめる
                    maxListC.forEach{ maxList.add(it) }
                    maxListR.forEach{ maxList.add(it) }


                    adapter = AchievementAdapter(titleList,maxList,progressList,hideButton)
                    val layoutManager = LinearLayoutManager(requireContext())
                    // アダプターとレイアウトマネージャーをセット
                    recyclerView.layoutManager = layoutManager//simpleRecyclerView
                    recyclerView.adapter = adapter
                    recyclerView.setHasFixedSize(true)

// インターフェースの実装
                    /*
                    adapter.setOnItemClickListener(object: AchievementAdapter.OnItemClickListener {
                        override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                            when (view.getId()) {
                                R.id.get_button -> {
                                    if(maxList[position]<=progressList[position]){
                                        SettingDialog().showDialogForMedal(this@AchievementActivity)
                                    }
                                    Log.e("TAG","実績を獲得しました")
                                }
                            }
                        }
                    })

                     */
                }
            }
        }

    }
    fun calculate(score:Int,max:Int):String{
        val a = score.toDouble()/max.toDouble()*100
        var result = a.toInt()
        if(result>100){
            result=100
        }
        return "$result%"
    }
}