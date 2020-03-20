package vip.yazilim.p2g.android.ui.main.friends

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_room_password.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.model.p2g.FriendModel
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboard
import vip.yazilim.p2g.android.util.refrofit.Singleton


class FriendsFragment : FragmentBase(
    R.layout.fragment_friends
),
    FriendsAdapter.OnItemClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        adapter.clearDataList()
        adapter.clearDataListFull()
        viewModel.loadFriendRequestModel()
        viewModel.loadFriends()
    }

    override fun setupViewModel() {
        viewModel = super.setupMainViewModel()
        viewModel.friendRequestModel.observe(this, renderData)
    }


    override fun setupUI() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FriendsAdapter(viewModel.friendRequestModel.value ?: mutableListOf(), this, this)
        recyclerView.adapter = adapter

        // SwipeRefreshLayout
        swipeRefreshContainer.setOnRefreshListener {
            adapter.clearDataList()
            adapter.clearDataListFull()
            loadFriendRequestModel()
            loadFriends()
        }

    }

    // Observer
    private val renderData = Observer<MutableList<Any>> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.adapterDataListFull.addAll(it)
        adapter.addAll(it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = resources.getString(R.string.hint_search_friends)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) = request(
        friendRequestModel.friendRequest?.id?.let { Singleton.apiClient().accept(it) },
        object : Callback<Boolean> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
            }

            override fun onSuccess(obj: Boolean) {
                adapter.remove(friendRequestModel)
                friendRequestModel.friendRequestUserModel?.let {
                    adapter.add(
                        FriendModel(
                            it,
                            null
                        )
                    )
                }
                friendRequestModel.friendRequestUserModel?.let {
                    adapter.adapterDataListFull.add(FriendModel(it, null))
                }
            }
        })


    override fun onRejectClicked(friendRequestModel: FriendRequestModel) = request(
        friendRequestModel.friendRequest?.id?.let { Singleton.apiClient().reject(it) },
        object : Callback<Boolean> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
            }

            override fun onSuccess(obj: Boolean) {
                adapter.remove(friendRequestModel)
            }
        })


    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) = request(
        friendRequestModel.friendRequest?.id?.let { Singleton.apiClient().ignore(it) },
        object : Callback<Boolean> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
            }

            override fun onSuccess(obj: Boolean) {
                adapter.remove(friendRequestModel)
            }
        })


    override fun onJoinClicked(room: Room?) {
        if (room?.password == null) {
            room?.let { joinRoomEvent(it) }
        } else {
            joinPrivateRoomEvent(room)
        }
    }

    override fun onDeleteClicked(friendModel: FriendModel?) {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    request(
                        friendModel?.userModel?.user?.id?.let {
                            Singleton.apiClient().deleteFriend(it)
                        },
                        object : Callback<Boolean> {
                            override fun onError(msg: String) {
                                UIHelper.showSnackBarShortTop(root, msg)
                            }

                            override fun onSuccess(obj: Boolean) {
                                friendModel?.let { adapter.remove(it) }
                            }
                        })
                }
            }
        }


        AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.info_delete_friend))
            .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
            .show()
    }

    override fun onRowClicked(userModel: UserModel?) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra("userModel", userModel)
        startActivity(intent)
    }

    private fun loadFriendRequestModel() = request(
        Singleton.apiClient().getFriendRequestModels(),
        object : Callback<MutableList<FriendRequestModel>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
                swipeRefreshContainer.isRefreshing = false
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                adapter.addAll(obj as MutableList<Any>)
                adapter.adapterDataListFull.addAll(obj)
            }
        })


    private fun loadFriends() = request(
        Singleton.apiClient().getFriendModels(),
        object : Callback<MutableList<FriendModel>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
                swipeRefreshContainer.isRefreshing = false
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendModel>) {
                adapter.addAll(obj as MutableList<Any>)
                adapter.adapterDataListFull.addAll(obj)
                swipeRefreshContainer.isRefreshing = false
            }
        })

    private fun joinRoomEvent(room: Room) = request(
        room.id.let { Singleton.apiClient().joinRoom(it, GeneralConstants.UNDEFINED) },
        object : Callback<RoomUser> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
            }

            override fun onSuccess(obj: RoomUser) {
                Log.d(TAG, "Joined room with roomUser ID: " + obj.id)

                val intent = Intent(activity, RoomActivity::class.java)
                intent.putExtra("roomUser", obj)
                intent.putExtra("room", room)
                startActivity(intent)
            }
        })

    private fun joinPrivateRoomEvent(room: Room) {
        val mDialogView = View.inflate(context, R.layout.dialog_room_password, null)
        val mBuilder = AlertDialog.Builder(activity).setView(mDialogView)
        val joinButton = mDialogView.dialog_join_room_button
        val roomPasswordEditText = mDialogView.dialog_room_password
        val mAlertDialog: AlertDialog
        mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        roomPasswordEditText.requestFocus()

        // For disable create button if password is empty
        roomPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                joinButton.isEnabled = s.isNotEmpty()
            }
        })

        // Click join
        joinButton.setOnClickListener {
            val roomPassword = roomPasswordEditText.text.toString()

            request(
                room.id.let { it1 -> Singleton.apiClient().joinRoom(it1, roomPassword) },
                object : Callback<RoomUser> {
                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShortTop(mDialogView, msg)
                    }

                    override fun onSuccess(obj: RoomUser) {
                        Log.d(TAG, "Joined room with roomUser ID: " + obj.id)
                        mAlertDialog.dismiss()
                        context?.closeKeyboard()

                        val intent = Intent(activity, RoomActivity::class.java)
                        startActivity(intent)
                    }
                })
        }

        // Click cancel
        mDialogView.dialog_cancel_button.setOnClickListener {
            mAlertDialog.cancel()
            roomPasswordEditText.clearFocus()
            context?.closeKeyboard()
        }
    }
}