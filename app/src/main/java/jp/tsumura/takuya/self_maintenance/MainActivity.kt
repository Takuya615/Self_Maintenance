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
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharacterListFragment
import jp.tsumura.takuya.self_maintenance.ForSetting.AccountSettingActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.LoginActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListFragment
import jp.tsumura.takuya.self_maintenance.forGallery.VideoListFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val user = FirebaseAuth.getInstance().currentUser

        when (item.itemId) {
            R.id.navi_tech -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, FirstFragment())
                    .commit()
                title = "ホーム"
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_myvideo -> {
                if(user !=null ){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, VideoListFragment())
                        .commit()
                    title="あなたの動画リスト"
                }else{
                    Toast.makeText(this,"ログインしてください",Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_friend -> {
                if(user !=null ){
                    val a = mRealm().UidToName(user.uid)
                    if(a.isEmpty()){
                        val intent= Intent(this, AccountSettingActivity::class.java)
                        startActivity(intent)
                    }else{
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, FriendListFragment())
                            .commit()
                        title = "フレンドリスト"
                        //val intent= Intent(this, FriendListActivity::class.java)
                        //startActivity(intent)
                    }

                }else{
                    Toast.makeText(this,"ログインしてください",Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_medal -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, MedalsTabFragment())
                    .commit()
                title="実績"
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_char -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, CharacterListFragment())
                    .commit()
                title = "スケット"

                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Realm.init(this)

        //ボトムナビゲーション
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FirstFragment())
            .commit()


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            //progressbar.visibility = android.widget.ProgressBar.VISIBLE
            val intent= Intent(this, CameraXActivity::class.java)
            startActivity(intent)
        }

        val prefs = getSharedPreferences( "preferences_key_sample", Context.MODE_PRIVATE)


        //初めて使う場合、ログイン画面にまず遷移する

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
            //else ->Log.e("TAG","設定画面でなにかを押しました")
        }
        return super.onOptionsItemSelected(item)
    }
}