package vip.yazilim.p2g.android.ui.user

import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.profile.ProfileAdapter


/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserFragment : FragmentBase(R.layout.fragment_user) {

    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: ProfileAdapter
    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(activity as UserActivity).get(UserViewModel::class.java)
        userModel = (activity as UserActivity).userModel
    }

    override fun onResume() {
        super.onResume()
        userModel?.user?.id?.let { viewModel.loadFriendsCount(it) }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        if (item != null) item.isVisible = false
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
        viewModel.friendCounts.observe(this, renderFriendsCount)
        viewModel.roomModel.observe(this, renderRoomModel)
    }

    override fun setupUI() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ProfileAdapter(userModel, userModel?.roomModel, 0, false)
        recyclerView.adapter = adapter
    }

    // Observers
    private val renderFriendsCount = Observer<Int> { adapter.update(it) }
    private val renderRoomModel = Observer<RoomModel> { adapter.update(it) }

}