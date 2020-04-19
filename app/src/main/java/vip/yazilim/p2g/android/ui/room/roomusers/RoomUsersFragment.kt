package vip.yazilim.p2g.android.ui.room.roomusers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.entity.User
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboardSoft
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarInfo
import kotlin.coroutines.CoroutineContext

/**
 * @author mustafaarifsisman - 07.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomUsersFragment :
    FragmentBase(R.layout.fragment_room_users),
    RoomUsersAdapter.OnItemClickListener,
    RoomInviteAdapter.OnItemClickListener,
    ChangeRoleAdapter.OnItemClickListener,
    SwipeLayout.SwipeListener,
    CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

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

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(activity)

        adapter =
            RoomUsersAdapter(roomViewModel.roomUserModelList.value ?: mutableListOf(), this, this)
        recycler_view.adapter = adapter

        // recyclerView divider
        recycler_view.addItemDecoration(object : DividerItemDecoration(
            recycler_view.context,
            (recycler_view.layoutManager as LinearLayoutManager).orientation
        ) {})

        roomViewModel.roomUserModelList.observe(this, renderRoomUserModelList)

        swipe_refresh_container.setOnRefreshListener {
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

    private fun refreshUsersEvent() =
        Api.client.getRoomUserModels(roomActivity.room.id).queue(success = {
            roomViewModel.roomUserModelList.postValue(it)
            swipe_refresh_container.isRefreshing = false
        }, failure = {
            roomViewModel.onMessageError.postValue(resources.getString(R.string.err_room_user_refresh))
            swipe_refresh_container.isRefreshing = false
        })

    private fun showInviteDialog() {
        inviteDialogView = View.inflate(context, R.layout.dialog_room_invite, null)

        val mBuilder = context?.let {
            MaterialAlertDialogBuilder(it)
                .setView(inviteDialogView)
        }
        mBuilder?.show()

        val inviteRecyclerView: RecyclerView =
            inviteDialogView.findViewById(R.id.invite_recycler_view)
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

        val queryEditText = inviteDialogView.dialog_query

        inviteRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!inviteRecyclerView.hasFocus()) {
                    inviteRecyclerView.requestFocus()
                }
            }
        })

        queryEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                context?.closeKeyboardSoft(queryEditText.windowToken)
            }
        }

        queryEditText.addTextChangedListener(object : TextWatcher {
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText

                launch {
                    delay(500)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    if (!s.isNullOrEmpty() && s.length > 2) {
                        inviteAdapter.clear()
                        Api.client.searchUser(s.toString())
                            .queue(success = { inviteAdapter.update(it) },
                                failure = { inviteRecyclerView.showSnackBarError(it) })
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit
        })

        // Load friends
        Api.client.getFriends().queue(success = { inviteAdapter.update(it) }, failure = {})
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

        roomUserModel.roomUser?.userId?.let { userId ->
            Api.client.addFriend(userId)
                .queue(
                    success = { roomViewModel.onMessageInfo.postValue("${resources.getString(R.string.info_friend_request_send)} ${roomUserModel.user?.name}") },
                    failure = { roomViewModel.onMessageError.postValue(it) })
        }
    }

    override fun onItemClicked(view: View, user: User) {
        val roomId = (activity as RoomActivity).room.id
        val userId = user.id

        Api.client.inviteUser(roomId, userId).queue(success = {
            inviteDialogView.showSnackBarInfo(
                "${user.name} ${resources.getString(R.string.info_room_invite_send)}"
            )
        }, failure = { inviteDialogView.showSnackBarError(it) })
    }

    override fun onOpen(layout: SwipeLayout?) {
    }

    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
    }

    override fun onStartOpen(layout: SwipeLayout?) {
//        val currentRole = roomViewModel.roomUserRole.value
//        if (currentRole == Role.ROOM_ADMIN.role || currentRole == Role.ROOM_OWNER.role) {
//            layout?.findViewById<ImageButton>(R.id.swipeChangeRoleButton)?.visibility = View.VISIBLE
//        } else {
//            layout?.findViewById<ImageButton>(R.id.swipeChangeRoleButton)?.visibility = View.GONE
//        }
    }

    override fun onStartClose(layout: SwipeLayout?) {
    }

    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
    }

    override fun onClose(layout: SwipeLayout?) {
    }

    override fun onItemClicked(view: View, roomUserModel: RoomUserModel, role: Role) {
        changeRoleDialogView?.dismiss()

        roomUserModel.roomUser?.id?.let { roomUserId ->
            Api.client.changeRoomUserRole(roomUserId, role.role).queue(success = {
                roomViewModel.onMessageInfo.postValue(
                    "${roomUserModel.user?.name}${resources.getString(R.string.info_promote_demote)} ${it.roomRole}"
                )
            }, failure = { roomViewModel.onMessageError.postValue(it) })
        }
    }
}