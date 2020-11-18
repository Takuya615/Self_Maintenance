package jp.tsumura.takuya.self_maintenance.ForCharacter

import jp.tsumura.takuya.self_maintenance.R


class Characters(var position:Int,
                 var name:String,
                 var icon:Int,
                 var script:String,
                 var level:Int,
                 var prefer:String,
                 var point:String,
                 var expl:String,
                 var tech:String) {


    companion object{
        fun setChara(num:Int):Characters{
            if(num==-1){
                val question = Characters(-1,"？？？",R.drawable.tree_seichou01,"",0,"question",
                    "？？？","？？？","？？？")
                return question
            }
            val wanwan = Characters(0,"ワンワン",R.drawable.tree_seichou02,
                "あらかじめ設定した時間通りに習慣を始めると\n経験値ボーナス＋１．２倍",1,"wanwan","1.2倍",
            "\n家に帰ると必ずしっぽを振りながら、玄関で出迎えてくれるワンワン。\n設定した時間になるとホーム画面であなたの帰りを" +
                    "待っていてくれます。１５分待ってもあなたが来なければ、あきらめて帰っていきます。\n",
                "\n毎日同じ時間にできるようになると、より習慣が身につきやすくなります。\n")
            val hanako = Characters(1,"地縛霊の花子さん", R.drawable.tree_seichou03,
                "習慣をする場所を決める\n経験値＋１０００",1,"hanako","1000",
            "\n\n",
                "\n例えば、「リビングの空いたスペースのヨガマットの上」など、数メートルの範囲まで絞って場所を決めましょう。" +
                        "習慣をするときは、いつも同じ風景の中で行ったほうがより続けやすくなります。\n")
            val MrQ = Characters(2,"Mｒ.キュー",R.drawable.tree_seichou04,
                "よい習慣を思い出すキッカケをつくる\n経験値＋１０００",2,"mrq","1000",
                "\n\n","\n\n")
            val MsQ = Characters(3,"Ms.キュー",R.drawable.tree_seichou05,
                "わるい習慣を思い出すキッカケをつくる\n経験値＋１０００",15,"msq","1000",
                "\n\n","\n\n")
            //val wanwan = Characters(0,"",1,"",4,"")
            //val wanwan = Characters(0,"",1,"",4,"")
            //val wanwan = Characters(0,"",1,"",4,"")
            //val wanwan = Characters(0,"",1,"",4,"")


            val list = mutableListOf<Characters>(wanwan,hanako,MrQ,MsQ)
            return list[num]
        }
    }
}