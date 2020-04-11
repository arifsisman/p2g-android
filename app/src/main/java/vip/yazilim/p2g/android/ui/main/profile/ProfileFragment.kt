package vip.yazilim.p2g.android.ui.main.profile

import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.MainActivity
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.main.MainViewModel

/**
 * @author mustafaarifsisman - 04.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class ProfileFragment : FragmentBase(R.layout.fragment_profile) {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(activity as MainActivity).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserModel()
        viewModel.loadFriendsCountMe()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        if (item != null) item.isVisible = false
    }

    override fun setupViewModel() {
        super.setupDefaultObservers(viewModel)
        viewModel.userModel.observe(this, renderUser)
        viewModel.friendCountsMe.observe(this, renderFriendsCount)
    }

    override fun setupUI() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ProfileAdapter(null, 0)
        recyclerView.adapter = adapter
    }

    // Observers
    private val renderUser = Observer<UserModel> {
        adapter.update(it)
    }

    private val renderFriendsCount = Observer<Int> {
        adapter.update(it)
    }
}