package vip.yazilim.p2g.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_error.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.RoomModel


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        root = inflater.inflate(R.layout.fragment_home, container, false)
        setupViewModel()

        // setupUI
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = HomeAdapter(viewModel.roomModels.value ?: emptyList())
        recyclerView.adapter = adapter

        return root
    }

    //viewmodel
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)

        viewModel.roomModels.observe(this, renderRoomModels)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
//        viewModel.searchString.observe(this, searchObserver)
    }


    //observers
    private val renderRoomModels = Observer<List<RoomModel>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        Log.v(GeneralConstants.LOG_TAG, "isViewLoading $it")
        val visibility = if (it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    @SuppressLint("SetTextI18n")
    private val onMessageErrorObserver = Observer<Any> {
        Log.v(GeneralConstants.LOG_TAG, "onMessageError $it")
        layoutError.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        textViewError.text = "Error $it"
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
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)

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
    }


//    private val searchObserver = Observer<String> { str ->
//        Log.v(GeneralConstants.LOG_TAG, "searchObserver $str")
//        val filtered: ArrayList<RoomModel> = arrayListOf()
//        val roomModel = viewModel.roomModels
//
//        roomModel.value?.forEach { r ->
//            if (r.room.name.contains(str, ignoreCase = true)) {
//                filtered.add(r)
//            }
//        }
//
//        adapter = HomeAdapter(filtered)
//        recyclerView.adapter = adapter
//    }

    override fun onResume() {
        super.onResume()
        viewModel.loadRooms()
    }

}