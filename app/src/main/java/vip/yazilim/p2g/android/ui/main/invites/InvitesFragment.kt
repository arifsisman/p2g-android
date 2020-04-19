package vip.yazilim.p2g.android.ui.main.invites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.MainActivity
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_INVITE_RECEIVE
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class InvitesFragment : FragmentBase(R.layout.fragment_invites),
    InvitesAdapter.OnItemClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: InvitesAdapter

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val roomInviteModel = intent?.getParcelableExtra<RoomInviteModel>("roomInviteModel")
            roomInviteModel?.let {
                adapter.add(it)
                adapter.roomInviteModelsFull.add(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)

        val intentFilter = IntentFilter(ACTION_ROOM_INVITE_RECEIVE)
        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRoomInviteModel()
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
        viewModel.roomInviteModel.observe(this, renderRoomInviteModel)
    }


    override fun setupUI() {
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = InvitesAdapter(viewModel.roomInviteModel.value ?: mutableListOf(), this)
        recycler_view.adapter = adapter

        // SwipeRefreshLayout
        swipe_refresh_container.setOnRefreshListener { refreshRoomInvitesEvent() }
    }

    // Observers
    private val renderRoomInviteModel = Observer<MutableList<RoomInviteModel>> {
        if (it.isNullOrEmpty()) {
            viewModel.onEmptyList.postValue(true)
            adapter.clear()
        } else {
            viewModel.onEmptyList.postValue(false)
            adapter.roomInviteModelsFull.addAll(it)
            adapter.update(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = resources.getString(R.string.hint_search_invites)

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

    override fun onAccept(roomInviteModel: RoomInviteModel) =
        Api.client.acceptInvite(roomInviteModel.roomInvite).queue(success = {
            val roomIntent = Intent(activity, RoomActivity::class.java)
            roomIntent.putExtra("room", it.room)
            roomIntent.putExtra("user", it.user)
            roomIntent.putExtra("roomUser", it.roomUser)
            startActivity(roomIntent)
        }, failure = { viewModel.onMessageError.postValue(it) })

    override fun onReject(roomInviteModel: RoomInviteModel) =
        Api.client.rejectInvite(roomInviteModel.roomInvite.id)
            .queue(success = { adapter.remove(roomInviteModel) },
                failure = { viewModel.onMessageError.postValue(it) })

    override fun onRowClicked(roomInviteModel: RoomInviteModel) =
        Api.client.getUserModel(roomInviteModel.roomInvite.inviterId).queue(success = {
            val intent = Intent(activity, UserActivity::class.java)
            intent.putExtra("userModel", it)
            startActivity(intent)
        }, failure = { viewModel.onMessageError.postValue(it) })


    private fun refreshRoomInvitesEvent() = Api.client.getRoomInviteModels().queue(success = {
        if (it.isNullOrEmpty()) {
            viewModel.onEmptyList.postValue(true)
        } else {
            viewModel.onEmptyList.postValue(false)
            viewModel.roomInviteModel.postValue(it)
        }
        swipe_refresh_container.isRefreshing = false
    }, failure = {
        viewModel.onMessageError.postValue(
            resources.getString(R.string.err_room_invites_refresh)
        )
        swipe_refresh_container.isRefreshing = false
    })
}