package vip.yazilim.p2g.android.ui.room.roomusers

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.swipe.SwipeLayout
import kotlinx.android.synthetic.main.fragment_room_users.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_users),
    RoomUsersAdapter.OnItemClickListener {

    private lateinit var roomActivity: RoomActivity
    private lateinit var adapter: RoomUsersAdapter

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomUsersAdapter(roomViewModel.roomUserList.value ?: mutableListOf(), this)

        recyclerView.adapter = adapter

        // recyclerView divider
        recyclerView.addItemDecoration(object : DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        roomViewModel.roomUserList.observe(this, renderRoomUsers)

        swipeRefreshContainer.setOnRefreshListener {
            refreshUsersEvent()
        }
    }

    override fun setupViewModel() {
        roomViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        roomViewModel.onMessageError.observe(this, onMessageErrorObserver)
        roomViewModel.isEmptyList.observe(this, emptyListObserver)
    }

    override fun onResume() {
        super.onResume()
        roomActivity.room?.id?.let { roomViewModel.loadRoomUsers(it) }
    }

    // Observer
    private val renderRoomUsers = Observer<MutableList<RoomUserModel>> { roomUserModels ->
        Log.v(TAG, "data updated $roomUserModels")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE

        adapter.update(roomUserModels)
    }

    private fun refreshUsersEvent() = request(
        roomActivity.room?.id?.let { Singleton.apiClient().getRoomUserModels(it) },
        object : Callback<MutableList<RoomUserModel>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortBottom(container, "Users cannot refreshed")
                swipeRefreshContainer.isRefreshing = false
            }

            override fun onSuccess(obj: MutableList<RoomUserModel>) {
                adapter.update(obj)
                swipeRefreshContainer.isRefreshing = false
            }
        })

    override fun onItemClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        TODO("Not yet implemented")
    }

    override fun onPromoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        TODO("Not yet implemented")
    }

    override fun onDemoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        TODO("Not yet implemented")
    }

    override fun onAddAsFriendClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        TODO("Not yet implemented")
    }
}