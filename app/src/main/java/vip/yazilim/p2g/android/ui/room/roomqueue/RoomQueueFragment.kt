package vip.yazilim.p2g.android.ui.room.roomqueue

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.SwipeToDeleteCallback
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment : FragmentBase(RoomQueueViewModel(), R.layout.fragment_room_queue) {
    private lateinit var adapter: RoomQueueAdapter
    private lateinit var viewModel: RoomQueueViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RoomQueueAdapter(viewModel.songs.value ?: mutableListOf())
        recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        val fab: FloatingActionButton = activity?.findViewById(R.id.fab)!!
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Swipe left for delete
        val swipeDeleteHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val song = adapter.songs[viewHolder.adapterPosition]
                onDelete(song)
            }
        }

        val swipeDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        swipeDeleteHelper.attachToRecyclerView(recyclerView)

        val slidingUpPanel: SlidingUpPanelLayout =
            root.findViewById(R.id.sliding_layout) as SlidingUpPanelLayout

        slidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(view: View, v: Float) {}

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                when (newState) {
                    DRAGGING -> {
                        if (previousState == COLLAPSED) {
                            fab.hide()

                            val playerMini: ConstraintLayout = root.findViewById(R.id.player_mini)
                            playerMini.visibility = View.GONE
                        } else if (previousState == EXPANDED) {
                            fab.show()

                            val playerMini: ConstraintLayout = root.findViewById(R.id.player_mini)
                            playerMini.visibility = View.VISIBLE
                        }
                    }
                    else -> {

                    }
                }
            }
        })

        val seekBarTop = root.findViewById<SeekBar>(R.id.seek_bar_top)
        seekBarTop.setOnTouchListener { _, _ -> true }

    }

    override fun setupViewModel() {
        viewModel = super.setupViewModelBase() as RoomQueueViewModel
        viewModel.songs.observe(this, renderRoomQueue)
        (activity as RoomActivity).room?.id?.let { viewModel.loadSongs(it) }
    }

    // Observer
    private val renderRoomQueue = Observer<MutableList<Song>> {
        Log.v(GeneralConstants.LOG_TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE
        adapter.update(it)
    }

    private fun onDelete(song: Song) =
        request(Singleton.apiClient().removeSongFromRoom(song.id), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
                adapter.remove(song)
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShort(root, msg)
            }
        })

}