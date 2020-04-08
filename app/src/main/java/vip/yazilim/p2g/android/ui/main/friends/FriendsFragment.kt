package vip.yazilim.p2g.android.ui.main.friends

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_room_password.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.MainActivity
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.model.p2g.FriendModel
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboard
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError


class FriendsFragment : FragmentBase(
    R.layout.fragment_friends
),
    FriendsAdapter.OnItemClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        adapter.clearDataList()
        adapter.clearDataListFull()
        viewModel.loadFriendRequestModel()
        viewModel.loadFriends()
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
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
        if (it.isNullOrEmpty() && adapter.adapterDataList.isNullOrEmpty()) {
            viewModel.onEmptyList.postValue(true)
            adapter.clearDataList()
            adapter.clearDataListFull()
        } else {
            viewModel.onEmptyList.postValue(false)
            adapter.addAll(it)
            adapter.adapterDataListFull.addAll(it)
        }
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

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) {
        friendRequestModel.friendRequest?.id?.let { fr ->
            Api.client.accept(fr).queue(object : Callback<Boolean> {
                override fun onError(msg: String) {
                    viewModel.onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                    friendRequestModel.friendRequestUserModel?.let {
                        adapter.add(FriendModel(it, null))
                    }
                    friendRequestModel.friendRequestUserModel?.let {
                        adapter.adapterDataListFull.add(FriendModel(it, null))
                    }
                }
            })
        }
    }


    override fun onRejectClicked(friendRequestModel: FriendRequestModel) {
        friendRequestModel.friendRequest?.id?.let {
            Api.client.reject(it).queue(
                object : Callback<Boolean> {
                    override fun onError(msg: String) {
                        viewModel.onMessageError.postValue(msg)
                    }

                    override fun onSuccess(obj: Boolean) {
                        adapter.remove(friendRequestModel)
                    }

                }
            )
        }
    }


    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) {
        friendRequestModel.friendRequest?.id?.let {
            Api.client.ignore(it).queue(
                object : Callback<Boolean> {
                    override fun onError(msg: String) {
                        viewModel.onMessageError.postValue(msg)
                    }

                    override fun onSuccess(obj: Boolean) {
                        adapter.remove(friendRequestModel)
                    }
                })
        }
    }


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
                    friendModel?.userModel?.user?.id?.let {
                        Api.client.deleteFriend(it).queue(
                            object : Callback<Boolean> {
                                override fun onError(msg: String) {
                                    viewModel.onMessageError.postValue(msg)
                                }

                                override fun onSuccess(obj: Boolean) {
                                    friendModel.let { adapter.remove(it) }
                                }
                            })
                    }
                }
            }
        }


        MaterialAlertDialogBuilder(context)
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

    private fun loadFriendRequestModel() = Api.client.getFriendRequestModels().queue(
        object : Callback<MutableList<FriendRequestModel>> {
            override fun onError(msg: String) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.onMessageError.postValue(msg)
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.friendRequestModel.postValue(obj as MutableList<Any>)
            }
        })


    private fun loadFriends() = Api.client.getFriendModels().queue(
        object : Callback<MutableList<FriendModel>> {
            override fun onError(msg: String) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.onMessageError.postValue(msg)
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendModel>) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.friendRequestModel.postValue(obj as MutableList<Any>)
            }
        })

    private fun joinRoomEvent(room: Room) =
        Api.client.joinRoom(room.id, GeneralConstants.UNDEFINED).queue(
            object : Callback<RoomUser> {
                override fun onError(msg: String) {
                    viewModel.onMessageError.postValue(msg)
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
        val mBuilder = MaterialAlertDialogBuilder(context).setView(mDialogView)
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

            Api.client.joinRoom(room.id, roomPassword).queue(
                object : Callback<RoomUser> {
                    override fun onError(msg: String) {
                        mDialogView.showSnackBarError(msg)
                    }

                    override fun onSuccess(obj: RoomUser) {
                        Log.d(TAG, "Joined room with roomUser ID: " + obj.id)
                        mAlertDialog.dismiss()
                        context?.closeKeyboard()

                        val intent = Intent(activity, RoomActivity::class.java)
                        intent.putExtra("roomUser", obj)
                        intent.putExtra("room", room)
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