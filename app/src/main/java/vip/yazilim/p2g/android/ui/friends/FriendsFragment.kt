package vip.yazilim.p2g.android.ui.friends

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.dialog_room_password.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_recycler_view_base.layoutEmpty
import kotlinx.android.synthetic.main.layout_recycler_view_base.layoutError
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.RoomUser
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.util.helper.UIHelper


class FriendsFragment : FragmentBase(
    FriendsViewModel(),
    R.layout.fragment_friends
),
    FriendsAdapter.OnItemClickListener {

    private lateinit var viewModel: FriendsViewModel
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
        viewModel = super.setupViewModelBase() as FriendsViewModel
        viewModel.data.observe(this, renderData)
    }


    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FriendsAdapter(viewModel.data.value ?: mutableListOf(), this, this)
        recyclerView.adapter = adapter

        val swipeContainer = root.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener {
            adapter.clearDataList()
            adapter.clearDataListFull()
            loadFriendRequestModel()
            loadFriends()
        }
    }

    // Observer
    private val renderData = Observer<MutableList<Any>> {
        Log.v(LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.adapterDataListFull.addAll(it)
        adapter.addAll(it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Friends"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("queryText", query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                Log.d("queryText", newText)
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.requestFocus()
                searchView.isIconified = false
                searchView.isIconifiedByDefault = false
                searchView.visibility = View.VISIBLE
                setMenuItemsVisibility(menu, searchItem, false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                adapter.filter.filter("")
                searchView.isIconified = true
                searchView.isIconifiedByDefault = true
                searchView.visibility = View.VISIBLE
                setMenuItemsVisibility(menu, searchItem, true)
                return true
            }
        })
    }

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) {
        P2GRequest.build(
            friendRequestModel.friendRequest?.id?.let { ApiClient.build().accept(it) },
            object : Callback<Boolean> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showSnackBarLong(root, msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                    friendRequestModel.friendRequestUserModel?.let { adapter.add(it) }
                    friendRequestModel.friendRequestUserModel?.let {
                        adapter.adapterDataListFull.add(it)
                    }
                }
            })
    }

    override fun onRejectClicked(friendRequestModel: FriendRequestModel) {
        P2GRequest.build(
            friendRequestModel.friendRequest?.id?.let { ApiClient.build().reject(it) },
            object : Callback<Boolean> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showSnackBarLong(root, msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                }
            })
    }

    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) {
        P2GRequest.build(
            friendRequestModel.friendRequest?.id?.let { ApiClient.build().ignore(it) },
            object : Callback<Boolean> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showSnackBarLong(root, msg)
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                }
            })
    }

    override fun onJoinClicked(room: Room?) {
        if (room?.password?.isNotEmpty()!!) {
            joinPrivateRoomEvent(room)
        } else {
            joinRoomEvent(room)
        }
    }

    override fun onDeleteClicked(userModel: UserModel) {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        P2GRequest.build(
                            userModel.user?.id?.let { ApiClient.build().deleteFriend(it) },
                            object : Callback<Boolean> {
                                override fun onError(msg: String) {
                                    Log.d(LOG_TAG, msg)
                                    UIHelper.showSnackBarLong(root, msg)
                                }

                                @Suppress("UNCHECKED_CAST")
                                override fun onSuccess(obj: Boolean) {
                                    adapter.remove(userModel)
                                }
                            })
                    }
                }
            }

        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete friend ?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    override fun onRowClicked(userModel: UserModel?) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra("userModel", userModel)
        startActivity(intent)
    }

    private fun loadFriendRequestModel() {
        P2GRequest.build(
            ApiClient.build().getFriendRequestModel(),
            object : Callback<MutableList<FriendRequestModel>> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showSnackBarLong(root, msg)
                    swipeContainer.isRefreshing = false
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                    adapter.addAll(obj as MutableList<Any>)
                    adapter.adapterDataListFull.addAll(obj)
                }
            })
    }

    private fun loadFriends() {
        P2GRequest.build(
            ApiClient.build().getFriends(),
            object : Callback<MutableList<UserModel>> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showSnackBarLong(root, msg)
                    swipeContainer.isRefreshing = false
                }

                @Suppress("UNCHECKED_CAST")
                override fun onSuccess(obj: MutableList<UserModel>) {
                    adapter.addAll(obj as MutableList<Any>)
                    adapter.adapterDataListFull.addAll(obj)
                    swipeContainer.isRefreshing = false
                }
            })
    }

    private fun joinRoomEvent(room: Room) {
        P2GRequest.build(
            room.id.let { ApiClient.build().joinRoom(it, GeneralConstants.UNDEFINED) },
            object : Callback<RoomUser> {
                override fun onError(msg: String) {
                    Log.d(LOG_TAG, msg)
                    UIHelper.showToastLong(context, msg)
//                        UIHelper.showSnackBarLong(root, msg)
                }

                override fun onSuccess(obj: RoomUser) {
                    Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)

                    val intent = Intent(activity, RoomActivity::class.java)
                    intent.putExtra("roomUser", obj)
                    startActivity(intent)
                }
            })
    }

    private fun joinPrivateRoomEvent(room: Room) {
        val mDialogView = View.inflate(context, R.layout.dialog_room_password, null)
        val mBuilder = AlertDialog.Builder(activity).setView(mDialogView)
        val joinButton = mDialogView.dialog_join_room_button
        val roomPasswordEditText = mDialogView.dialog_room_password
        val mAlertDialog: AlertDialog
        mAlertDialog = mBuilder.show()

        roomPasswordEditText.requestFocus()
        showKeyboard()

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

            P2GRequest.build(
                room.id.let { it1 -> ApiClient.build().joinRoom(it1, roomPassword) },
                object : Callback<RoomUser> {
                    override fun onError(msg: String) {
                        Log.d(LOG_TAG, msg)
                        UIHelper.showToastLong(context, msg)
//                            UIHelper.showSnackBarLong(root, msg)
                    }

                    override fun onSuccess(obj: RoomUser) {
                        Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)
                        mAlertDialog.dismiss()
                        closeKeyboard()

                        val intent = Intent(activity, RoomActivity::class.java)
                        startActivity(intent)
                    }
                })
        }

        // Click cancel
        mDialogView.dialog_cancel_button.setOnClickListener {
            mAlertDialog.cancel()
            roomPasswordEditText.clearFocus()
            closeKeyboard()
        }
    }
}