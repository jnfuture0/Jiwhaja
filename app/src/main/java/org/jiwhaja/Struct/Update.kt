package org.jiwhaja.Struct

abstract class Update (val updateType:String, val updateBoard:BoardList){
    class TYPE{
        companion object{
            val NEW_FRIEND = "friend"
        }
    }
}

data class ReviewUpdate(val user:BoardList): Update(Update.TYPE.NEW_FRIEND, user)