package vip.yazilim.p2g.android.data.p2g

import org.threeten.bp.LocalDateTime
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class Room(
    var id:Long,
    var name:String,
    var ownerId:String,
    var creationDate:LocalDateTime,
    var privateFlag:Boolean,
    var password:String,
    var maxUsers:Int,
    var usersAllowedQueueFlag:Boolean,
    var usersAllowedControlFlag:Boolean,
    var showRoomActivityFlag:Boolean,
    var activeFlag:Boolean,
    var countryCode:String
) : Serializable