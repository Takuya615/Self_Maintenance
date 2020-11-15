package jp.tsumura.takuya.self_maintenance.ForCharacter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.recycler_view.*


class CharacterListFragment : Fragment() {

    private lateinit var adapter: CharacterListAdapter
    private lateinit var charImageList: MutableList<Int>
    private lateinit var charNameList: MutableList<String>
    private lateinit var charScriptList: MutableList<String>
    private lateinit var charUsedList: MutableList<Boolean>


    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


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

        charImageList= mutableListOf(
            R.drawable.tree_seichou02, R.drawable.tree_seichou04, R.drawable.tree_seichou06
        )
        charNameList = mutableListOf(
            "ワンワン","地縛霊の花子さん","Mｒ.キュー"
        )
        charScriptList = mutableListOf(
            "あらかじめ設定した時間通りに習慣を始めると\n経験値ボーナス＋１．２倍",
            "習慣をする場所を決めると\n経験値＋１０００",
            "よい習慣を思い出すキッカケをつくる\n経験値＋１０００"

        )
        val prefs = requireContext().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val charUsed = mutableListOf(prefs.getString("wanwan",""),prefs.getString("hanako",""),prefs.getString("mrq",""))


        if(user !=null ) {

            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (documentSnapshot.data != null && score != null) {
                    val continuous = score.continuous
                    val recover = score.recover
                    val totalD = score.totalD
                    val totalT = score.totalT
                    var DoNot = score.DoNot
                    val totalPoint = score.totalPoint
                    val level =CameraDialogFragment(1).calculate(totalPoint,450,-450,100)

                    if(level>9){
                        charImageList.add(R.drawable.tree_seichou07)
                        charNameList.add("Ms.キュー")
                        charScriptList.add("わるい習慣を思い出すキッカケをつくる\n経験値＋１０００")
                        charUsed.add(prefs.getString("msq",""))

                    }else{
                        charImageList.add(R.drawable.tree_seichou01)
                        charNameList.add("？？？")
                        charScriptList.add("レベル１０以上で開放")
                        charUsed.add("")
                    }
                    if(level>19){
                        charImageList.add(R.drawable.tree_seichou07)
                        charNameList.add("Ms.キュー")
                        charScriptList.add("わるい習慣を思い出すキッカケをつくる\n経験値＋１０００")
                        charUsed.add(prefs.getString("msq",""))

                    }else{
                        charImageList.add(R.drawable.tree_seichou01)
                        charNameList.add("？？？")
                        charScriptList.add("レベル２０以上で開放")
                        charUsed.add("")
                    }
                    if(level>29){
                        charImageList.add(R.drawable.tree_seichou07)
                        charNameList.add("Ms.キュー")
                        charScriptList.add("わるい習慣を思い出すキッカケをつくる\n経験値＋１０００")
                        charUsed.add(prefs.getString("msq",""))

                    }else{
                        charImageList.add(R.drawable.tree_seichou01)
                        charNameList.add("？？？")
                        charScriptList.add("レベル３０以上で開放")
                        charUsed.add("")
                    }
                    adapter.notifyDataSetChanged()

                }
            }


        }

        charUsedList = mutableListOf()
        for(i in charUsed){
            if(i == null||i.isEmpty()){
                charUsedList.add(false)
            }else{
                charUsedList.add(true)
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
                        CharaIntroDialogFragment(position).show(requireActivity().supportFragmentManager,"cyaracter")
                    }
                }
            }
        })


    }



}

