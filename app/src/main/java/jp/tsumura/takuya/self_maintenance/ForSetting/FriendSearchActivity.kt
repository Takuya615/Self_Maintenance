package jp.tsumura.takuya.self_maintenance.ForSetting

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_friend_search.*
import kotlinx.android.synthetic.main.activity_goal_setting.*

class FriendSearchActivity : AppCompatActivity() {

    private lateinit var uid :String
    private lateinit var name :String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db :FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)
        request.visibility = View.INVISIBLE
        //uid =""val list = mutableListOf<String>(name,uid)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        request.setOnClickListener{
            val user = mAuth.currentUser
            if(user!=null){
                Log.e("TAG","コレクションのUidは$uid")
                val docRef = db.collection(uid).document("friendRequest")
                docRef.set(hashMapOf("friendUid" to user.uid))
                    .addOnSuccessListener {
                        Toast.makeText(this,"リクエストを送りました", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { Toast.makeText(this,"リクエスト送信に失敗しました", Toast.LENGTH_LONG).show() }//e -> Log.e("TAG", "ドキュメントの作成・上書きエラー", e)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(searchableInfo)

        searchView?.isIconified

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val ans = Realm().SearchName(query)
                if(ans.AcUid.isEmpty()){
                    answer.text=ans.Name
                    request.visibility = View.INVISIBLE
                }else{
                    answer.text="${ans.Name}さんが見つかりました\nフレンドリクエストを送りますか？"
                    name = ans.Name
                    uid = ans.AcUid
                    request.visibility = View.VISIBLE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }
}