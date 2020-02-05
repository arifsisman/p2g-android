package vip.yazilim.p2g.android.ui.roominvites

import android.content.Intent
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
import kotlinx.android.synthetic.main.layout_recycler_view_base.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.client.ApiClient
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.P2GRequest
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.model.p2g.RoomUser
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.util.helper.UIHelper

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesFragment : FragmentBase(RoomInvitesViewModel(), R.layout.fragment_room_invites),
    RoomInvitesAdapter.OnItemClickListener {

    private lateinit var viewModel: RoomInvitesViewModel
    private lateinit var adapter: RoomInvitesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
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
    }

    // Observers
    private val renderRoomInviteModel = Observer<List<RoomInviteModel>> {
        Log.v(LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.roomInviteModelsFull = it as MutableList<RoomInviteModel>
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
                    Log.d(LOG_TAG, msg)
                    UIHelper.showToastLong(context, msg)
                }

                override fun onSuccess(obj: RoomUser) {
                    Log.d(LOG_TAG, "Joined room with roomUser ID: " + obj.id)

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
                    Log.d(LOG_TAG, msg)
                    UIHelper.showToastLong(context, msg)
                }

                override fun onSuccess(obj: Boolean) {
                    adapter.remove(roomInviteModel)
                }
            })
    }
}