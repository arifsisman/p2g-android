package vip.yazilim.p2g.android.ui.user

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.UserActivity
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase


/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserFragment : FragmentBase(UserViewModel(), R.layout.fragment_user) {

    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter
    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
        viewModel = super.setupViewModelBase() as UserViewModel
        viewModel.friends.observe(this, renderFriendsCount)
        viewModel.roomModel.observe(this, renderRoomModel)
    }

    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = UserAdapter(userModel, null, mutableListOf())
        recyclerView.adapter = adapter
    }

    // Observers
    private val renderFriendsCount = Observer<MutableList<UserModel>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private val renderRoomModel = Observer<RoomModel> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }
}