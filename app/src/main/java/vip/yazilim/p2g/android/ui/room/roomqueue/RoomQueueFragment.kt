package vip.yazilim.p2g.android.ui.room.roomqueue

import android.app.AlertDialog
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.dialog_spotify_search.view.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.SwipeToDeleteCallback
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment(var roomViewModel: RoomViewModel) :
    FragmentBase(roomViewModel, R.layout.fragment_room_queue),
    SearchAdapter.OnItemClickListener,
    RoomQueueAdapter.OnItemClickListener {

    private lateinit var adapter: RoomQueueAdapter

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var mDialogView: View

    override fun setupUI() {
        val recyclerView = root.findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomQueueAdapter(
            (activity as RoomActivity).roomViewModel.songList.value ?: mutableListOf()
            , this
        )
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter

        // recyclerView divider
        recyclerView.addItemDecoration(object : DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

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

        (activity as RoomActivity).roomViewModel.songList.observe(this, renderRoomQueue)

        val swipeContainer = root.findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener {
            refreshQueueEvent()
        }

    }

    private fun refreshQueueEvent() = request(
        (activity as RoomActivity).room?.id?.let { Singleton.apiClient().getRoomSongs(it) },
        object : Callback<MutableList<Song>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortSafe(root, "Rooms cannot refreshed")
                swipeContainer.isRefreshing = false
            }

            override fun onSuccess(obj: MutableList<Song>) {
                adapter.update(obj)
                swipeContainer.isRefreshing = false
            }
        })

    override fun setupViewModel() {
        roomViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        roomViewModel.onMessageError.observe(this, onMessageErrorObserver)
        roomViewModel.isEmptyList.observe(this, emptyListObserver)
    }

    // Observer
    private val renderRoomQueue = Observer<MutableList<Song>> {
        Log.v(TAG, "data updated $it")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE

        adapter.update(it)
    }

    private fun onDelete(song: Song) {
        val position = adapter.songs.indexOf(song)
        adapter.remove(song)

        request(Singleton.apiClient().removeSongFromRoom(song.id), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortSafe(root, msg)
                adapter.add(song, position)
            }
        })
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
        }

        // Click search
        searchButton.setOnClickListener {
            // Adapter start and update with requested search model
            val recyclerView =
                mDialogView.findViewById<View>(R.id.searchRecyclerView) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)

            searchAdapter = SearchAdapter(mutableListOf(), this@RoomQueueFragment)
            recyclerView.adapter = searchAdapter

            recyclerView.addItemDecoration(object : DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            ) {})

            val query = queryEditText.text.toString()

            request(
                Singleton.apiClient().search(query),
                object : Callback<MutableList<SearchModel>> {
                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShortSafe(mDialogView, msg)
                    }

                    override fun onSuccess(obj: MutableList<SearchModel>) {
                        closeKeyboard()

                        // Hide search bar, search button and show addButton
                        searchButton.visibility = View.GONE
                        addButton.visibility = View.VISIBLE

                        mDialogView.findViewById<EditText>(R.id.dialog_query).visibility = View.GONE

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
                    UIHelper.showSnackBarShortSafe(root, msg)
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
                UIHelper.showSnackBarShortSafe(
                    mDialogView,
                    "10 songs or 1 Album/Playlist can be added in each search!"
                )
            }
        }
    }

    override fun onSongClicked(view: View, song: Song) {
        val popup = PopupMenu(activity, view)
        popup.menuInflater?.inflate(R.menu.song_popup_menu, popup.menu)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }
        popup.show()
    }


    private fun playSong(song: Song) =
        request(Singleton.apiClient().play(song), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortSafe(root, msg)
            }
        })

}