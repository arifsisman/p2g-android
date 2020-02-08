package vip.yazilim.p2g.android.ui.friends

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_recycler_view_base.layoutEmpty
import kotlinx.android.synthetic.main.layout_recycler_view_base.layoutError
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.Room
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
        Log.v(LOG_TAG, "Join - ${room?.name}")
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
}