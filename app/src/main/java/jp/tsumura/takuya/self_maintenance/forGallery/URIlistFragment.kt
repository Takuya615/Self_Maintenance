package jp.tsumura.takuya.self_maintenance.forGallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import jp.tsumura.takuya.self_maintenance.R

class URIlistFragment : Fragment() {

    private lateinit var URIadapter : ArrayAdapter<String>
    private lateinit var mdateList:MutableList<String>
    private lateinit var muriList:MutableList<String>

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_urilist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        muriList = mutableListOf<String>()
        mdateList = mutableListOf<String>()
        val Reference = FirebaseDatabase.getInstance().reference
        val genreRef = Reference.child("URIlists")
        genreRef.addChildEventListener(mEventListener)


        val itemLayoutId = R.layout.fragment_urilist_item
        val textViewId = R.id.label
        URIadapter = ArrayAdapter(requireActivity(),itemLayoutId,textViewId,mdateList)
        val listview = view.findViewById<ListView>(R.id.ListView)
        listview.adapter = URIadapter
        URIadapter.notifyDataSetChanged()

        listview.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(requireActivity(), mdateList[position], Toast.LENGTH_SHORT).show()

            val value = muriList[position]
            val intent = Intent(requireActivity(), GalleryActivity::class.java)
            intent.putExtra("selectedName",value)
            startActivity(intent)

        }
    }

}