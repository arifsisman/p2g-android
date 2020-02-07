package vip.yazilim.p2g.android.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.layout_recycler_view_base.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.FriendRequestModel
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase

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

        adapter = FriendsAdapter(viewModel.data.value!!, this, this)
        recyclerView.adapter = adapter

        val swipeContainer = root.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener { refreshFriendsEvent() }
    }

    // Observer
    private val renderData = Observer<MutableList<Any>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.adapterDataListFull = it
        adapter.update(it)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
//        val searchView: SearchView = searchItem?.actionView as SearchView
//
//        searchView.queryHint = "Search Friends"
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                Log.d("queryText", query)
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                adapter.filter.filter(newText)
//                Log.d("queryText", newText)
//                return true
//            }
//        })
//
//        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                searchView.requestFocus()
//                searchView.isIconified = false
//                searchView.isIconifiedByDefault = false
//                searchView.visibility = View.VISIBLE
//                setMenuItemsVisibility(menu, searchItem, false)
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                searchView.clearFocus()
//                searchView.setQuery("", false)
//                adapter.filter.filter("")
//                searchView.isIconified = true
//                searchView.isIconifiedByDefault = true
//                searchView.visibility = View.VISIBLE
//                setMenuItemsVisibility(menu, searchItem, true)
//                return true
//            }
//        })
//    }

    private fun refreshFriendsEvent() {
    }

    override fun onAcceptClicked(friendRequestModel: FriendRequestModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRejectClicked(friendRequestModel: FriendRequestModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onIgnoreClicked(friendRequestModel: FriendRequestModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onJoinClicked(room: Room) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeleteClicked(userModel: UserModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}