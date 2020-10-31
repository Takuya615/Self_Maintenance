package jp.tsumura.takuya.self_maintenance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import jp.tsumura.takuya.self_maintenance.forGallery.FriendListAdapter
import kotlinx.android.synthetic.main.achivement_list_item.view.*


class AchievementAdapter(private val customList: MutableList<String>,
                         //private val customList_2: MutableList<String>,
                         private val customList2: MutableList<Int>,
                         //private val customList2_2: MutableList<Int>,
                         private val customList3: MutableList<Int>,
                         //private val customList3_2: MutableList<Int>,
                         private val customList4: MutableList<Boolean>) : RecyclerView.Adapter<AchievementAdapter.CustomViewHolder>() {

    //リスナー
    lateinit var listener: OnItemClickListener

    // ViewHolderクラス(別ファイルに書いてもOK)
    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val sampleImg = view.mission
        val sampleTxt = view.progressBar2
    }

    // getItemCount onCreateViewHolder onBindViewHolderを実装
    // 上記のViewHolderクラスを使ってViewHolderを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.achivement_list_item, parent, false)
        return CustomViewHolder(item)
    }

    // recyclerViewのコンテンツのサイズ
    override fun getItemCount(): Int {
        return customList4.size
    }

    // ViewHolderに表示する画像とテキストを挿入
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.view.mission.text = customList[position]
        holder.view.progressBar2.max = customList2[position]//それぞれのmaxの値を設定
        holder.view.progressBar2.progress = customList3[position]
        //holder.view.mission.text = customList_2[position]
        //holder.view.progressBar2.max = customList2_2[position]//それぞれのmaxの値を設定
        //holder.view.progressBar2.progress = customList3_2[position]
        holder.view.get_button.isInvisible = customList4[position]//１００％->false->獲得ボタンがでる


        holder.view.get_button.setOnClickListener {
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