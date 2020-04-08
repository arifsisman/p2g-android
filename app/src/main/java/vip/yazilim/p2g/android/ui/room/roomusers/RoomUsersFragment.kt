package vip.yazilim.p2g.android.ui.room.roomusers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_room_invite.view.*
import kotlinx.android.synthetic.main.fragment_room_users.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.entity.RoomInvite
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboard
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarInfo

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersFragment :
    FragmentBase(R.layout.fragment_room_users),
    RoomUsersAdapter.OnItemClickListener,
    RoomInviteAdapter.OnItemClickListener,
    ChangeRoleAdapter.OnItemClickListener,
    SwipeLayout.SwipeListener {

    private lateinit var roomActivity: RoomActivity
    private lateinit var adapter: RoomUsersAdapter

    private lateinit var inviteAdapter: RoomInviteAdapter
    private lateinit var inviteDialogView: View

    private lateinit var roomViewModel: RoomViewModel

    private lateinit var changeRoleAdapter: ChangeRoleAdapter
    private var changeRoleDialogView: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomViewModel = ViewModelProvider(activity as RoomActivity).get(RoomViewModel::class.java)
    }

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter =
            RoomUsersAdapter(roomViewModel.roomUserModelList.value ?: mutableListOf(), this, this)
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

        fab_invite.setOnClickListener { showInviteDialog() }
    }

    override fun setupViewModel() {
        setupDefaultObservers(roomViewModel)
    }

    // Observer
    private val renderRoomUserModelList = Observer<MutableList<RoomUserModel>> { roomUserModels ->
        adapter.update(roomUserModels)

        roomUserModels.forEach {
            if (it.user?.id == roomViewModel.roomUserModel.value?.user?.id) {
                roomViewModel.roomUserModel.postValue(it)
            }
        }
    }

    private fun refreshUsersEvent() = roomActivity.room?.id?.let {
        Api.client.getRoomUserModels(it).queue(
            object : Callback<MutableList<RoomUserModel>> {
                override fun onError(msg: String) {
                    roomViewModel.onMessageError.postValue(resources.getString(R.string.err_room_user_refresh))
                    swipeRefreshContainer.isRefreshing = false
                }

                override fun onSuccess(obj: MutableList<RoomUserModel>) {
                    roomViewModel.roomUserModelList.postValue(obj)
                    swipeRefreshContainer.isRefreshing = false
                }
            })
    }

    private fun showInviteDialog() {
        inviteDialogView = View.inflate(context, R.layout.dialog_room_invite, null)

        val mBuilder = MaterialAlertDialogBuilder(context)
            .setView(inviteDialogView)
        val mAlertDialog = mBuilder.show()

        val queryEditText = inviteDialogView.dialogQuery
        val searchButton = inviteDialogView.dialog_search_button
        val cancelButton = inviteDialogView.dialog_close_button

        // For disable create button if name is empty
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchButton.isEnabled = s.length > 2
            }
        })

        cancelButton.setOnClickListener {
            mAlertDialog.cancel()
            queryEditText.clearFocus()
        }

        val inviteRecyclerView: RecyclerView =
            inviteDialogView.findViewById(R.id.inviteRecyclerView)
        inviteRecyclerView.setHasFixedSize(true)
        inviteRecyclerView.layoutManager = LinearLayoutManager(activity)

        inviteAdapter =
            RoomInviteAdapter(
                mutableListOf(),
                this@RoomUsersFragment
            )
        inviteAdapter.clear()
        inviteRecyclerView.adapter = inviteAdapter

        inviteRecyclerView.addItemDecoration(object : DividerItemDecoration(
            inviteRecyclerView.context,
            (inviteRecyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        searchButton.setOnClickListener {
            val query = queryEditText.text.toString()

            Api.client.searchUser(query).queue(
                object : Callback<MutableList<User>> {
                    override fun onError(msg: String) {
                        inviteRecyclerView.showSnackBarError(msg)
                    }

                    override fun onSuccess(obj: MutableList<User>) {
                        context?.closeKeyboard()
                        inviteAdapter.update(obj)

                        // Search text query
                        val searchText: TextView = inviteDialogView.findViewById(R.id.searchText)
                        val searchTextPlaceholder =
                            "${resources.getString(R.string.info_search_invite)} '${query}'"
                        searchText.text = searchTextPlaceholder
                        searchText.visibility = View.VISIBLE
                    }
                })
        }

        // Load friends
        Api.client.getFriends().queue(
            object : Callback<MutableList<User>> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: MutableList<User>) {
                    inviteAdapter.update(obj)
                }
            })

    }

    override fun onChangeRoleClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        view.close()
        val mDialogView = View.inflate(context, R.layout.dialog_change_role, null)
        val mBuilder = context?.let { MaterialAlertDialogBuilder(it).setView(mDialogView) }
        changeRoleDialogView = mBuilder?.show()

        val changeRoleRecyclerView: RecyclerView = mDialogView.findViewById(R.id.rolesRecyclerView)
        changeRoleRecyclerView.setHasFixedSize(true)
        changeRoleRecyclerView.layoutManager = LinearLayoutManager(activity)

        changeRoleAdapter = ChangeRoleAdapter(roomUserModel, emptyList(), this@RoomUsersFragment)
        changeRoleRecyclerView.adapter = changeRoleAdapter

        changeRoleRecyclerView.addItemDecoration(object : DividerItemDecoration(
            changeRoleRecyclerView.context,
            (changeRoleRecyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        val roles = Role.values().toMutableList()
        roles.removeAt(0)
        roles.removeAt(0)
        changeRoleAdapter.update(roles)
    }

    override fun onAddClicked(view: SwipeLayout, roomUserModel: RoomUserModel) {
        view.close()

        roomUserModel.roomUser?.userId?.let {
            Api.client.addFriend(it).queue(
                object : Callback<Boolean> {
                    override fun onSuccess(obj: Boolean) {
                        roomViewModel.onMessageInfo.postValue("${resources.getString(R.string.info_friend_request_send)} ${roomUserModel.user?.name}")
                    }

                    override fun onError(msg: String) {
                        roomViewModel.onMessageError.postValue(msg)
                    }
                })
        }
    }

    override fun onItemClicked(view: View, user: User) {
        val roomId = (activity as RoomActivity).room?.id
        val userId = user.id

        if (roomId != null && userId != null) {
            Api.client.inviteUser(roomId, userId).queue(
                object : Callback<RoomInvite> {
                    override fun onSuccess(obj: RoomInvite) {
                        inviteDialogView.showSnackBarInfo("${user.name} ${resources.getString(R.string.info_room_invite_send)}")
                    }

                    override fun onError(msg: String) {
                        inviteDialogView.showSnackBarError(msg)
                    }
                })
        }
    }

    override fun onOpen(layout: SwipeLayout?) {
    }

    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
    }

    override fun onStartOpen(layout: SwipeLayout?) {
        val currentRole = roomViewModel.roomUserModel.value?.roomUser?.role
        if (currentRole == Role.ROOM_ADMIN.role || currentRole == Role.ROOM_OWNER.role) {
            layout?.findViewById<ImageButton>(R.id.swipeChangeRoleButton)?.visibility = View.VISIBLE
        } else {
            layout?.findViewById<ImageButton>(R.id.swipeChangeRoleButton)?.visibility = View.GONE
        }
    }

    override fun onStartClose(layout: SwipeLayout?) {
    }

    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
    }

    override fun onClose(layout: SwipeLayout?) {
    }

    override fun onItemClicked(view: View, roomUserModel: RoomUserModel, role: Role) {
        changeRoleDialogView?.dismiss()

        roomUserModel.roomUser?.id?.let {
            Api.client.changeRoomUserRole(it, role.role).queue(
                object : Callback<RoomUser> {
                    override fun onSuccess(obj: RoomUser) {
                        roomViewModel.onMessageInfo.postValue(
                            "${roomUserModel.user?.name}${resources.getString(R.string.info_promote_demote)} ${obj.role}"
                        )
                    }

                    override fun onError(msg: String) {
                        roomViewModel.onMessageError.postValue(msg)
                    }
                })
        }
    }
}