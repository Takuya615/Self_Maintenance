package jp.tsumura.takuya.self_maintenance.ForCamera

class Score (
             val continuous:Int = 0,
             val recover:Int = 0,
             val totalD:Int = 0,
             val totalT:Int = 0,
             val DoNot:Int = 0,
             val totalPoint:Int = 0,
             val vialist:MutableList<Int> = mutableListOf(1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1,1 ,1,1,1,1)
)