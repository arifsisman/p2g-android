package vip.yazilim.p2g.android.ui.roomqueue

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.FragmentBase

/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment : FragmentBase(RoomQueueViewModel(), R.layout.fragment_room_queue),
    RoomQueueAdapter.OnItemClickListener {
    private lateinit var adapter: RoomQueueAdapter
    private lateinit var viewModel: RoomQueueViewModel

    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RoomQueueAdapter(viewModel.songs.value ?: mutableListOf(), this)
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = activity?.findViewById(R.id.fab)!!
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun setupViewModel() {
        viewModel = super.setupViewModelBase() as RoomQueueViewModel
        viewModel.songs.observe(this, renderRoomQueue)
    }

    // Observer
    private val renderRoomQueue = Observer<MutableList<Song>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    override fun onItemClicked(roomModel: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}