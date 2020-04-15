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
import vip.yazilim.p2g.android.api.Api.withCallback
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.model.p2g.FriendModel
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
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = FriendsAdapter(mutableListOf(), this, this)
        recyclerView.adapter = adapter

        // SwipeRefreshLayout
        swipeRefreshContainer.setOnRefreshListener {
            adapter.clearDataList()
            adapter.clearDataListFull()
            loadUserFriendModel()
        }

    }

    // Observer
    private val renderData = Observer<UserFriendModel> {
        if (it.friendRequestModelList.isEmpty() && it.friendModelList.isEmpty()) {
            viewModel.onEmptyList.postValue(true)
            adapter.clearDataList()
            adapter.clearDataListFull()
        } else {
            viewModel.onEmptyList.postValue(false)
            adapter.update(it)
            it.friendModelList.forEach { friend -> adapter.adapterDataListFull.add(friend) }
            it.friendRequestModelList.forEach { request -> adapter.adapterDataListFull.add(request) }
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
        Api.client?.accept(friendRequestModel.friendRequest.id)
            ?.withCallback(object : Callback<Boolean> {
                override fun onError(msg: String) {
                    viewModel.onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                    adapter.add(FriendModel(friendRequestModel.friendRequestUserModel, null))
                    adapter.adapterDataListFull.add(
                        FriendModel(
                            friendRequestModel.friendRequestUserModel,
                            null
                        )
                    )
                }
            })
    }


    override fun onRejectClicked(friendRequestModel: FriendRequestModel) {
        Api.client?.reject(friendRequestModel.friendRequest.id)?.withCallback(
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


    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) {
        Api.client?.ignore(friendRequestModel.friendRequest.id)?.withCallback(
            object : Callback<Boolean> {
                override fun onError(msg: String) {
                    viewModel.onMessageError.postValue(msg)
                }

                override fun onSuccess(obj: Boolean) {
                    adapter.remove(friendRequestModel)
                }
            })
    }

    override fun onJoinClicked(room: Room) {
        (activity as MainActivity).onItemClicked(room)
    }

    override fun onDeleteClicked(friendModel: FriendModel) {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Api.client?.deleteFriend(friendModel.userModel.user.id)?.withCallback(
                        object : Callback<Boolean> {
                            override fun onError(msg: String) {
                                viewModel.onMessageError.postValue(msg)
                            }

                            override fun onSuccess(obj: Boolean) {
                                adapter.remove(friendModel)
                            }
                        })
                }
            }
        }


        MaterialAlertDialogBuilder(context)
            .setMessage(resources.getString(R.string.info_delete_friend))
            .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
            .show()
    }

    override fun onRowClicked(userModel: UserModel) {
        val intent = Intent(activity, UserActivity::class.java)
        intent.putExtra("userModel", userModel)
        startActivity(intent)
    }

    private fun loadUserFriendModel() = Api.client?.getUserFriendModel()?.withCallback(
        object : Callback<UserFriendModel> {
            override fun onError(msg: String) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.onMessageError.postValue(msg)
            }

            override fun onSuccess(obj: UserFriendModel) {
                swipeRefreshContainer.isRefreshing = false
                viewModel.onViewLoading.postValue(false)
                viewModel.userFriendModel.postValue(obj)
            }
        })

}