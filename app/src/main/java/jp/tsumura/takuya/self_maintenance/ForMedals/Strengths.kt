package jp.tsumura.takuya.self_maintenance.ForMedals

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.util.*
import kotlin.collections.ArrayList

class Strengths{

    companion object{

        val ViaStrItem = mutableListOf<String>("創造性", "勇敢さ","好奇心","公平さ","寛容さ","審美眼","感謝","誠実さ",
            "希望","慎み深さ","ユーモア","柔軟性","親切心","リーダーシップ","愛情","向学心","大局観","忍耐力","思慮深さ",
            "自己調整","社会的知性","スピリチュアリティ","チームワーク","熱意")


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

        fun strengthsExplain(title:String):String{
            val ex = mutableListOf<String>(
                "何か物事を行うのに真新しく生産性の高いやり方を考える力",
                "試練困難苦痛などに決して怯まない。反対にあっても正しい事をきちんと言う \n",
                "あらゆる経験それ自体に興味を持ち、主題やテーマに対して興味深いと感じる力。探究心を発揮して新しいことを発見することを好む\n",
                "公平や正義という概念に従って、あらゆる人々を同様に扱う。自身の個人的な感情が他者への評価を歪めることがない。皆に公平にチャンスを与える",
                "過ちを犯した人を許す。やり直しのチャンスを与える。決して復讐心を持たない慈悲の精神を持っている",

                "人生のあらゆる領域に、美や卓越性、熟練の技を見出し、それらの真価を認める人",
                "自分や周りに起こった良い出来事に目を向け、それに感謝する心を持っている\n",
                "自分の気持ちと行動に対して責任をもち、偽りなく存在している",
                "素晴らしい未来を描いてそれが達成できるように努力する。良い未来がもたらされると信じる",
                "自分の業績を自慢せず、自身が脚光を浴びることを求めない謙虚さがある。ありのままの自分以上に自分のことを特別だとは考えない\n",

                "笑いやいたずらを好み、人に笑いをもたらす",
                "あらゆる角度から物事を考え抜いて検討する力 証拠に照らして判断を変えることができる。",
                "人に親切にし、人のために良いことをする 見知らぬ相手に対しても喜んで良い行いができる人",
                "グループのメンバーが活動しやすいように支援し実現できるように動く力。グループ内で良い人間関係が保てるよう尽力できる",
                "人との親密性。互いに共感しあったり思いやったりする関係に重きを置く\n",

                "いつでもどこでも学ぶ機会を得るのが大好き。好奇心の枠にとどまらず、既知の知識についても体系的に理解を深める傾向がある ",
                "自分やまた、他人にとっても納得できるような世の中に対する見方を身につけている。他人に賢明な助言ができる\n",
                "始めたことを最後までやり遂げる力。困難にあっても粘り強く前進し続ける。やりとげることに喜びを見出している。\n",
                "注意深く選択する。不必要なリスクは決して取らない。後悔するような言動は取らない。",
                "自分の気持ちや振る舞いをコントロールできる。規則正しい。食欲や感情をコントロールする。\n",

                "他人や自分自身の動機や感情を意識している。他者を動かすにはどうすれば良いかを知っている。他者との適切な振る舞い方を知っている\n",
                "より高次の目的や森羅万象の意味について一貫した信念を持つ。 自分がより大きな枠組みの中で、どこに位置するのかを理解している。",
                "グループやチームの一員としてうまく立ち振るまえる。グループの成功に貢献し、その中で自分のやるべきことを行える",
                "感動と情熱を持って生きる 。物事を中途半端にすることなく、いきいきと活動的。"

            )
            val num = ViaStrItem.indexOf(title)

            return ex[num]
        }

    }

}