package jp.tsumura.takuya.self_maintenance


import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.TutorialActivity.Companion.showIfNeeded
import jp.tsumura.takuya.self_maintenance.forGallery.URIlistFragment

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

        showIfNeeded(this,savedInstanceState)

        pager = findViewById(R.id.pager1)
        val adapter =FragmentsPagerAdapter(this)
        pager.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            val intent= Intent(this, CameraXActivity::class.java)
            startActivity(intent)
        }
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