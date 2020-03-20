package vip.yazilim.p2g.android.ui.room.roomusers

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
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
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.entity.RoomUser
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.options_menu_room, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Observer
    private val renderRoomUserModelList = Observer<MutableList<RoomUserModel>> { roomUserModels ->
        Log.v(TAG, "data updated $roomUserModels")
        layoutError.visibility = View.GONE

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
                UIHelper.showSnackBarShortTop(root, "Users cannot refreshed")
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

    override fun onPromoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        view.close()

        if (roomUserModel.roomUser?.role == Role.ROOM_ADMIN.role) {
            val userName = roomUserModel.roomUser?.userName
            val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
                when (ans) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        request(
                            roomUserModel.roomUser?.id?.let {
                                Singleton.apiClient().changeRoomOwner(it)
                            },
                            object : Callback<Boolean> {
                                override fun onSuccess(obj: Boolean) {
                                    UIHelper.showSnackBarShortTop(
                                        root,
                                        "${roomUserModel.user?.name}'s role updated as ${Role.ROOM_OWNER.role}"
                                    )
                                }

                                override fun onError(msg: String) {
                                    UIHelper.showSnackBarShortTop(root, msg)
                                }
                            })
                    }
                }
            }

            AlertDialog.Builder(activity)
                .setMessage("Are you sure you want make $userName's Role ROOM_OWNER? (Your role will be demoted to ROOM_ADMIN)")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show()
        } else {
            request(
                roomUserModel.roomUser?.id?.let { Singleton.apiClient().promoteUser(it) },
                object : Callback<RoomUser> {
                    override fun onSuccess(obj: RoomUser) {
                        UIHelper.showSnackBarShortTop(
                            root,
                            "${roomUserModel.user?.name}'s role updated as ${obj.role}"
                        )
                    }

                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShortTop(root, msg)
                    }
                })
        }
    }

    override fun onDemoteClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        view.close()

        request(
            roomUserModel.roomUser?.id?.let { Singleton.apiClient().demoteUser(it) },
            object : Callback<RoomUser> {
                override fun onSuccess(obj: RoomUser) {
                    UIHelper.showSnackBarShortTop(
                        root,
                        "${roomUserModel.user?.name}'s role updated as ${obj.role}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortTop(root, msg)
                }
            })
    }

    override fun onAddClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        view.close()

        request(
            roomUserModel.roomUser?.userId?.let { Singleton.apiClient().addFriend(it) },
            object : Callback<Boolean> {
                override fun onSuccess(obj: Boolean) {
                    UIHelper.showSnackBarShortTop(
                        root,
                        "Friend request sent to ${roomUserModel.user?.name}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortTop(root, msg)
                }
            })
    }
}