package vip.yazilim.p2g.android.ui.room.roomqueue

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.dialog_spotify_search.*
import kotlinx.android.synthetic.main.dialog_spotify_search.view.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.model.p2g.Song
import vip.yazilim.p2g.android.ui.FragmentBase
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

    private lateinit var roomActivity: RoomActivity
    private lateinit var adapter: RoomQueueAdapter

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var mDialogView: View

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomQueueAdapter(
            roomActivity.roomViewModel.songList.value ?: mutableListOf()
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

        roomActivity.roomViewModel.songList.observe(this, renderRoomQueue)

        swipeRefreshContainer.setOnRefreshListener {
            refreshQueueEvent()
        }
    }

    private fun refreshQueueEvent() = request(
        roomActivity.room?.id?.let { Singleton.apiClient().getRoomSongs(it) },
        object : Callback<MutableList<Song>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortSafe(root, "Rooms cannot refreshed")
                swipeRefreshContainer.isRefreshing = false
            }

            override fun onSuccess(obj: MutableList<Song>) {
                adapter.update(obj)
                swipeRefreshContainer.isRefreshing = false
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

        val queryEditText = mDialogView.dialogQuery
        val searchButton = mDialogView.dialog_search_button
        val addButton = mDialogView.addButton
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
            searchRecyclerView.layoutManager = LinearLayoutManager(activity)
            searchRecyclerView.setHasFixedSize(true)

            searchAdapter = SearchAdapter(mutableListOf(), this@RoomQueueFragment)
            searchRecyclerView.adapter = searchAdapter

            searchRecyclerView.addItemDecoration(object : DividerItemDecoration(
                searchRecyclerView.context,
                (searchRecyclerView.layoutManager as LinearLayoutManager).orientation
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

                        dialogQuery.visibility = View.GONE

                        searchAdapter.update(obj)

                        // Search text query
                        val searchTextPlaceholder = "Search with query '${query}'"
                        searchText.text = searchTextPlaceholder
                        searchText.visibility = View.VISIBLE
                    }
                })
        }

        addButton.setOnClickListener {
            val selectedSearchModels = searchAdapter.selectedSearchModels

            request(roomActivity.room?.id?.let {
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
                addButton.isEnabled = isAnyItemsSelected
            } else {
                UIHelper.showSnackBarShortSafe(
                    mDialogView,
                    "10 songs or 1 Album/Playlist can be added in each search!"
                )
            }
        }
    }

    override fun onPlayClicked(view: SwipeLayout, song: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpvoteClicked(view: SwipeLayout, song: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDownvoteClicked(view: SwipeLayout, song: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeleteClicked(view: SwipeLayout, song: Song) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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