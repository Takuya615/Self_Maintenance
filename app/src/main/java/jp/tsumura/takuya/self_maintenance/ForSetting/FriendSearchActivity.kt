package jp.tsumura.takuya.self_maintenance.ForSetting

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Telephony.Mms.Part.CONTENT_ID
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListFragment
import kotlinx.android.synthetic.main.activity_friend_search.*
import kotlinx.android.synthetic.main.activity_goal_setting.*

class FriendSearchActivity : AppCompatActivity() {

    private lateinit var uid :String
    private lateinit var name :String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db :FirebaseFirestore
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "フレンド検索"
        request.visibility = View.INVISIBLE//         request=ボタンのid

        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark5(this,this)
        },1000)


        //uid =""val list = mutableListOf<String>(name,uid)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        request.setOnClickListener{
            val user = mAuth.currentUser
            if(user!=null){
                val docRef = db.collection(uid).document("friendRequest")
                docRef.set(hashMapOf("friendUid" to user.uid))
                    .addOnSuccessListener {
                        Toast.makeText(this,"リクエストを送りました", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))


                        //友人にリクエストを送ったのか、イベントログを記録する。他の人へオススメしたという指標になる
                        val bundle:Bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "friend_request")
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "friend_request")
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "friend_request")
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


                    }
                    .addOnFailureListener { Toast.makeText(this,"リクエスト送信に失敗しました", Toast.LENGTH_LONG).show() }//e -> Log.e("TAG", "ドキュメントの作成・上書きエラー", e)
            }
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = findViewById<SearchView>(R.id.search1123)//menu.findItem(R.id.app_bar_search).actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(searchableInfo)

        searchView?.isIconified

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val ans = mRealm().SearchName(query)
                if(ans.AcUid.isEmpty()){
                    answer.text=ans.Name
                    request.visibility = View.INVISIBLE
                }else{
                    answer.text="${ans.Name}さんが\n見つかりました\nフレンドリクエストを送りますか？"
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


    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

 */
    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}