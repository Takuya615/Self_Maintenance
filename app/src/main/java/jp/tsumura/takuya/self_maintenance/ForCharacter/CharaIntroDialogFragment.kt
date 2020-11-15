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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import jp.tsumura.takuya.self_maintenance.ForSetting.SettingDialog
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_chara_intro.*
import kotlinx.android.synthetic.main.dialog_chara_intro.view.*
import kotlinx.android.synthetic.main.fragment_character_list_item.*
import kotlinx.android.synthetic.main.fragment_character_list_item.view.*

class CharaIntroDialogFragment(position:Int) : DialogFragment() {
    val position = position
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

        when(position){

            0->{
                builder.setTitle("ワンワン")
                builder.setIcon(R.drawable.tree_seichou02)
                builder.setMessage("あらかじめ設定した時間どおりに習慣をすると、応援にかけつけてくれます。\n経験値ボーナス　＋１．２倍")
                builder.setPositiveButton("時間をきめる"){ dialog, which ->
                    wanwanDialog(requireContext())
                }
                builder.setNegativeButton("やめる"){ dialog, which ->
                    save.putString("wanwan", "")
                    save.apply()
                }
                return builder
            }
            1->{
                builder.setTitle("地縛霊の花子さん")
                builder.setIcon(R.drawable.tree_seichou04)
                builder.setMessage("習慣をする場所を設定してください。\n経験値ボーナス　＋１０００")
                builder.setView(customView)
                return builder
            }
            2->{
                builder.setTitle("Mr.キュー")
                builder.setIcon(R.drawable.tree_seichou06)
                builder.setMessage("習慣を思い出せるアイテムを部屋に飾りましょう。\n運動＞ヨガマットを敷いておく\n読書＞本を開いた状態で置いておくなど\n経験値ボーナス　＋１０００")
                builder.setView(customView)
                return builder
            }
            3->{
                builder.setTitle("Ms.キュー")
                builder.setIcon(R.drawable.tree_seichou07)
                builder.setMessage("わるい習慣を未然に防げるようにアイテムを部屋に飾りましょう。\n間食を辞めるために、「空腹時はサラダをたべろ！」と張り紙をするなど\n経験値ボーナス　＋１０００")
                builder.setView(customView)
                return builder
            }

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

        when(position){
            1->{
                val hanako=prefs.getString("hanako","")
                Log.e("TAG","花子さんのテキストは今$hanako")
                settingText.text = Editable.Factory.getInstance().newEditable(hanako)
                val text = customView.findViewById<EditText>(R.id.settingText)//.text.toString()
                set.setOnClickListener {

                    Log.e("TAG","入力したテキストは${text.text}")
                    save.putString("hanako", text.text.toString())
                    save.commit()
                    val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    Toast.makeText(requireContext(),"設定しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                unset.setOnClickListener {
                    save.putString("hanako", "")
                    save.commit()
                    Toast.makeText(requireContext(),"設定を解除しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }

            }
            2->{
                val mrq=prefs.getString("mrq","")
                Log.e("TAG","mrqのテキストは今$mrq")
                settingText.text = Editable.Factory.getInstance().newEditable(mrq)
                val text = customView.findViewById<EditText>(R.id.settingText)//.text.toString()
                set.setOnClickListener {

                    Log.e("TAG","入力したテキストは${text.text}")
                    save.putString("mrq", text.text.toString())
                    save.commit()
                    val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    Toast.makeText(requireContext(),"設定しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                unset.setOnClickListener {
                    save.putString("mrq", "")
                    save.commit()
                    Toast.makeText(requireContext(),"設定を解除しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            3->{
                val msq=prefs.getString("msq","")
                Log.e("TAG","msqのテキストは今$msq")
                settingText.text = Editable.Factory.getInstance().newEditable(msq)
                val text = customView.findViewById<EditText>(R.id.settingText)//.text.toString()
                set.setOnClickListener {

                    Log.e("TAG","入力したテキストは${text.text}")
                    save.putString("msq", text.text.toString())
                    save.commit()
                    val im = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    Toast.makeText(requireContext(),"設定しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                unset.setOnClickListener {
                    save.putString("msq", "")
                    save.commit()
                    Toast.makeText(requireContext(),"設定を解除しました",Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

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
}