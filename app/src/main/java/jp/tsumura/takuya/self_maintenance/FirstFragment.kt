package jp.tsumura.takuya.self_maintenance

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val totalday =prefs.getInt("preferences_key2",0)//総日数
        val growthimage =view.findViewById<ImageView>(R.id.growth_image)

        Log.e("TAG","総日数は$totalday 日")
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


    }
}