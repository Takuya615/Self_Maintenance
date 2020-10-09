package jp.tsumura.takuya.self_maintenance.ForSetting

import java.io.Serializable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Person (
    @PrimaryKey var id: Int = 0,
    var Name: String = "",
    var AcUid: String = ""
) : RealmObject(), Serializable