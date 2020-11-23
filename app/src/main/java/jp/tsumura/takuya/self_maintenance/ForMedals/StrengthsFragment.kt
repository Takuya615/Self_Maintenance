package jp.tsumura.takuya.self_maintenance.ForMedals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.ViaStrItem
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_strengths.view.*
import org.json.JSONArray

class StrengthsFragment: Fragment() {

    private var viaList :MutableList<Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_strengths, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.bar_chart
        //表示データ取得
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val saveScore = db.collection("Scores").document(user.uid)
            saveScore.get().addOnSuccessListener { document ->
                val score = document.toObject(Score::class.java)
                if (document.data != null && score != null) {
                    viaList = score.vialist

                    chart.data = BarData(getBarData())

                    //Y軸(左)の設定
                    chart.axisLeft.apply {
                        axisMinimum = 0f
                        axisMaximum = 10f
                        labelCount = 5
                        setDrawTopYLabelEntry(true)
                        setValueFormatter { value, axis -> "" + value.toInt()}
                    }

                    //Y軸(右)の設定
                    chart.axisRight.apply {
                        setDrawLabels(false)
                        setDrawGridLines(false)
                        setDrawZeroLine(false)
                        setDrawTopYLabelEntry(true)
                    }

                    //X軸の設定
                    val labels = ViaStrItem//最初の””は原点の値arrayOf("","創造性","勇敢さ")
                    chart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        labelCount = 24 //表示させるラベル数
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawLabels(true)
                        setDrawGridLines(false)
                        setDrawAxisLine(true)
                    }

                    //グラフ上の表示
                    chart.apply {
                        setDrawValueAboveBar(true)
                        description.isEnabled = false
                        isClickable = false
                        legend.isEnabled = false //凡例
                        setScaleEnabled(false)
                        animateY(1200, Easing.EasingOption.Linear)
                    }
                }
            }

        }
    }

    private fun getBarData(): ArrayList<IBarDataSet> {

        val entries = ArrayList<BarEntry>().apply {
            for(i in 0..23){
                add(BarEntry(i.toFloat(), viaList!![i].toFloat()))
            }

        }

        val dataSet = BarDataSet(entries, "bar").apply {
            //整数で表示
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + value.toInt() }
            //ハイライトさせない
            isHighlightEnabled = false
            //Barの色をセット
            setColors(intArrayOf(R.color.material_blue, R.color.material_green, R.color.material_yellow),requireContext())
        }

        val bars = ArrayList<IBarDataSet>()
        bars.add(dataSet)
        return bars
    }
/*
    fun showViaList(){
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val saveScore = db.collection("Scores").document(user.uid)
            saveScore.get().addOnSuccessListener { document ->
                viaList = document["vialist"] as MutableList<Int>
                if (viaList == null || viaList.isEmpty()) {
                    viaList = mutableListOf(1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1)
                }
            }

        }else{
            viaList = mutableListOf(0,0,0,0,0 ,0,0,0,0,0 ,0,0,0,0,0 ,0,0,0,0,0 ,0,0,0,0)
        }
    }
 */
}