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
import vip.yazilim.p2g.android.model.p2g.RoomUser
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
        adapter = RoomUsersAdapter(roomViewModel.roomUserModelList.value ?: mutableListOf(), this)

        recyclerView.adapter = adapter

        // recyclerView divider
        recyclerView.addItemDecoration(object : DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        roomViewModel.roomUserModelList.observe(this, renderRoomUserModelList)

        swipeRefreshContainer.setOnRefreshListener {
            refreshUsersEvent()
        }
    }

    override fun setupViewModel() {
        roomViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        roomViewModel.onMessageError.observe(this, onMessageErrorObserver)
        roomViewModel.isEmptyList.observe(this, emptyListObserver)
    }

    // Observer
    private val renderRoomUserModelList = Observer<MutableList<RoomUserModel>> { roomUserModels ->
        Log.v(TAG, "data updated $roomUserModels")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE

        adapter.update(roomUserModels)

        roomUserModels.forEach {
            if (it.user?.id == roomViewModel.roomUserModel.value?.user?.id) {
                roomViewModel.roomUserModel.postValue(it)
            }
        }
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
        if (view.openStatus != SwipeLayout.Status.Open) {
            view.toggle()
        }
    }

    override fun onPromoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) =
        request(
            roomUserModel.roomUser?.id?.let { Singleton.apiClient().promoteUser(it) },
            object : Callback<RoomUser> {
                override fun onSuccess(obj: RoomUser) {
                    UIHelper.showSnackBarShortBottom(
                        container,
                        "${roomUserModel.user?.name}'s role updated as ${obj.role}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortBottom(container, msg)
                }
            })

    override fun onDemoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) =
        request(
            roomUserModel.roomUser?.id?.let { Singleton.apiClient().demoteUser(it) },
            object : Callback<RoomUser> {
                override fun onSuccess(obj: RoomUser) {
                    UIHelper.showSnackBarShortBottom(
                        container,
                        "${roomUserModel.user?.name}'s role updated as ${obj.role}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortBottom(container, msg)
                }
            })

    override fun onAddClicked(view: SwipeLayout, roomUserModel: RoomUserModel) =
        request(
            roomUserModel.roomUser?.userId?.let { Singleton.apiClient().addFriend(it) },
            object : Callback<Boolean> {
                override fun onSuccess(obj: Boolean) {
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortBottom(container, msg)
                }
            })
}