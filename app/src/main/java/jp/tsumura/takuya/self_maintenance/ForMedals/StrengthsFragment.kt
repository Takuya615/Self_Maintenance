package jp.tsumura.takuya.self_maintenance.ForMedals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.ViaForChart
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_strengths.*
import kotlinx.android.synthetic.main.fragment_strengths.view.*
import org.json.JSONArray

class StrengthsFragment: Fragment() {



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

        //表示用サンプルデータの作成//
        val dimensions = listOf("A", "B", "C", "D")//分割円の名称(String型)
        val values = listOf(1f, 2f, 3f, 4f)//分割円の大きさ(Float型)

        //①Entryにデータ格納
        var entryList = mutableListOf<PieEntry>()
        for(i in values.indices){
            entryList.add(
                PieEntry(values[i], dimensions[i])
            )
        }

        //②PieDataSetにデータ格納
        val pieDataSet = PieDataSet(entryList, "candle")
        //③DataSetのフォーマット指定
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        //④PieDataにPieDataSet格納
        val pieData = PieData(pieDataSet)
        //⑤PieChartにPieData格納
        val pieChart = pieChartExample//requireActivity().findViewById(R.id.pieChartExample)

        pieChart.data = pieData
        //⑥Chartのフォーマット指定
        pieChart.legend.isEnabled = false
        //⑦PieChart更新
        pieChart.invalidate()

    }

    /*棒グラフの設定
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chart = view.bar_chart
        //表示データ取得
        chart.data = BarData(getBarData())

        //Y軸(左)の設定
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = 100f
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
        val labels = ViaForChart //最初の””は原点の値arrayOf("","創造性","勇敢さ")
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

    private fun getBarData(): ArrayList<IBarDataSet> {
        //表示させるデータ
        val prefs = requireContext().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val ViaList = JSONArray(prefs.getString("VIA","[100,1,1,1,1,1,1,1,1,1,1 ,1,1,1,1,1,1,1,1,1,1 ,1,1,1,1]"))
        //val StrengthsPoint = mutableListOf<Float>(20f,40f,60f,85f)
        val entries = ArrayList<BarEntry>().apply {
            for(i in 1..24){
                add(BarEntry(i.toFloat(), ViaList[i].toString().toInt().toFloat()))
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

     */

}