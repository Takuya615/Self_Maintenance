package jp.tsumura.takuya.self_maintenance.ForSetting

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_friend_search.*

class FriendSearchActivity : AppCompatActivity() {

    private lateinit var uid :String
    private lateinit var name :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)
        request.visibility = View.INVISIBLE
        //uid =""
        request.setOnClickListener{


            val list = mutableListOf<String>(name,uid)
            //（　＾ω＾）・・・





            Toast.makeText(this,"リクエストを送りました", Toast.LENGTH_LONG).show()
            finish()
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