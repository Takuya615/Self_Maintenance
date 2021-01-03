package jp.tsumura.takuya.self_maintenance.ForMedals

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_second_list_item.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import kotlinx.android.synthetic.main.recycler_view.recyclerView
import kotlinx.android.synthetic.main.recycler_view2.*


class SecondFragment : Fragment() {


    class SecondFragmentAdapter(private val customList: MutableList<String>,
                                private val customList2: MutableList<String>) : RecyclerView.Adapter<SecondFragmentAdapter.CustomViewHolder>() {

        // ViewHolderクラス(別ファイルに書いてもOK)
        class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
            val sampleImg = view.content2
            val sampleTxt = view.record
        }

        // getItemCount onCreateViewHolder onBindViewHolderを実装
        // 上記のViewHolderクラスを使ってViewHolderを作成
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val item = layoutInflater.inflate(R.layout.fragment_second_list_item, parent, false)
            return CustomViewHolder(item)
        }

        // recyclerViewのコンテンツのサイズ
        override fun getItemCount(): Int {
            return customList.size
        }

        // ViewHolderに表示する画像とテキストを挿入
        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.view.content2.text = customList[position]
            holder.view.record.text = customList2[position]

        }
    }





    private lateinit var prefs : SharedPreferences
    private lateinit var adapter: SecondFragmentAdapter
    private lateinit var list1 :MutableList<String>
    private lateinit var list2 : MutableList<String>

    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_view2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        list1 = mutableListOf<String>()
        list2 = mutableListOf<String>()

        if(user!=null) {
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get().addOnSuccessListener { documentSnapshot ->
                val score = documentSnapshot.toObject(Score::class.java)
                if (documentSnapshot.data != null && score != null) {
                    val ncd = score.continuous//prefs.getInt("continue",0)連続日数
                    val revival = score.recover//復活回数
                    val totalday = score.totalD//総日数
                    val TT = score.totalT//総時間
                    val totalPoint = score.totalPoint
                    val MAX : Int = prefs.getInt(getString(R.string.preferences_key_MAX),0)//最長連続日数

                    list1.add("継続日数")
                    list1.add("これまでの最長継続日数")
                    list1.add("復活回数")
                    list1.add("総活動日数")
                    list1.add("総活動時間")
                    list1.add("総経験値")

                    list2.add("$ncd 日")
                    list2.add("$MAX 日")
                    list2.add("$revival 回")
                    list2.add("$totalday 日")
                    val seconds =TT%60;
                    val minite =(TT/60)%60;
                    val totaltime="$minite"+"分"+"$seconds"+"秒"
                    list2.add(totaltime)
                    list2.add("$totalPoint")

                    adapter.notifyDataSetChanged()
                    Log.d("tag","ここまで来てる")
                }
            }

            adapter = SecondFragmentAdapter(list1,list2)
            val layoutManager = LinearLayoutManager(requireContext())
            // アダプターとレイアウトマネージャーをセット
            recyclerView2.layoutManager = layoutManager
            recyclerView2.adapter = adapter
            recyclerView2.setHasFixedSize(true)
        }

    }
}