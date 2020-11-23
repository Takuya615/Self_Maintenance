package jp.tsumura.takuya.self_maintenance.forGallery

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import jp.tsumura.takuya.self_maintenance.ForCamera.Score
import jp.tsumura.takuya.self_maintenance.ForCharacter.Characters
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.strengthsExplain
import jp.tsumura.takuya.self_maintenance.ForMedals.Strengths.Companion.takeAtRandom
import jp.tsumura.takuya.self_maintenance.ForSetting.mRealm
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.dialog_chara_deteal.*
import kotlinx.android.synthetic.main.dialog_gallery.*
import java.util.*

class GalleryDialogFragment(val coll: DocumentReference,val friendUid: String): DialogFragment()  {

    private lateinit var db : FirebaseFirestore

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        db = FirebaseFirestore.getInstance()
        val dialog: Dialog
        dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gallery, null)
        return customView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.setContentView(R.layout.dialog_gallery)
        dialog?.window?.setLayout(700,800)
        dialog?.StrengthsTitle?.text = "${mRealm().UidToName(friendUid)}さんの\n強みはどちらですか？"

        val item = Strengths.ViaStrItem.takeAtRandom(2, Random())//2つの要素だけを取り出す

        dialog?.Str1button?.text = item[0]
        dialog?.Str2button?.text = item[1]
        var te1 = 0
        var te2 = 0
        val limit = 5
        dialog?.Str1button?.setOnClickListener {
            if(te1!=limit){ te1=te1+1 }
            if(te1+te2 > limit){ te2 = te2-1 }
            dialog?.Str1?.text = te1.toString()
            dialog?.Str2?.text = te2.toString()
        }
        dialog?.Str2button?.setOnClickListener {
            if(te2!=limit){ te2=te2+1 }
            if(te1+te2 > limit){ te1 = te1-1 }
            dialog?.Str1?.text = te1.toString()
            dialog?.Str2?.text = te2.toString()
        }
        dialog?.AddButton?.setOnClickListener {
            addPoint(item[0],friendUid,te1)//強みの評価
            addPoint(item[1],friendUid,te2)
            saveLikeAndNamelist(coll)//いいね＋１、　動画に署名
            requireActivity().finish()
        }
        dialog?.help1?.setOnClickListener{ dialogShow(item[0])
        }
        dialog?.help2?.setOnClickListener{ dialogShow(item[1])
        }

    }

    fun addPoint(item:String,friendUid:String,addNum:Int){

        val saveScore = db.collection("Scores").document(friendUid)
        saveScore.get().addOnSuccessListener { document ->
            val score = document.toObject(Score::class.java)
            if (document.data != null && score != null) {
                val viaList = score.vialist
                val num = Strengths.ViaStrItem.indexOf(item)//ランダムにとった値の要素数（順番数）を取得
                val newList = mutableListOf<Int>()
                for(i in 0..23){
                    if(i!=num){
                        newList.add(viaList[i])
                    }else{
                        newList.add(viaList[i] + addNum)
                    }
                }
                saveScore.update( "vialist",newList)
            }
        }
    }

    fun saveLikeAndNamelist(coll: DocumentReference){
        coll.get().addOnSuccessListener { document ->
            val myid =  FirebaseAuth.getInstance().currentUser!!.uid
            var like = document["like"].toString().toInt()
            like = like + 1
            coll.update("like",like)
            coll.update("$myid","true")
        }
    }
    fun dialogShow(tit:String){
        val ex = strengthsExplain(tit)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(tit)
        alertDialogBuilder.setMessage(ex)
        alertDialogBuilder.setPositiveButton("閉じる",{dialog, which -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}