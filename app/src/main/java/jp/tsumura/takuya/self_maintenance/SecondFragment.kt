package jp.tsumura.takuya.self_maintenance

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_second.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private lateinit var prefs : SharedPreferences


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)

        val ncd = prefs.getInt("preferences_key3",0)//連続日数
        val MAX : Int = prefs.getInt("preferences_key_MAX",0)//最長連続日数
        val revival = prefs.getInt("preferences_key_rev",0)//復活回数
        val totalday =prefs.getInt("preferences_key2",0)//総日数
        val TT = prefs.getInt("preferences_key",0)//総時間

        val ncdtext =view.findViewById<TextView>(R.id.textView2_1)
        val MAXtext =view.findViewById<TextView>(R.id.textView2_2)
        val revtext = view.findViewById<TextView>(R.id.textView2_3)
        val tDtext =view.findViewById<TextView>(R.id.textView2_4)
        val tTtext =view.findViewById<TextView>(R.id.textView2_5)

        
        ncdtext.text = "$ncd 日"
        MAXtext.text = "$MAX 日"
        var editrevaival = revival - 1//復活数の調整
        if(editrevaival == -1){ editrevaival = 0 }
        revtext.text = "$editrevaival 回"
        tDtext.text = "$totalday 日"
        val seconds =TT%60;
        val minite =(TT/60)%60;
        val totaltime="$minite"+"分"+"$seconds"+"秒"
        tTtext.text = totaltime


    }
}