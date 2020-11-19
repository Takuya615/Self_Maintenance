package jp.tsumura.takuya.self_maintenance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var prefs : SharedPreferences
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val taskSec: Int = prefs.getInt(getString(R.string.preferences_key_smalltime),0)

        /*
        val user = mAuth.currentUser
        val db = FirebaseFirestore.getInstance()
        //prefs.getInt("totalday",0)総日数
        if(user !=null ){
            val docRef = db.collection("Scores").document(user.uid)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val score = documentSnapshot.toObject(Score::class.java)
                    if (score != null) {
                        totalday = score.totalD.toString()
                        Log.e("TAG","totalday の値は${score.totalD}")

                    }else{
                        Log.e("TAG","Scoreがnullだった")

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "Totaldayの取得失敗", exception)

                }
        }

         */
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalday = prefs.getInt("totalday",0)

        val growthimage =view.findViewById<ImageView>(R.id.growth_image)

        if(1<=totalday && totalday<3){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou02))
        }
        if(3<=totalday && totalday<5){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou03))
        }
        if(5<=totalday && totalday<7){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou04))
        }
        if(7<=totalday && totalday<12){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou05))
        }
        if(12<=totalday && totalday<17){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou06))
        }
        if(17<=totalday && totalday<23){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou07))
        }
        if(23<=totalday && totalday<30){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou08))
        }
        if(30<=totalday){
            growthimage?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.tree_seichou09))
        }

        if(wanwan(prefs)){
            wanwanImage.visibility = View.VISIBLE
        }

    }

    fun wanwan(prefs : SharedPreferences):Boolean{
        val wanwan=prefs.getString("wanwan","")
        if(wanwan!=null&&wanwan.isNotEmpty()){
            val setday : String? ="2020-10-28"// prefs.getString("setDate", "2020-10-28")//前回利用した日
            val now = LocalDate.now() //2019-07-28T15:31:59.754
            val day1 = LocalDate.parse(setday)//2019-08-28T10:15:30.123
            val different = ChronoUnit.DAYS.between(day1, now).toInt() // diff: 30
            if(different!=0){
                val sdf = SimpleDateFormat("HH")
                val sdf2 = SimpleDateFormat("mm")
                val hour = sdf.format(System.currentTimeMillis())
                val min = sdf2.format(System.currentTimeMillis())

                Log.e("TAG","今の時間を表すと$hour:$min")
                Log.e("TAG","wanwanは$wanwan")
                val a = wanwan.toInt() - hour.toInt()*60 - min.toInt() + 15//設定した分時 －今の時（分表記）－今の分＋アソビ15分
                Log.e("TAG","与えられる式は${wanwan}-${hour.toInt()*60}-${min.toInt()}")
                Log.e("TAG","その差は$a")
                if(0<a&&30>a){//設定が１２時なら、１１：４５～１２：１５が、使える時間

                    return true
                }
            }
            return false
        }
        return false
    }
}