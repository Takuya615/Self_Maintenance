package jp.tsumura.takuya.self_maintenance.forGallery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForSetting.FriendSearchActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R

import kotlinx.android.synthetic.main.recycler_view.*

class FriendListFragment: Fragment() {

    private lateinit var adapter : FriendListAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var mnameList:MutableList<String>
    private lateinit var muidList:MutableList<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        //コーチマーク
        //val Coach = TutorialCoachMarkActivity(requireContext())
        //Coach.CoachMark5(requireActivity(),requireContext())

        muidList = mutableListOf<String>()
        mnameList = mutableListOf<String>()
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        //フレンドリクエストが来ている場合、ダイアログを表示するYesなら、相手のUidをドキュメントとしてFirebaseに追加する
        val user = mAuth.currentUser

        if(user!=null){
            val docRef = db.collection(user.uid).document("friendRequest")//whereEqualTo("request",true)
            docRef.get().addOnSuccessListener { document ->
                if(document.data != null){
                    val friendUid = document.data!!["friendUid"]
                    if(friendUid !=null){

                        val requestName = mRealm().UidToName(friendUid.toString())
                        //ダイアログ
                        val alertDialogBuilder = AlertDialog.Builder(requireContext())
                        alertDialogBuilder.apply {
                            setTitle("$requestName　さんからフレンドリクエストが届いてます")
                            setMessage("フレンドになると、その人の日々のミッションを応援してあげることができます\n\nリクエストを承認しますか？")
                            setPositiveButton("承認する"){dialog, which ->

                                val checkSameName = muidList.contains(friendUid.toString())//すでに同じユーザーが登録されている場合はダメ
                                if(checkSameName){
                                    docRef.set(hashMapOf("friendUid" to null ))
                                    Toast.makeText(requireContext(), "同じひとをリストへ加えることはできません", Toast.LENGTH_LONG).show()
                                }else{
                                    val docRef2 = db.collection(user.uid).document(requestName)
                                    val map = hashMapOf(
                                        "friend" to true,
                                        "name" to requestName,
                                        "uid" to friendUid//.toString()
                                    )
                                    docRef2.set(map)
                                        .addOnSuccessListener {

                                            mnameList.add(requestName)
                                            muidList.add(friendUid.toString())
                                            adapter.notifyDataSetChanged()
                                        }
                                    //保存されているデータをnullに戻さないと。。。
                                    docRef.set(hashMapOf("friendUid" to null ))
                                }

                            }
                            setNegativeButton("見なかったことにする"){dialog, which ->
                                //保存されているデータをnullに戻さないと。。。
                                docRef.set(hashMapOf("friendUid" to null ))
                            }
                        }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }

                }
            }//.addOnFailureListener { e -> Log.e("TAG", "データ取得に失敗", e) }


            db.collection(user.uid).whereEqualTo("friend", true).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val name = document.data["name"].toString()
                        val uid = document.data["uid"].toString()
                        mnameList.add(name)
                        muidList.add(uid)
                        adapter.notifyDataSetChanged()

                    }
                }
            //.addOnFailureListener { exception -> Log.w("TAG", "Error getting documents: ", exception) }

            adapter = FriendListAdapter(mnameList)
            layoutManager = LinearLayoutManager(requireContext())
            // アダプターとレイアウトマネージャーをセット
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            // インターフェースの実装
            adapter.setOnItemClickListener(object:FriendListAdapter.OnItemClickListener{
                override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                    when(view.getId()){
                        R.id.itemTextView -> {
                            val friendName = mnameList[position]
                            val friendUid = muidList[position]
                            val intent = Intent(requireContext(), VideoListActivity::class.java)
                            intent.putExtra("friendName",friendName)
                            intent.putExtra("friendUid",friendUid)
                            startActivity(intent)
                            //Toast.makeText(applicationContext, "${clickedText}がタップされました", Toast.LENGTH_LONG).show()
                        }
                        R.id.itemdeleate -> {
                            db.collection(user.uid).document(clickedText).delete()
                            //Toast.makeText(applicationContext, "${clickedText}を削除しました", Toast.LENGTH_LONG).show()
                            mnameList.remove(mnameList[position])
                            muidList.remove(muidList[position])
                            adapter.notifyItemRemoved(position)
                            adapter.notifyItemRangeChanged(position, mnameList.size)

                        }
                    }
                }
            })


        }

    }

    //メニューバーのレイアウトを設定する
    override fun onCreateOptionsMenu(menu: Menu,inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_friend_list, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(requireContext(), FriendSearchActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}