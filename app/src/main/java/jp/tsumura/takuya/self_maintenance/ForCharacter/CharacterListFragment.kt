package jp.tsumura.takuya.self_maintenance.ForCharacter

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment.Companion.calculate
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.Characters.Companion.setChara
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_character_list_item.*
import kotlinx.android.synthetic.main.recycler_view.*


class CharacterListFragment : Fragment() {

    private lateinit var adapter: CharacterListAdapter
    private lateinit var charImageList: MutableList<Int>
    private lateinit var charNameList: MutableList<String>
    private lateinit var charScriptList: MutableList<String>
    private lateinit var charUsedList: MutableList<Boolean>
    private lateinit var onPreferences:MutableList<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var level:Int=0


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
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser

        charNameList= mutableListOf()
        charImageList= mutableListOf()
        charScriptList= mutableListOf()
        charUsedList= mutableListOf()
        onPreferences= mutableListOf()

        val prefs = requireContext().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)

        val docRef = db.collection("Scores").document(user!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val score = documentSnapshot.toObject(Score::class.java)
            if (documentSnapshot.data != null && score != null) {
                val totalPoint = score.totalPoint
                level =calculate(totalPoint,450,-450,100)//CameraDialogFragmentのメソッド

                for(i in 0..3){//4タイのキャラを表示。レベルに応じて非表示
                    val chara = setChara(i)
                    if(level>=chara.level){

                        charNameList.add(chara.name)
                        charImageList.add(chara.icon)
                        charScriptList.add(chara.script)
                        onPreferences.add(prefs.getString(chara.prefer,"")!!)
                    }else{
                        val question=setChara(-1)
                        charNameList.add(question.name)
                        charImageList.add(question.icon)
                        charScriptList.add("Lv.${chara.level}で開放")
                        onPreferences.add(prefs.getString(question.prefer,"")!!)

                    }

                }

                for(i in onPreferences){
                    if(i == null||i.isEmpty()){
                        charUsedList.add(false)
                    }else{
                        charUsedList.add(true)
                    }
                }

                adapter.notifyDataSetChanged()
            }

        }

        adapter = CharacterListAdapter(charImageList,charNameList,charScriptList,charUsedList)
        val layoutManager = LinearLayoutManager(requireContext())
        // アダプターとレイアウトマネージャーをセット
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)


// インターフェースの実装
        adapter.setOnItemClickListener(object: CharacterListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                when (view.getId()) {
                    R.id.inviteButton -> {
                        CharaIntroDialogFragment(position).show(requireActivity().supportFragmentManager,"character")
                    }
                    R.id.itemSet -> {
                        //fragmentManager?.run{
                        //    CharaDetealDialogFragment(position,level).show(this,"aaa")
                        //}
                        CharaDetealDialogFragment(position,level).show(requireActivity().supportFragmentManager,"charaDeteal")
                    }
                }
            }
        })
        val Coach = TutorialCoachMarkActivity(requireContext())
        Coach.CoachMark6(requireActivity(),requireContext())

    }



}

