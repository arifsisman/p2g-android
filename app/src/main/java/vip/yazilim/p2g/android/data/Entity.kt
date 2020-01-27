package vip.yazilim.p2g.android.data

import vip.yazilim.p2g.android.model.p2g.RoomModel

data class RoomModelResponse(val status:Int?, val msg:String?, val data:List<RoomModel>?){
    fun isSuccess():Boolean= (status==200)
}