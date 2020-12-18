package jp.tsumura.takuya.self_maintenance


import android.content.Context
import jp.tsumura.takuya.self_maintenance.ForSetting.GoalSettingActivity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraXActivity
import jp.tsumura.takuya.self_maintenance.ForCamera.Sounds
import jp.tsumura.takuya.self_maintenance.ForCharacter.CharacterListFragment
import jp.tsumura.takuya.self_maintenance.ForMedals.MedalsTabFragment
import jp.tsumura.takuya.self_maintenance.ForSetting.AccountSettingActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.LoginActivity
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialActivity
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListFragment
import jp.tsumura.takuya.self_maintenance.forGallery.VideoListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_chara_intro.view.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val user = FirebaseAuth.getInstance().currentUser

        val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        val checkMini = prefs.getInt(getString(R.string.prefs_check_mini),0)
        when (item.itemId) {
            R.id.navi_tech -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, FirstFragment())
                    .commit()
                fab.show()
                supportActionBar!!.hide()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_myvideo -> {
                if(checkMini >=2 ){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, VideoListFragment())
                        .commit()

                    fab.show()
                    supportActionBar!!.show()
                    title="あなたの動画リスト"

                }else{
                    Toast.makeText(this,"まだ使えません...",Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_friend -> {
                if(checkMini >=3 ){
                    val a = mRealm().UidToName(user!!.uid)
                    if(a.isEmpty()){
                        val intent= Intent(this, AccountSettingActivity::class.java)
                        startActivity(intent)
                    }else{
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, FriendListFragment())
                            .commit()

                        fab.show()
                        supportActionBar!!.show()
                        title = "フレンドリスト"

                        Handler().postDelayed({
                            val Coach = TutorialCoachMarkActivity(this)
                            Coach.CoachMark4(this,this)
                        },1000)
                        //val intent= Intent(this, FriendListActivity::class.java)
                        //startActivity(intent)
                    }
                }else{
                    Toast.makeText(this,"まだ使えません...",Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_char -> {
                if(checkMini >=4 ){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, CharacterListFragment())
                        .commit()

                    fab.show()
                    supportActionBar!!.show()
                    title = "スケット"
                }else{
                    Toast.makeText(this,"まだ使えません...",Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navi_medal -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, MedalsTabFragment())
                    .commit()

                fab.hide()
                supportActionBar!!.show()
                title="実績"
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


//　　　　　　ボトムナビゲーション
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, FirstFragment())
            .commit()
        supportActionBar!!.hide()

        //  fabの表示について
        //val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        //val checkMini = prefs.getInt(getString(R.string.prefs_check_mini),0)
        //if(checkMini==0){fab.visibility= View.INVISIBLE}

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            //progressbar.visibility = android.widget.ProgressBar.VISIBLE
            val intent= Intent(this, CameraXActivity::class.java)
            startActivity(intent)
        }

        //初めて使う場合、ログイン画面にまず遷移する
        val user=FirebaseAuth.getInstance().currentUser
        Handler().postDelayed({
            if(user==null){//ログインしていなければ
                val intent= Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }else{
                val prefs = getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
                val Tuto1 : Boolean = prefs.getBoolean("Tuto1",false)
                if(!Tuto1){
                    val Coach = TutorialCoachMarkActivity(this)
                    Coach.CoachMark1(this,this)
                }else{
                    val Coach = TutorialCoachMarkActivity(this)
                    Coach.CoachMark2(this,this)
                }

            }

        }, 1000)


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
            R.id.action_settings -> startActivity(intent3)
            R.id.action_tutoreal -> {
                val Coach = TutorialCoachMarkActivity(this)
                Coach.reset()
                recreate()
                //TutorialActivity.showForcibly(this)//チューとリアル
            }

            R.id.action_logout->{FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,"ログアウトしました",Toast.LENGTH_LONG).show()
                startActivity(intent2)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}