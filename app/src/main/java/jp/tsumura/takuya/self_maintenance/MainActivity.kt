package jp.tsumura.takuya.self_maintenance


import android.content.Context
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.FriendSearchActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.LoginActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var pager : ViewPager2


    inner class FragmentsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return FirstFragment();
                1 -> return SecondFragment()
                //2 -> return URIlistFragment()
                else -> return FirstFragment()

            }
        }
        //override fun getItemPosition(obj: Any)
        //        : Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Realm.init(this)

        progressbar.visibility = android.widget.ProgressBar.INVISIBLE
        //showIfNeeded(this, savedInstanceState)//全画面のチュートリアル(ウォークスルー)

        pager = findViewById(R.id.pager1)
        val adapter =FragmentsPagerAdapter(this)
        pager.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            //progressbar.visibility = android.widget.ProgressBar.VISIBLE
            val intent= Intent(this, CameraXActivity::class.java)
            startActivity(intent)
        }

        //初めて使う場合、ログイン画面にまず遷移する
        val prefs = getSharedPreferences( "preferences_key_sample", Context.MODE_PRIVATE)
        val Tuto0 : Boolean = prefs.getBoolean("Tuto0",false)
        if(!Tuto0){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Handler().postDelayed({
                val Coach = TutorialCoachMarkActivity(this)
                Coach.CoachMark1(this,this)
            }, 1000)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent= Intent(this, GoalSettingActivity::class.java)
        val intent2= Intent(this, LoginActivity::class.java)

        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(applicationContext, "ただいま工事中💦", Toast.LENGTH_LONG).show()
            R.id.action_settings2 -> startActivity(intent)
            R.id.action_login->startActivity(intent2)
            //R.id.action_search -> { startActivity(Intent(this, FriendSearchActivity::class.java)) }
            else ->Log.e("TAG","設定画面でなにかを押しました")
        }
        return super.onOptionsItemSelected(item)
    }
}