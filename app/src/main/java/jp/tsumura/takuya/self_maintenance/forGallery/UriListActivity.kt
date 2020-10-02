package jp.tsumura.takuya.self_maintenance.forGallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import jp.tsumura.takuya.self_maintenance.R

class UriListActivity : AppCompatActivity() {

    private lateinit var URIadapter : ArrayAdapter<String>
    private lateinit var mdateList:MutableList<String>
    private lateinit var muriList:MutableList<String>
    private lateinit var mAuth: FirebaseAuth

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val date = map["date"].toString()
            val uri = map["uri"].toString()
            //val only = onlydate(date).date

            mdateList.add(date)
            muriList.add(uri)
            URIadapter.notifyDataSetChanged()
            Log.e("TAG","リストの要素$map　を追加しました")
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TAG", "loadPost:onCancelled失敗", databaseError.toException())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uri_list)
        title = "動画リスト"

        mAuth = FirebaseAuth.getInstance()
        muriList = mutableListOf<String>()
        mdateList = mutableListOf<String>()
        val user = mAuth.currentUser
        if(user!=null){
            val Reference = FirebaseDatabase.getInstance().reference
            val genreRef = Reference.child(user.uid)
            genreRef.addChildEventListener(mEventListener)
        }



        val itemLayoutId = R.layout.fragment_urilist_item
        val textViewId = R.id.label
        URIadapter = ArrayAdapter(this,itemLayoutId,textViewId,mdateList)
        val listview = findViewById<ListView>(R.id.ListView1)
        listview.adapter = URIadapter
        URIadapter.notifyDataSetChanged()

        listview.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, mdateList[position], Toast.LENGTH_SHORT).show()

            val value = muriList[position]
            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra("selectedName",value)
            startActivity(intent)

        }
    }
}