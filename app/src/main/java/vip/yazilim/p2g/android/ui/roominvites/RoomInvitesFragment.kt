package vip.yazilim.p2g.android.ui.roominvites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.model.p2g.RoomUser
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.util.helper.UIHelper

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesFragment : FragmentBase(RoomInvitesViewModel(), R.layout.fragment_room_invites),
    RoomInvitesAdapter.OnItemClickListener {

    companion object {
        private val TAG = this::class.simpleName
        private const val ACTION_ROOM_INVITE = "RoomInvite"
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val roomInviteModel = intent?.getParcelableExtra<RoomInviteModel>("roomInviteModel")
            roomInviteModel?.let {
                adapter.add(it)
                adapter.roomInviteModelsFull.add(it)
            }
        }
    }

    private lateinit var viewModel: RoomInvitesViewModel
    private lateinit var adapter: RoomInvitesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val intentFilter = IntentFilter(ACTION_ROOM_INVITE)
        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        adapter.clear()
        viewModel.loadRoomInviteModel()
    }

    override fun setupViewModel() {
        viewModel = super.setupViewModelBase() as RoomInvitesViewModel
        viewModel.roomInviteModel.observe(this, renderRoomInviteModel)
    }


    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RoomInvitesAdapter(viewModel.roomInviteModel.value ?: mutableListOf(), this)
        recyclerView.adapter = adapter

        val swipeContainer = root.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener { refreshRoomInvitesEvent() }
    }

    // Observers
    private val renderRoomInviteModel = Observer<MutableList<RoomInviteModel>> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.roomInviteModelsFull.addAll(it)
        adapter.update(it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Room Invites"

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

    override fun onAcceptClicked(roomInviteModel: RoomInviteModel) {
        P2GRequest.build(
            roomInviteModel.roomInvite?.let { ApiClient.build().acceptInvite(it) },
            object : Callback<RoomUser> {
                override fun onError(msg: String) {
                    UIHelper.showSnackBarShort(root, "Can not join room")
                }

                override fun onSuccess(obj: RoomUser) {
                    Log.d(TAG, "Joined room with roomUser ID: " + obj.id)

                    val intent = Intent(activity, RoomActivity::class.java)
                    intent.putExtra("roomModel", roomInviteModel.roomModel)
                    intent.putExtra("roomUser", obj)
                    startActivity(intent)
                }
            })
    }

    override fun onRejectClicked(roomInviteModel: RoomInviteModel) {
        P2GRequest.build(
            roomInviteModel.roomInvite?.id?.let { ApiClient.build().rejectInvite(it) },
            object : Callback<Boolean> {
                override fun onError(msg: String) {
                    Log.d(TAG, msg)
                    UIHelper.showSnackBarShort(root, msg)
                }

                override fun onSuccess(obj: Boolean) {
                    adapter.remove(roomInviteModel)
                }
            })
    }

    override fun onRowClicked(roomInviteModel: RoomInviteModel) {
        val inviterId = roomInviteModel.roomInvite?.inviterId

        P2GRequest.build(
            inviterId?.let { ApiClient.build().getUserModel(it) },
            object : Callback<UserModel> {
                override fun onError(msg: String) {
                }

                override fun onSuccess(obj: UserModel) {
                    val intent = Intent(activity, UserActivity::class.java)
                    intent.putExtra("userModel", obj)
                    startActivity(intent)
                }
            })
    }

    private fun refreshRoomInvitesEvent() {
        P2GRequest.build(
            ApiClient.build().getRoomInviteModels(),
            object : Callback<MutableList<RoomInviteModel>> {
                override fun onError(msg: String) {
                    Log.d(TAG, msg)
                    UIHelper.showSnackBarShort(root, "Rooms Invites cannot refreshed")
                    swipeContainer.isRefreshing = false
                }

                override fun onSuccess(obj: MutableList<RoomInviteModel>) {
                    adapter.update(obj)
                    adapter.roomInviteModelsFull.addAll(obj)
                    swipeContainer.isRefreshing = false
                }
            })
    }
}