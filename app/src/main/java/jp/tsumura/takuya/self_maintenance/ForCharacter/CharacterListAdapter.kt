package jp.tsumura.takuya.self_maintenance.ForCharacter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import jp.tsumura.takuya.self_maintenance.R
import kotlinx.android.synthetic.main.fragment_character_list_item.view.*

class CharacterListAdapter (private val customList: MutableList<Int>,
                            private val customList2: MutableList<String>,
                            private val customList3: MutableList<String>,
                            private val customList4: MutableList<Boolean>
                            ) : RecyclerView.Adapter<CharacterListAdapter.CustomViewHolder>(){

    //リスナー
    lateinit var listener: OnItemClickListener

    // ViewHolderクラス(別ファイルに書いてもOK)
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.charName
        val sampleTxt = view.charScript
    }

    // getItemCount onCreateViewHolder onBindViewHolderを実装
    // 上記のViewHolderクラスを使ってViewHolderを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.fragment_character_list_item, parent, false)


        return CustomViewHolder(item)
    }

    // recyclerViewのコンテンツのサイズ
    override fun getItemCount(): Int {
        return customList.size
    }

    // ViewHolderに表示する画像とテキストを挿入
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.charImage.setImageResource(customList[position])
        holder.view.charName.text = customList2[position]//それぞれのmaxの値を設定
        holder.view.charScript.text = customList3[position]

        if(customList4[position]){
            holder.view.inviteButton.text="使用中"
            holder.view.inviteButton.getResources().getColor(R.color.colorHighlight)//(R.color.colorHighlight)
        }

        holder.view.inviteButton.setOnClickListener {
            listener.onItemClickListener(it, position, customList2[position])
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