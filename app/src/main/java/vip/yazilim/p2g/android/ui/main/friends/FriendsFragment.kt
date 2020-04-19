package vip.yazilim.p2g.android.ui.main.friends

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.MainActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.then
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.UserFriendModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel


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
        viewModel.loadUserFriendModel()
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
        viewModel.userFriendModel.observe(this, renderData)
    }


    override fun setupUI() {
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(activity)

        adapter = FriendsAdapter(mutableListOf(), this, this)
        recycler_view.adapter = adapter

        // SwipeRefreshLayout
        swipe_refresh_container.setOnRefreshListener {
            adapter.clearDataList()
            adapter.clearDataListFull()
            loadUserFriendModel()
        }

    }

    // Observer
    private val renderData = Observer<UserFriendModel> {
        if (it.requestModels.isEmpty() && it.friendModels.isEmpty()) {
            viewModel.onEmptyList.postValue(true)
            adapter.clearDataList()
            adapter.clearDataListFull()
        } else {
            viewModel.onEmptyList.postValue(false)
            adapter.update(it)
            it.friendModels.forEach { friend -> adapter.adapterDataListFull.add(friend) }
            it.requestModels.forEach { request -> adapter.adapterDataListFull.add(request) }
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

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) =
        Api.client.accept(friendRequestModel.friendRequest.id) then { obj, msg ->
            obj?.let {
                adapter.remove(friendRequestModel)
                adapter.add(friendRequestModel.userModel)
                adapter.adapterDataListFull.add(friendRequestModel.userModel)
            }
            msg?.let {
                viewModel.onMessageError.postValue(msg)
            }
        }

    override fun onRejectClicked(friendRequestModel: FriendRequestModel) =
        Api.client.reject(friendRequestModel.friendRequest.id) then { obj, msg ->
            obj?.let {
                adapter.remove(friendRequestModel)
            }
            msg?.let {
                viewModel.onMessageError.postValue(msg)
            }
        }

    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) =
        Api.client.ignore(friendRequestModel.friendRequest.id) then { obj, msg ->
            obj?.let {
                adapter.remove(friendRequestModel)
            }
            msg?.let {
                viewModel.onMessageError.postValue(msg)
            }
        }

    override fun onJoinClicked(room: Room) {
        (activity as MainActivity).onItemClicked(room)
    }

    override fun onDeleteClicked(userModel: UserModel) {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Api.client.deleteFriend(userModel.user.id) then { obj, msg ->
                        obj?.let {
                            adapter.remove(userModel)
                        }
                        msg?.let {
                            viewModel.onMessageError.postValue(msg)
                        }
                    }
                }
            }
        }

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setMessage(resources.getString(R.string.info_delete_friend))
                .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
                .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
                .show()
        }
    }

    override fun onRowClicked(userModel: UserModel) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra("userModel", userModel)
        startActivity(intent)
    }

    private fun loadUserFriendModel() = Api.client.getUserFriendModel() then { obj, msg ->
        obj?.let {
            swipe_refresh_container.isRefreshing = false
            viewModel.onViewLoading.postValue(false)
            viewModel.userFriendModel.postValue(obj)
        }
        msg?.let {
            swipe_refresh_container.isRefreshing = false
            viewModel.onViewLoading.postValue(false)
            viewModel.onMessageError.postValue(msg)
        }
    }
}