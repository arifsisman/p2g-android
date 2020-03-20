package vip.yazilim.p2g.android.ui.user

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel
import vip.yazilim.p2g.android.util.helper.TAG


/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserFragment : FragmentBase(R.layout.fragment_user) {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UserAdapter
    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        userModel = (activity as UserActivity).userModel
    }

    override fun onResume() {
        super.onResume()
        userModel?.user?.id?.let { viewModel.loadFriendsCount(it) }
        userModel?.room?.id?.let { viewModel.loadRoomModel(it) }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        if (item != null) item.isVisible = false
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
        viewModel.friendCountsMe.observe(this, renderFriendsCount)
        viewModel.roomModel.observe(this, renderRoomModel)
    }

    override fun setupUI() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = UserAdapter(userModel, null, 0)
        recyclerView.adapter = adapter
    }

    // Observers
    private val renderFriendsCount = Observer<Int> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private val renderRoomModel = Observer<RoomModel> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }
}