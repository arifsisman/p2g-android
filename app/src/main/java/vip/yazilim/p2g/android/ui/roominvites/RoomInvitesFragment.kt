package vip.yazilim.p2g.android.ui.roominvites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_recycler_view_base.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.RoomInviteModel

/**
 * @author mustafaarifsisman - 31.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomInvitesFragment : Fragment(), RoomInvitesAdapter.OnItemClickListener {

    private lateinit var viewModel: RoomInvitesViewModel
    private lateinit var adapter: RoomInvitesAdapter
    private lateinit var root: View
    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        root = inflater.inflate(R.layout.fragment_room_invites, container, false)

        if (container != null) {
            this.container = container
        }

        setupViewModel()
        setupUI()

        return root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            RoomInvitesViewModelFactory()
        ).get(RoomInvitesViewModel::class.java)

        viewModel.roomInviteModel.observe(this, renderRoomInviteModel)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
    }


    private fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = RoomInvitesAdapter(viewModel.roomInviteModel.value ?: RoomInviteModel(), this)
        recyclerView.adapter = adapter
    }

    //observers
    private val renderRoomInviteModel = Observer<RoomInviteModel> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.roomInviteModelFull = it
        adapter.update(it)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(GeneralConstants.LOG_TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val onMessageErrorObserver = Observer<Any> {
        Log.v(GeneralConstants.LOG_TAG, "onMessageError $it")
        layoutError.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        textViewError.text = it?.toString()
    }

    private val emptyListObserver = Observer<Boolean> {
        Log.v(GeneralConstants.LOG_TAG, "emptyListObserver $it")
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search Room Invites"

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

//        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                searchView.requestFocus()
//                searchView.isIconified = false
//                searchView.isIconifiedByDefault = false
//                searchView.visibility = View.VISIBLE
//                setItemsVisibility(menu, searchItem, false)
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
//                setItemsVisibility(menu, searchItem, true)
//                return true
//            }
//        })
    }


    override fun onItemClicked(roomInviteModel: RoomInviteModel) {
        Log.d(GeneralConstants.LOG_TAG, "Click " + roomInviteModel.roomInvites)
    }


    private fun setItemsVisibility(
        menu: Menu,
        exception: MenuItem,
        visible: Boolean
    ) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item !== exception) item.isVisible = visible
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRoomInviteModel()
    }

    private fun showKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    class RoomInvitesViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RoomInvitesViewModel() as T
        }
    }
}