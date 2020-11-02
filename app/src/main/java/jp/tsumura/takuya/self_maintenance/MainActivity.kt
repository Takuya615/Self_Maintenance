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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.AccountSettingActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.LoginActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListActivity
import jp.tsumura.takuya.self_maintenance.forGallery.VideoListActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var pager : ViewPager2

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navi_myvideo -> {
                val user = FirebaseAuth.getInstance().currentUser
                if(user !=null ){
                    val intent= Intent(this, VideoListActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"ログインしてください",Toast.LENGTH_SHORT).show()
                }
                //return@OnNavigationItemSelectedListener true
            }
            R.id.navi_friend -> {
                val user = FirebaseAuth.getInstance().currentUser
                if(user !=null ){
                    val intent= Intent(this, FriendListActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"ログインしてください",Toast.LENGTH_SHORT).show()
                }
                //return@OnNavigationItemSelectedListener true
            }
            R.id.navi_medal -> {
                val intent= Intent(this, MedalsTabActivity::class.java)
                startActivity(intent)
                //return@OnNavigationItemSelectedListener true
            }
            R.id.navi_tech -> {
                Toast.makeText(this,"ただいま工事中",Toast.LENGTH_SHORT).show()
                /*
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, FirstFragment())
                    .commit()

                 */
                //return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    inner class FragmentsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return FirstFragment();
                //1 -> return SecondFragment()
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

        //ボトムナビゲーション
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        /*
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FirstFragment())
            .commit()
         */


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
        val intent3= Intent(this, AccountSettingActivity::class.java)

        when (item.itemId) {
            R.id.action_settings2 -> startActivity(intent)
            R.id.action_login->startActivity(intent2)
            R.id.action_settings -> startActivity(intent3)
            R.id.action_tutoreal -> {
                val Coach = TutorialCoachMarkActivity(this)
                Coach.reset()
                recreate()
                TutorialActivity.showForcibly(this)//チューとリアル
            }
            //R.id.action_search -> { startActivity(Intent(this, FriendSearchActivity::class.java)) }
            else ->Log.e("TAG","設定画面でなにかを押しました")
        }
        return super.onOptionsItemSelected(item)
    }
}