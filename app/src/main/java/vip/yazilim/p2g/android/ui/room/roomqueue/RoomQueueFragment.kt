package vip.yazilim.p2g.android.ui.room.roomqueue

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.dialog_spotify_search.view.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.GeneralConstants
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.SwipeToDeleteCallback
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment : FragmentBase(RoomQueueViewModel(), R.layout.fragment_room_queue),
    SearchAdapter.OnItemClickListener {
    private lateinit var adapter: RoomQueueAdapter
    private lateinit var viewModel: RoomQueueViewModel

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var mDialogView: View

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

        // Search with floating action button
        val fab: ExtendedFloatingActionButton = activity?.findViewById(R.id.fab)!!
        fab.setOnClickListener { showSearchDialog() }

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
                            showMaximizedPlayer()
                        }
                    }
                    COLLAPSED -> {
                        val roomActivity = activity as RoomActivity
                        roomActivity.roomUser?.let { roomActivity.canUserAddAndControlSongs(it) }
                        showMinimizedPlayer()
                    }
                    EXPANDED -> {
                        fab.hide()
                        showMaximizedPlayer()
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

    private fun showMinimizedPlayer() {
        val playerMini: ConstraintLayout = root.findViewById(R.id.player_mini)
        playerMini.visibility = View.VISIBLE
    }

    private fun showMaximizedPlayer() {
        val playerMini: ConstraintLayout = root.findViewById(R.id.player_mini)
        playerMini.visibility = View.GONE
    }

    private fun showSearchDialog() {
        mDialogView = View.inflate(context, R.layout.dialog_spotify_search, null)
        val mBuilder =
            AlertDialog.Builder(context, R.style.myFullscreenAlertDialogStyle).setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        val queryEditText = mDialogView.dialog_query
        val searchButton = mDialogView.dialog_search_button
        val addButton = mDialogView.dialog_add_button
        val cancelButton = mDialogView.dialog_cancel_button

        // For request focus and open keyboard
        queryEditText.requestFocus()
        showKeyboard()

        // For disable create button if name is empty
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchButton.isEnabled = s.isNotEmpty()
            }
        })

        // Click cancel
        cancelButton.setOnClickListener {
            mAlertDialog.cancel()
            queryEditText.clearFocus()
            closeKeyboard()
        }

        // Click search
        searchButton.setOnClickListener {
            val query = queryEditText.text.toString()
            request(
                Singleton.apiClient().search(query),
                object : Callback<MutableList<SearchModel>> {
                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShort(mDialogView, msg)
                    }

                    override fun onSuccess(obj: MutableList<SearchModel>) {
                        closeKeyboard()

                        // Hide search bar, search button and show addButton
                        searchButton.visibility = View.GONE
                        addButton.visibility = View.VISIBLE

                        mDialogView.findViewById<EditText>(R.id.dialog_query).visibility = View.GONE

                        // Adapter start and update with requested search model
                        val recyclerView =
                            mDialogView.findViewById<View>(R.id.searchRecyclerView) as RecyclerView
                        recyclerView.layoutManager = LinearLayoutManager(activity)

                        searchAdapter = SearchAdapter(mutableListOf(), this@RoomQueueFragment)
                        recyclerView.adapter = searchAdapter

                        recyclerView.addItemDecoration(object : DividerItemDecoration(
                            recyclerView.context,
                            (recyclerView.layoutManager as LinearLayoutManager).orientation
                        ) {})

                        searchAdapter.update(obj)

                        // Search text query
                        val searchText: TextView = mDialogView.findViewById(R.id.search_text)
                        val searchTextPlaceholder = "Search with query '${query}'"
                        searchText.text = searchTextPlaceholder
                        searchText.visibility = View.VISIBLE
                    }
                })
        }

        addButton.setOnClickListener {
            val selectedSearchModels = searchAdapter.selectedSearchModels

            request((activity as RoomActivity).room?.id?.let {
                Singleton.apiClient().addSongToRoom(it, selectedSearchModels)
            }, object : Callback<Boolean> {
                override fun onSuccess(obj: Boolean) {
                    cancelButton.performClick()
                    selectedSearchModels.clear()
                }

                override fun onError(msg: String) {
                    cancelButton.performClick()
                    UIHelper.showSnackBarShort(root, msg)
                }
            })
        }

    }

    override fun onSearchItemClicked(searchModel: SearchModel) {
        if (::searchAdapter.isInitialized && ::mDialogView.isInitialized) {
            val isAnyItemsSelected = searchAdapter.select(searchModel)
            if (isAnyItemsSelected != null) {
                mDialogView.findViewById<Button>(R.id.dialog_add_button).isEnabled =
                    isAnyItemsSelected
            } else {
                UIHelper.showSnackBarShort(
                    mDialogView,
                    "10 songs or 1 Album/Playlist can be added in each search!"
                )
            }
        }
    }

}