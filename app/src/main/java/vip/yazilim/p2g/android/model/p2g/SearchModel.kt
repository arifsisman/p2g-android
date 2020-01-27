package vip.yazilim.p2g.android.model.p2g

import vip.yazilim.p2g.android.constant.enums.SearchType
import java.io.Serializable

/**
 * @author mustafaarifsisman - 26.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
data class SearchModel(
    var type: SearchType,
    var name: String,
    var artistNames: List<String>,
    var albumName: String,
    var id: String,
    var uri: String,
    var durationMs: Int,
    var imageUrl: String
) : Serializable
