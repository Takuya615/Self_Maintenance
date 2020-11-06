package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_login.*

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "アカウント設定"

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if(user!=null) {
            val setName: String? = Realm().UidToName(user.uid)//同じUidのアカウント名を返す
            accountName.text = Editable.Factory.getInstance().newEditable(setName)

        }

        set_button.setOnClickListener{
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val Name = accountName.text.toString()
            val uid = user!!.uid.toString()

            if(Realm().SearchSameName(Name)){
                Realm().addPerson(Name,uid)
                Snackbar.make(it, "このアカウント名を登録しました", Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(it, "このアカウント名はすでに使われています。ちがう名前を設定してください", Snackbar.LENGTH_LONG).show()
            }

        }
    }

    //戻るボタンを押すと今いるviewを削除する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}