package jp.tsumura.takuya.self_maintenance.ForCharacter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.CameraDialogFragment.Companion.calculate

import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.Characters.Companion.setChara
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.activity_medals_tab.*
import kotlinx.android.synthetic.main.dialog_chara_intro.*
import kotlinx.android.synthetic.main.dialog_chara_intro.view.*
import kotlinx.android.synthetic.main.fragment_character_list_item.*
import kotlinx.android.synthetic.main.fragment_character_list_item.view.*

class CharaIntroDialogFragment(position:Int) : DialogFragment() {
    val posi = position
    private lateinit var customView :View
    private lateinit var prefs :SharedPreferences
    private lateinit var save: SharedPreferences.Editor
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_chara_intro, null)
        prefs = requireActivity().getSharedPreferences("preferences_key_sample", Context.MODE_PRIVATE)
        save = prefs.edit()
        val builder = charaSelect()//AlertDialog.Builder(requireActivity())
        return builder.create()
    }

    fun charaSelect():AlertDialog.Builder{
        val builder = AlertDialog.Builder(requireActivity())

        val chara = setChara(posi)
        builder.setTitle(chara.name)
        builder.setIcon(chara.icon)
        builder.setMessage(chara.script)
        if(chara.position==0){
            builder.setPositiveButton("時間をきめる"){ dialog, which ->
                wanwanDialog(requireContext())
            }
            builder.setNegativeButton("やめる"){ dialog, which ->
                save.putString(chara.prefer, "")
                save.apply()
            }
        }else{
            builder.setView(customView)
        }

        return builder
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return customView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val chara = setChara(posi)
        val data=prefs.getString(chara.prefer,"")
        Log.e("TAG","テキストは今$data")
        settingText.text = Editable.Factory.getInstance().newEditable(data)
        var flg = true//初回利用ならフラグが立つ
        if(data!!.isNotEmpty()){ flg = false }
        val text = customView.findViewById<EditText>(R.id.settingText)//.text.toString()
        set.setOnClickListener {
            Log.e("TAG","入力したテキストは${text.text}")
            save.putString(chara.prefer, text.text.toString())
            save.commit()
            val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            if(flg) {
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val docRef = db.collection("Scores").document(user!!.uid)
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    val score = documentSnapshot.toObject(Score::class.java)
                    if (documentSnapshot.data != null && score != null) {
                        val totalPoint = score.totalPoint
                        val new = totalPoint + chara.point.toInt()
                        docRef.update("totalPoint",new)

                        val Prelevel = calculate(totalPoint, 450, -450, 100)
                        val newlevel = calculate(new, 450, -450, 100)
                        if(Prelevel>newlevel){
                            showLevelUp("レベルアップ！！","Lv.$newlevel\n経験値　＋${chara.point}")
                        }else{
                            showLevelUp("経験値獲得！！","経験値　＋${chara.point}")
                        }
                    }
                }
            }else{
                dismiss()
            }


        }

        unset.setOnClickListener {
            save.putString(chara.prefer, "")
            save.commit()
            Toast.makeText(requireContext(),"設定を解除しました",Toast.LENGTH_SHORT).show()
            dismiss()
        }


    }
    //wanwan 用のダイアログ
    fun wanwanDialog(context: Context) {
        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->

                val b=(hour*60+minute).toString()
                Log.e("tag","wanwanに入れた値は$b")
                save.putString("wanwan", b)
                save.apply()
                Toast.makeText(context,"$hour 時$minute 分に設定しました", Toast.LENGTH_SHORT).show()
            },
            13, 0, true)
        timePickerDialog.show()
    }

    fun showLevelUp(title:String,script:String){
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(script)
        alertDialogBuilder.setPositiveButton("閉じる"){dialog, which ->        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}