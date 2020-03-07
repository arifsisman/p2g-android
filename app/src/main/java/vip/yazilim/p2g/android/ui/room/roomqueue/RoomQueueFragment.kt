package vip.yazilim.p2g.android.ui.room.roomqueue

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.dialog_spotify_search.view.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.enums.SongStatus
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
    private lateinit var searchDialogView: View

    private val viewPager by lazy { (activity as RoomActivity).viewPager }

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomQueueAdapter(
            roomActivity.roomViewModel.songList.value ?: mutableListOf()
            , this
        )

        recyclerView.adapter = adapter

        // recyclerView divider
        recyclerView.addItemDecoration(object : DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ) {})

        // Search with floating action button
        val fab: FloatingActionButton = activity?.findViewById(R.id.fab)!!
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
                UIHelper.showSnackBarShortBottom(viewPager, "Rooms cannot refreshed")
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
    private val renderRoomQueue = Observer<MutableList<Song>> { songList ->
        Log.v(TAG, "data updated $songList")
        layoutError.visibility = View.GONE
        layoutEmpty.visibility = View.GONE

        if (songList.isNullOrEmpty()) {
            (activity as RoomActivity).roomViewModel._isEmptyList.postValue(true)
        } else {
            var hasNext = false

            songList.forEach { song ->
                if (song.songStatus.equals(SongStatus.NEXT.songStatus)) {
                    hasNext = true
                }
            }

            (activity as RoomActivity).skipFlag = hasNext
        }

        adapter.update(songList)
    }

    private fun showSearchDialog() {
        searchDialogView = View.inflate(context, R.layout.dialog_spotify_search, null)
        val mBuilder =
            AlertDialog.Builder(context, R.style.fullScreenAppTheme).setView(searchDialogView)
        val mAlertDialog = mBuilder.show()

        val queryEditText = searchDialogView.dialogQuery
        val searchButton = searchDialogView.dialog_search_button
        val addButton = searchDialogView.addButton
        val cancelButton = searchDialogView.dialog_cancel_button

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
            val searchRecyclerView: RecyclerView =
                searchDialogView.findViewById(R.id.searchRecyclerView)
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
                        UIHelper.showSnackBarShortTop(searchDialogView, msg)
                    }

                    override fun onSuccess(obj: MutableList<SearchModel>) {
                        closeKeyboard()

                        // Hide search bar, search button and show addButton
                        searchButton.visibility = View.GONE
                        addButton.visibility = View.VISIBLE

                        searchDialogView.findViewById<EditText>(R.id.dialogQuery).visibility =
                            View.GONE

                        searchAdapter.update(obj)

                        // Search text query
                        val searchText: TextView = searchDialogView.findViewById(R.id.searchText)
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
                    UIHelper.showSnackBarShortBottom(viewPager, msg)
                }
            })
        }

    }

    override fun onSearchItemClicked(searchModel: SearchModel) {
        if (::searchAdapter.isInitialized && ::searchDialogView.isInitialized) {
            val isAnyItemsSelected = searchAdapter.select(searchModel)
            if (isAnyItemsSelected != null) {
                searchDialogView.findViewById<Button>(R.id.addButton).isEnabled = isAnyItemsSelected
            } else {
                UIHelper.showSnackBarShortTop(
                    searchDialogView,
                    "10 songs or 1 Album/Playlist can be added in each search!"
                )
            }
        }
    }

    override fun onItemClicked(view: SwipeLayout, song: Song) {
        if (view.openStatus != SwipeLayout.Status.Open) {
            view.toggle()
        }
    }

    override fun onPlayClicked(view: SwipeLayout, song: Song) =
        request(Singleton.apiClient().play(song), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortBottom(viewPager, msg)
            }
        })

    override fun onUpvoteClicked(view: SwipeLayout, song: Song) {
        val db = (activity as RoomActivity).db

        if (db.isVotedBefore(song)) {
            UIHelper.showSnackBarShortBottom(viewPager, "Song voted before")
        } else {
            request(Singleton.apiClient().upvoteSong(song.id), object : Callback<Int> {
                override fun onSuccess(obj: Int) {
                    db.insertVotedSong(song)
                    UIHelper.showSnackBarShortBottom(viewPager, "${song.songName} upvoted.")
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortBottom(viewPager, msg)
                }
            })
        }
    }

    override fun onDownvoteClicked(view: SwipeLayout, song: Song) {
        val db = (activity as RoomActivity).db

        if (db.isVotedBefore(song)) {
            UIHelper.showSnackBarShortBottom(viewPager, "Song voted before")
        } else {
            request(Singleton.apiClient().downvoteSong(song.id), object : Callback<Int> {
                override fun onSuccess(obj: Int) {
                    db.insertVotedSong(song)
                    UIHelper.showSnackBarShortBottom(viewPager, "${song.songName} downvoted.")
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortBottom(viewPager, msg)
                }
            })
        }
    }

    override fun onDeleteClicked(view: SwipeLayout, song: Song) {
        val position = adapter.songs.indexOf(song)
        adapter.remove(song)

        request(Singleton.apiClient().removeSongFromRoom(song.id), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortBottom(root, msg)
                adapter.add(song, position)
            }
        })
    }

}