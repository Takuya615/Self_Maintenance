package jp.tsumura.takuya.self_maintenance.ForMedals

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log

import java.util.*
import kotlin.collections.ArrayList

class Strengths{

    companion object{

        val ViaStrItem = mutableListOf<String>("創造性", "勇敢さ","好奇心","公平さ","寛容さ","審美眼","感謝","誠実さ",
            "希望","慎み深さ","ユーモア","柔軟性","親切心","リーダーシップ","愛情","向学心","大局観","忍耐力","思慮深さ",
            "自己調整","社会的知能","スピリチュアリティ","チームワーク","熱意")//ランダムに値を取り出す用

        fun saveVia(sP:SharedPreferences, list: ArrayList<Int>){
            Log.e("TAG","保存されるリストは$list")
            val editor = sP.edit()
            editor.putString("viaItem", list.toString());
            editor.apply();
        }




        /**
         * リストから指定された数の要素をランダムに重複なく取り出す。
         *
         * @receiver     このリストから要素を取り出す。
         * @param n      取り出す要素の数。
         * @param random 使用する乱数生成器。
         * @param <T>    要素の型。
         * @return 取得された要素を持つリスト。
         */
        fun <T> Collection<T>.takeAtRandom(n: Int, random: Random = Random()): List<T> {
            require(n <= size) { "引数 n の値 $n がレシーバーのサイズ ${size} より大きいです。" }

            val taken = mutableListOf<T>() // ランダムに選択された要素を持たせるリスト

            val remaining = toMutableList() // 残っている要素のリスト
            // n 回繰り返す。
            repeat(n) {
                val remainingCount = remaining.size // 残っている要素の数
                val index = random.nextInt(remainingCount) // ランダムに選択されたインデックス

                taken += remaining[index] // ランダムに選択された要素

                val lastIndex = remainingCount - 1 // 残っている要素のリストの末尾のインデックス
                val lastElement = remaining.removeAt(lastIndex) // 残っている要素のリストから末尾を削除する。
                if (index < lastIndex) { // ランダムに選択された要素が末尾以外なら…
                    remaining[index] = lastElement // それを末尾の要素で置換する。
                }
            }

            return taken
        }

    }




}