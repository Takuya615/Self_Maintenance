package jp.tsumura.takuya.self_maintenance.ForMedals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_medals_tab.*

class MedalsTabFragment: Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_medals_tab, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TabLayoutの取得
        val tabLayout = requireActivity().findViewById<TabLayout>(R.id.tab_layout)
        pager.adapter = MedalsTabAdapter(requireActivity().supportFragmentManager, requireContext())
        tab_layout.setupWithViewPager(pager)



        // OnTabSelectedListenerの実装
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            // タブが選択された際に呼ばれる
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(requireContext(), tab.text, Toast.LENGTH_SHORT).show()
            }

            // タブが未選択になった際に呼ばれる
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            // 同じタブが選択された際に呼ばれる
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

}