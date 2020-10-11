package jp.tsumura.takuya.self_maintenance.forGallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.friend_list_item.view.*

class FriendListAdapter(private val customList: MutableList<String>) : RecyclerView.Adapter<FriendListAdapter.CustomViewHolder>() {

    //リスナー
    lateinit var listener: OnItemClickListener

    // ViewHolderクラス(別ファイルに書いてもOK)
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.itemdeleate
        val sampleTxt = view.itemTextView
    }

    // getItemCount onCreateViewHolder onBindViewHolderを実装
    // 上記のViewHolderクラスを使ってViewHolderを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.friend_list_item, parent, false)
        return CustomViewHolder(item)
    }

    // recyclerViewのコンテンツのサイズ
    override fun getItemCount(): Int {
        return customList.size
    }

    // ViewHolderに表示する画像とテキストを挿入
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.itemTextView.text = customList[position]

        holder.view.itemTextView.setOnClickListener {
            listener.onItemClickListener(it, position, customList[position])
        }

        holder.view.itemdeleate.setOnClickListener {
            listener.onItemClickListener(it, position, customList[position])
        }

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