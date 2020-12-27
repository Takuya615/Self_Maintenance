package jp.tsumura.takuya.self_maintenance.ForSetting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*


class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title="プライバシーポリシー"

    }

/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

 */

    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}