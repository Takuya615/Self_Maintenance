package jp.tsumura.takuya.self_maintenance.ForSetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForStart.TutorialCoachMarkActivity
import jp.tsumura.takuya.self_maintenance.MainActivity
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    //private lateinit var mDataBaseReference: DatabaseReference
    private lateinit var db:FirebaseFirestore

    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存する
    private var mIsCreateAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Handler().postDelayed({
            val Coach = TutorialCoachMarkActivity(this)
            Coach.CoachMark0(this,this)
        }, 1000)

        //mDataBaseReference = FirebaseDatabase.getInstance().reference

        db = FirebaseFirestore.getInstance()
        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()


        //メールとアカウント名を記録する
        val user = mAuth.currentUser
        if(user!=null){
            val setName :String? = Realm().UidToName(user.uid)//同じUidのアカウント名を返す
            nameText.text = Editable.Factory.getInstance().newEditable(setName)
            Log.e("TAG", "ログインしています")
        }else{
            Log.e("TAG", "まだログインしていません")
        }


        // アカウント作成処理のリスナー
        mCreateAccountListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                // ログインを行う
                val email = emailText.text.toString()
                val password = passwordText.text.toString()
                login(email, password)
            } else {
                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "アカウント作成に失敗しました", Snackbar.LENGTH_LONG).show()
                // プログレスバーを非表示にする
                progressBar.visibility = View.GONE
            }
        }

        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                if (mIsCreateAccount) {
                    Toast.makeText(this,"アカウントが作成されました",Toast.LENGTH_LONG).show()

                    Handler().postDelayed({
                        val name = nameText.text.toString()// アカウント作成の時はアカウント名とUidをRealmに保存する
                        val AcUid = user!!.uid
                        Realm().addPerson(name,AcUid)
                    }, 1000)

                } else {
                    Toast.makeText(this,"ログインしました",Toast.LENGTH_LONG).show()
                }

                progressBar.visibility = View.GONE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                // 失敗した場合
                Toast.makeText(this,"ログイン失敗\nメールアドレスかパスワードに間違いがないか確認してください",Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }

        // UIの準備
        title = "ログイン"

        createButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val name = nameText.text.toString()

            if (email.length != 0 && password.length >= 6&& name.length != 0) {
                if(Realm().SearchSameName(name)){
                    // ログイン時に表示名とUidを保存するようにフラグを立てる
                    mIsCreateAccount = true
                    createAccount(email, password)
                }else{
                    Snackbar.make(v, "このアカウント名はすでに使われています", Snackbar.LENGTH_LONG).show()
                }

            } else {
                // エラーを表示する
                Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show()
            }
        }

        loginButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val name=nameText.text.toString()

            if (email.length != 0 && password.length >= 6 ) {
                // フラグを落としておく
                mIsCreateAccount = false

                login(email, password)
            } else {
                // エラーを表示する
                Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show()
            }
        }
        logoutButton.setOnClickListener{
            if(mAuth.currentUser ==null){
                Toast.makeText(this,"すでにログアウトされています",Toast.LENGTH_LONG).show()
            }else{
                mAuth.signOut()
                Toast.makeText(this,"ログアウトしました",Toast.LENGTH_LONG).show()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        // プログレスバーを表示する
        progressBar.visibility = View.VISIBLE

        // アカウントを作成する
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
    }

    private fun login(email: String, password: String) {
        // プログレスバーを表示する
        progressBar.visibility = View.VISIBLE

        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
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