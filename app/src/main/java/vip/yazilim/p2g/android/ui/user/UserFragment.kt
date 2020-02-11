package vip.yazilim.p2g.android.ui.user

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.UserModel
import vip.yazilim.p2g.android.ui.FragmentBase


/**
 * @author mustafaarifsisman - 11.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class UserFragment : FragmentBase(UserViewModel(), R.layout.fragment_user) {

    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val bundle = this.arguments
        if (bundle != null) {
            userModel = bundle.getParcelable("userModel")!!
        }

    }

    override fun onResume() {
        super.onResume()
        userModel.user?.id?.let { viewModel.loadFriendsCount(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title = userModel.user?.name
        actionBar?.setDisplayHomeAsUpEnabled(true)

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                parentFragmentManager.popBackStack()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        if (item != null) item.isVisible = false
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.home) {
//            Log.v(LOG_TAG, "Back!!!")
//            activity?.onBackPressed()
//        }
//        return true
//    }

    override fun setupViewModel() {
        viewModel = super.setupViewModelBase() as UserViewModel
        viewModel.friends.observe(this, renderFriendsCount)
    }

    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = UserAdapter(userModel, mutableListOf())
        recyclerView.adapter = adapter
    }

    // Observer
    private val renderFriendsCount = Observer<MutableList<UserModel>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }
}