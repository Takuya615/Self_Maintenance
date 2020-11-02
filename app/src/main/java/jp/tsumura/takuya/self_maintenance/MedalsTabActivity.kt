package jp.tsumura.takuya.self_maintenance

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_medals_tab.*

class MedalsTabActivity : AppCompatActivity() {

    class MedalsTabAdapter (fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    return SecondFragment()
                }
                else -> {
                    return AchievementFragment()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> {
                    return "成績"
                }
                else -> {
                    return "実績"
                }
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medals_tab)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "成績"

        // TabLayoutの取得
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        pager.adapter = MedalsTabAdapter(supportFragmentManager,this)
        tab_layout.setupWithViewPager(pager)



        // OnTabSelectedListenerの実装
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            // タブが選択された際に呼ばれる
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(this@MedalsTabActivity, tab.text, Toast.LENGTH_SHORT).show()
            }

            // タブが未選択になった際に呼ばれる
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            // 同じタブが選択された際に呼ばれる
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }


    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

