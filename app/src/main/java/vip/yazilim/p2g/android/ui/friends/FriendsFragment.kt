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
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.activity.room.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


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

        // SwipeRefreshLayout
        val swipeContainer = root.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener {
            adapter.clearDataList()
            adapter.clearDataListFull()
            loadFriendRequestModel()
            loadFriends()
        }

//        // Swipe left for delete
//        val swipeDeleteHandler = object : SwipeToDeleteCallback(context) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val data = adapter.adapterDataList[viewHolder.adapterPosition]
//                if (data is FriendModel) {
//                    onDeleteClicked(data)
//                } else if (data is FriendRequestModel) {
//                    onRejectClicked(data)
//                }
//            }
//        }
//
//        val swipeDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
//        swipeDeleteHelper.attachToRecyclerView(recyclerView)
//
//        // Swipe right for accept
//        val swipeAcceptHandler = object : SwipeToAcceptCallback(
//            ContextCompat.getDrawable(
//                this.context!!,
//                R.drawable.ic_check_white_24dp
//            )!!, Color.parseColor("#1DB954")
//        ) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val data = adapter.adapterDataList[viewHolder.adapterPosition]
//                if (data is FriendModel) {
//                    onJoinClicked(data.userModel?.room)
//                } else if (data is FriendRequestModel) {
//                    onAcceptClicked(data)
//                }
//                adapter.remove(data)
//            }
//        }
//
//        val swipeAcceptHelper = ItemTouchHelper(swipeAcceptHandler)
//        swipeAcceptHelper.attachToRecyclerView(recyclerView)
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

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) = request(
        friendRequestModel.friendRequest?.id?.let { Singleton.apiClient().accept(it) },
        object : Callback<Boolean> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, msg)
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
                UIHelper.showSnackBarShort(root, msg)
            }

            override fun onSuccess(obj: Boolean) {
                adapter.remove(friendRequestModel)
            }
        })


    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) = request(
        friendRequestModel.friendRequest?.id?.let { Singleton.apiClient().ignore(it) },
        object : Callback<Boolean> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, msg)
            }

            override fun onSuccess(obj: Boolean) {
                adapter.remove(friendRequestModel)
            }
        })


    override fun onJoinClicked(room: Room?) {
        if (room?.password?.isNotEmpty()!!) {
            joinPrivateRoomEvent(room)
        } else {
            joinRoomEvent(room)
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
                                UIHelper.showSnackBarShort(root, msg)
                            }

                            override fun onSuccess(obj: Boolean) {
                                friendModel?.let { adapter.remove(it) }
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

    private fun loadFriendRequestModel() = request(
        Singleton.apiClient().getFriendRequestModel(),
        object : Callback<MutableList<FriendRequestModel>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, msg)
                swipeContainer.isRefreshing = false
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendRequestModel>) {
                adapter.addAll(obj as MutableList<Any>)
                adapter.adapterDataListFull.addAll(obj)
            }
        })


    private fun loadFriends() = request(
        Singleton.apiClient().getFriends(),
        object : Callback<MutableList<FriendModel>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, msg)
                swipeContainer.isRefreshing = false
            }

            @Suppress("UNCHECKED_CAST")
            override fun onSuccess(obj: MutableList<FriendModel>) {
                adapter.addAll(obj as MutableList<Any>)
                adapter.adapterDataListFull.addAll(obj)
                swipeContainer.isRefreshing = false
            }
        })

    private fun joinRoomEvent(room: Room) = request(
        room.id.let { Singleton.apiClient().joinRoom(it, GeneralConstants.UNDEFINED) },
        object : Callback<RoomUser> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, "Can not join room")
            }

            override fun onSuccess(obj: RoomUser) {
                Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)

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

            request(
                room.id.let { it1 -> Singleton.apiClient().joinRoom(it1, roomPassword) },
                object : Callback<RoomUser> {
                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShort(root, "Can not join room")
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