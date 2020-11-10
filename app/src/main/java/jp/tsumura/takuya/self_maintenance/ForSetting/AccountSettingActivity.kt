package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
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

        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark6(this,this)
        }, 1000)


        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if(user!=null) {
            val setName: String? = mRealm().UidToName(user.uid)//同じUidのアカウント名を返す
            accountName.text = Editable.Factory.getInstance().newEditable(setName)
        }else{
            accountName.text = Editable.Factory.getInstance().newEditable("ログインしてください")
            set_button.visibility = View.INVISIBLE
        }

        set_button.setOnClickListener{
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val Name = accountName.text.toString()
            val uid = user!!.uid.toString()
            if(mRealm().SearchSameName(Name)){
                val a = mRealm().UidToName(user.uid)//
                if(a.isEmpty()){
                    mRealm().addPerson(Name,uid)
                    Toast.makeText(this,"アカウント名を登録しました",Toast.LENGTH_SHORT).show()
                    finish()
                    //val intent= Intent(this, FriendListActivity::class.java)
                    //startActivity(intent)
                }else{
                    mRealm().update(uid,Name)
                    Snackbar.make(it, "アカウント名を変更しました", Snackbar.LENGTH_LONG).show()
                }

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