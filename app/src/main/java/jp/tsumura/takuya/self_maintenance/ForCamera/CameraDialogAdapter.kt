package jp.tsumura.takuya.self_maintenance.ForCamera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import jp.tsumura.takuya.self_maintenance.AchievementAdapter
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.achivement_list_item.view.*
import kotlinx.android.synthetic.main.dialog_camera.view.*
import kotlinx.android.synthetic.main.dialog_camera_list_item.view.*

class CameraDialogAdapter(private val customList: MutableList<String>) : RecyclerView.Adapter<CameraDialogAdapter.CustomViewHolder>()  {


    //リスナー
    lateinit var listener: OnItemClickListener

    // ViewHolderクラス(別ファイルに書いてもOK)
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.dialogText
        val sampleTex = view.dialogMedal

    }

    // getItemCount onCreateViewHolder onBindViewHolderを実装
    // 上記のViewHolderクラスを使ってViewHolderを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.dialog_camera_list_item, parent, false)
        return CustomViewHolder(item)
    }

    // recyclerViewのコンテンツのサイズ
    override fun getItemCount(): Int {
        return customList.size
    }

    // ViewHolderに表示する画像とテキストを挿入
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.dialogText.text = customList[position]

/*
        holder.view.get_button.setOnClickListener {
            listener.onItemClickListener(it, position, customList[position])
        }

 */
    }
    //インターフェースの作成
    interface OnItemClickListener{
        fun onItemClickListener(view: View, position: Int, clickedText: String)
    }

    // リスナー
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

}