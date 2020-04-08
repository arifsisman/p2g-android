package vip.yazilim.p2g.android.model.p2g

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vip.yazilim.p2g.android.constant.enums.SearchType

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Parcelize
data class SearchModel(
    var type: SearchType,
    var name: String,
    var artistNames: ArrayList<String>,
    var albumName: String,
    var id: String,
    var uri: String,
    var durationMs: Int,
    var imageUrl: String?
) : Parcelable