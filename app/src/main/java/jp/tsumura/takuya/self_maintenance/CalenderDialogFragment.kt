package jp.tsumura.takuya.self_maintenance

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import org.json.JSONArray
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class CalenderDialogFragment(TimerSec:Int): DialogFragment() {
    lateinit var customView: View
    var time = TimerSec

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_calender, null)

        val builder = AlertDialog.Builder(activity)
        //alertDialogBuilder.setTitle("活動の記録")
        //builder.setItems(list){ dialog, which -> Log.e("TAG", "${list[which]} が選択されました") }
        builder.setPositiveButton("メイン画面") { dialog, which ->
            requireActivity().finish()
        }
        builder.setView(customView)

        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView//inflater.inflate(R.layout.dialog_camera, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val calendarView = view?.findViewById<CalendarView>(R.id.calendarView)
        val key = "json5"
        val todaytime = arrayListOf<String>()
        val jsonList:ArrayList<String> = loadArrayList(key)
        val numbers = arrayListOf<String>()

        val prefs =
            requireContext().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val save: SharedPreferences.Editor = prefs.edit()

        val setday: String? = prefs.getString("setDate", "2020-12-17")//前回利用した日
        val now = LocalDate.now() //2019-07-28T15:31:59.754
        val day1 = LocalDate.parse(setday)//2019-08-28T10:15:30.123
        val different = ChronoUnit.DAYS.between(day1, now).toInt() // diff: 30


        if (different == 1) {
            todaytime.add(time.toString())//今日の分の時間
            numbers.addAll(todaytime)
            numbers.addAll(jsonList)
            Log.e("tag", "different is 1")

        } else if (different >= 2) {//日数の差が開いた数だけブランクを入れる

            numbers.add(time.toString())//今日の分の時間
            for (i in 2..different) {
                numbers.add("")
            }
            jsonList.forEach{
                numbers.add(it)
            }
            Log.e("tag", "different is over 2")

        } else if (different == 0) {
            numbers.addAll(jsonList)
            Log.e("tag", "different is 0")
        }


        Log.e("tag", "生成された配列は$numbers ")
        saveArrayList(key, numbers )//jsonに新たに保存
        getMarkDays(calendarView, numbers )//カレンダーに反映


        //カレンダーの日付のクリックイベント設定
        calendarView?.setOnDayClickListener(OnDayClickListener { eventDay ->
            val nowCalendar = eventDay.calendar
            Toast.makeText(
                requireContext(),
                nowCalendar.get(Calendar.YEAR).toString() + "-" +
                        (nowCalendar.get(Calendar.MONTH) + 1).toString() + "-" +
                        nowCalendar.get(Calendar.DATE).toString(), Toast.LENGTH_SHORT
            ).show()
        })


    }



    private fun getMarkDays(calendarView: CalendarView?, list: List<Any>) {
        val events = ArrayList<EventDay>()

        Log.e("tag", "カレンダーに反映するリストは$list ")

        for (i in 0..list.size - 1) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            val text = CalendarUtils.getDrawableText(requireContext(), list[i].toString(), null, android.R.color.holo_green_dark, 20)
            events.add(EventDay(calendar, text))
        }
        calendarView?.setEvents(events)
    }


    // リストの保存
    fun saveArrayList(key: String, arrayList:List<Any> ) {//ArrayList<String>

        val prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val shardPrefEditor = prefs.edit()

        val jsonArray = JSONArray(arrayList)
        shardPrefEditor.putString(key, jsonArray.toString())
        shardPrefEditor.apply()
    }

    // リストの読み込み     初日の時間も記録したうえでlistで返す
    fun loadArrayList(key: String): ArrayList<String> {

        val shardPreferences = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)//this.getPreferences(Context.MODE_PRIVATE)

        val jsonArray = JSONArray(shardPreferences.getString(key, "[]"));

        /*
        for (i in 0 until jsonArray.length()) {
            Log.i("loadArrayList", "[$i] -> " + jsonArray.get(i))
          }
         */

        val list = ArrayList<String>()
        //val jsonArray = jsonObject as JSONArray?
        val len = jsonArray.length()
        for (i in 0 until len) {
            list.add(jsonArray[i].toString())
        }


        return list
    }

}