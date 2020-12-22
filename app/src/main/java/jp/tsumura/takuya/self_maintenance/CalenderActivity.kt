package jp.tsumura.takuya.self_maintenance
/*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.util.*


class CalenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        val lst = arrayListOf<Int>(0,1,0,3,4)

        //アイコンを使ってその日にイベントをおく
        val events = ArrayList<EventDay>()

        for(i in 0..lst.size-1){
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            val text = CalendarUtils.getDrawableText(this,lst[i].toString(), null, R.color.colorPrimary, 12)
            events.add(EventDay(calendar, text))
        }
        //今日の日付に　M　〇マークを付ける

        calendarView.setEvents(events)



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


 */