package jp.tsumura.takuya.self_maintenance.ForCharacter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_chara_deteal.*


class CharaDetealDialogFragment(position:Int,level:Int): DialogFragment() {

    val posi = position
    val Lv = level
    //private lateinit var customView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog
        dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_chara_deteal, null)
        return customView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.setContentView(R.layout.dialog_chara_deteal)//customView
        dialog?.window?.setLayout(700,1100)

        val chara = Characters.setChara(posi)
        if (Lv >= chara.level) {
            dialog?.title!!.text = chara.name
            dialog?.bonus!!.text = "ボーナス経験値　＋${chara.point}"
            dialog?.imageView!!.setImageResource(chara.icon)
            dialog?.explanation!!.text = chara.expl
            dialog?.tech_point!!.text = chara.tech
        } else {
            dialog?.title!!.text = Characters.setChara(-1).name
            dialog?.bonus!!.text = "ボーナス　　${Characters.setChara(-1).point}"
            dialog?.imageView!!.setImageResource(Characters.setChara(-1).icon)
            dialog?.explanation!!.text = Characters.setChara(-1).expl
            dialog?.tech_point!!.text = Characters.setChara(-1).tech
        }
    }

}
