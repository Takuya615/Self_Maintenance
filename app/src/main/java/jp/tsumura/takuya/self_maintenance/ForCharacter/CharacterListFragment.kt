package jp.tsumura.takuya.self_maintenance.ForCharacter

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
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_achievement.*

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
        val view = inflater.inflate(R.layout.fragment_achievement, container, false)
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
            "ワンワン","testtest","テストテスト"
        )
        charScriptList = mutableListOf(
            "決まった時間になると現れて、あなたの習慣を応援してくれます。\n経験値ボーナス１．２倍",
            "決まった時間になると現れて、あなたの習慣を応援してくれます。\n経験値ボーナス１．２倍",
            "決まった時間になると現れて、あなたの習慣を応援してくれます。\n経験値ボーナス１．２倍"
        )
        charUsedList = mutableListOf()
        val prefs = requireContext().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val charUsed = arrayOf(prefs.getString("wanwan",""),prefs.getString("AAA",""),prefs.getString("wanwan",""))
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
        achieveRecyclerView.layoutManager = layoutManager
        achieveRecyclerView.adapter = adapter
        achieveRecyclerView.setHasFixedSize(true)

        if(user !=null ){}


// インターフェースの実装
        adapter.setOnItemClickListener(object: CharacterListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                when (view.getId()) {
                    R.id.inviteButton -> {
                        when(position){
                            0->SettingDialog().showTimePickerDialog(requireContext())
                            1->SettingDialog().showDialog(requireContext())
                            else->SettingDialog().showDialog(requireContext())
                        }

                    }
                }
            }
        })


    }



}

