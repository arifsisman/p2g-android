package vip.yazilim.p2g.android.ui.room.roominvite

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_room_invite.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.entity.RoomInvite
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton

/**
 * @author mustafaarifsisman - 14.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInviteFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_invite),
    RoomInviteAdapter.OnItemClickListener {
    private lateinit var adapter: RoomInviteAdapter

    override fun setupUI() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RoomInviteAdapter(roomViewModel.inviteUserList.value ?: mutableListOf(), this)
        recyclerView.adapter = adapter

        // recyclerView divider
        recyclerView.addItemDecoration(object : DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        swipeRefreshContainer.setOnRefreshListener { refreshRoomInviteUsers() }
    }

    private fun refreshRoomInviteUsers() = request(
        Singleton.apiClient().getAllUsers(),
        object : Callback<MutableList<User>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, "Users cannot refreshed")
                swipeRefreshContainer.isRefreshing = false
            }

            override fun onSuccess(obj: MutableList<User>) {
                roomViewModel.inviteUserList.value = obj
                adapter.update(obj)
                swipeRefreshContainer.isRefreshing = false
            }
        })

    override fun setupViewModel() {
        roomViewModel.inviteUserList.observe(this, renderRoomInviteUsers)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomViewModel.loadRoomInviteUsers()
    }

    // Observer
    private val renderRoomInviteUsers = Observer<MutableList<User>> { userList ->
        Log.v(TAG, "data updated $userList")
        layoutError.visibility = View.GONE
        adapter.update(userList)
    }

    override fun onItemClicked(view: View, user: User) {
        val roomId = (activity as RoomActivity).room?.id
        val userId = user.id

        if (roomId != null && userId != null) {
            request(Singleton.apiClient().inviteUser(roomId, userId),
                object : Callback<RoomInvite> {
                    override fun onSuccess(obj: RoomInvite) {
                        UIHelper.showSnackBarShortTop(root, "${user.name} invited to room.")
                    }

                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShortTop(root, msg)
                    }
                })
        }
    }

    override fun onInviteClicked(view: View, user: User) {
        onItemClicked(view, user)
    }

}