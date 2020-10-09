package jp.tsumura.takuya.self_maintenance.ForSetting

import android.widget.EditText
import io.realm.Realm
import jp.tsumura.takuya.self_maintenance.R


class Realm {

    val person:Person = Person()
    val realm = Realm.getDefaultInstance()

    fun addPerson(Name:String,AcUid:String) {
        if(!realm.isInTransaction){
            realm.beginTransaction()
        }

        /*if (person == null) {
            // 新規作成の場合
            person = Person()
        }
         */

        val taskRealmResults = realm.where(Person::class.java).findAll()

        val identifier: Int =
            if (taskRealmResults.max("id") != null) {
                taskRealmResults.max("id")!!.toInt() + 1
            } else {
                0
            }
        person.id = identifier
        person.Name = Name
        person.AcUid =AcUid
        realm.copyToRealmOrUpdate(person)
        realm.commitTransaction()
        realm.close()
    }

    fun NameToUid(SearchedName:String):String{
        if(!realm.isInTransaction){
            realm.beginTransaction()
        }
        val taskRealmResults = realm.where(Person::class.java).findAll()
        for (i in taskRealmResults) {
            if(i.Name == SearchedName){

                return i.AcUid
            }
        }
        realm.close()
        return "完全一致する名前が見つかりません"
    }
    //
    fun UidToName(SearchedUid:String):String{
        if(!realm.isInTransaction){
            realm.beginTransaction()
        }
        val taskRealmResults = realm.where(Person::class.java).findAll()
        for (i in taskRealmResults) {
            if(i.AcUid == SearchedUid){

                return i.Name
            }
        }
        realm.close()
        return ""
    }

    //同じ名前がすでに使われていれば、false、使える名前ならtrue
    fun SearchSameName(SearchedName:String):Boolean{
        if(!realm.isInTransaction){
            realm.beginTransaction()
        }
        val taskRealmResults = realm.where(Person::class.java).findAll()
        for (i in taskRealmResults) {
            if(i.Name == SearchedName){

                return false
            }
        }
        realm.close()
        return true
    }
}