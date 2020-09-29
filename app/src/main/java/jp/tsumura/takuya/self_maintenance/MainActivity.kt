package jp.tsumura.takuya.self_maintenance


import android.content.Context
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.target.SimpleTarget
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.TutorialActivity.Companion.showForcibly
import jp.tsumura.takuya.self_maintenance.TutorialActivity.Companion.showIfNeeded
import jp.tsumura.takuya.self_maintenance.forGallery.URIlistFragment
import jp.tsumura.takuya.self_maintenance.forGallery.UriListActivity
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var pager : ViewPager2


    inner class FragmentsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return FirstFragment();
                1 -> return SecondFragment()
                2 -> return URIlistFragment()
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

        progressbar.visibility = android.widget.ProgressBar.INVISIBLE
        //showIfNeeded(this, savedInstanceState)//全画面のチュートリアル(ウォークスルー)

        pager = findViewById(R.id.pager1)
        val adapter =FragmentsPagerAdapter(this)
        pager.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            progressbar.visibility = android.widget.ProgressBar.VISIBLE
            val intent= Intent(this, CameraXActivity::class.java)
            startActivity(intent)
        }


        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark1(this,this)
        }, 1000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent= Intent(this, GoalSettingActivity::class.java)
        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(applicationContext, "ただいま工事中💦", Toast.LENGTH_LONG).show()
            R.id.action_settings2 ->startActivity(intent)
            else ->Log.e("TAG","設定画面でなにかを押しました")
        }
        return super.onOptionsItemSelected(item)
    }
}