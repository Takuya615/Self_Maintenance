package jp.tsumura.takuya.self_maintenance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.util.*

class CalenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        //カレンダーの日付のクリックイベント設定
        calendarView.setOnDayClickListener(OnDayClickListener { eventDay ->
            val nowCalendar = eventDay.calendar
            Toast.makeText(applicationContext,
                nowCalendar.get(Calendar.YEAR).toString() + "-" +
                        (nowCalendar.get(Calendar.MONTH)+1).toString() + "-" +
                        nowCalendar.get(Calendar.DATE).toString(), Toast.LENGTH_SHORT).show()
        })

    }
}