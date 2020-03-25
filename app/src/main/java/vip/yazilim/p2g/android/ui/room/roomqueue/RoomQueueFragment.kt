package vip.yazilim.p2g.android.ui.room.roomqueue

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dialog_spotify_search.view.*
import kotlinx.android.synthetic.main.fragment_room_queue.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboard
import vip.yazilim.p2g.android.util.refrofit.Singleton


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment :
    FragmentBase(R.layout.fragment_room_queue),
    SearchAdapter.OnItemClickListener,
    RoomQueueAdapter.OnItemClickListener,
    SwipeLayout.SwipeListener {

    private lateinit var roomActivity: RoomActivity
    private lateinit var adapter: RoomQueueAdapter

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchDialogView: View

    private lateinit var roomViewModel: RoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomViewModel = ViewModelProvider(activity as RoomActivity).get(RoomViewModel::class.java)
    }

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomQueueAdapter(
            roomViewModel.songList.value ?: mutableListOf()
            , this
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

        swipeRefreshContainer.setOnRefreshListener {
            refreshQueueEvent()
        }
    }

    override fun setupViewModel() {
        roomViewModel.songList.observe(this, renderRoomQueue)
        roomViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        roomViewModel.onMessageError.observe(this, onMessageErrorObserver)
    }

    private fun refreshQueueEvent() = request(
        roomActivity.room?.id?.let { Singleton.apiClient().getRoomSongs(it) },
        object : Callback<MutableList<Song>> {
            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(
                    root,
                    resources.getString(R.string.err_room_queue_refresh)
                )
                swipeRefreshContainer.isRefreshing = false
            }

            override fun onSuccess(obj: MutableList<Song>) {
                adapter.update(obj)
                swipeRefreshContainer.isRefreshing = false
            }
        })

    // Observer
    private val renderRoomQueue = Observer<MutableList<Song>> { songList ->
        Log.v(TAG, "data updated $songList")
        layoutError.visibility = View.GONE

        var hasNext = false

        songList.forEach { song ->
            if (song.songStatus.equals(SongStatus.NEXT.songStatus)) {
                hasNext = true
            }
        }

        roomActivity.skipFlag = hasNext

        adapter.update(songList)
    }

    private fun showSearchDialog() {
        searchDialogView = View.inflate(context, R.layout.dialog_spotify_search, null)
        val mBuilder = MaterialAlertDialogBuilder(context)
            .setView(searchDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        val queryEditText = searchDialogView.dialogQuery
        val searchButton = searchDialogView.dialog_search_button
        val addButton = searchDialogView.addButton
        val cancelButton = searchDialogView.dialog_cancel_button

        // For disable create button if name is empty
        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchButton.isEnabled = s.isNotEmpty()
            }
        })

        // For request focus and open keyboard
        queryEditText.requestFocus()

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
                Singleton.apiClient().searchSpotify(query),
                object : Callback<MutableList<SearchModel>> {
                    override fun onError(msg: String) {
                        UIHelper.showSnackBarShortTop(searchDialogView, msg)
                    }

                    override fun onSuccess(obj: MutableList<SearchModel>) {
                        context?.closeKeyboard()

                        // Hide search bar, search button and show addButton
                        searchButton.visibility = View.GONE
                        addButton.visibility = View.VISIBLE

                        searchDialogView.findViewById<EditText>(R.id.dialogQuery).visibility =
                            View.GONE

                        searchAdapter.update(obj)

                        // Search text query
                        val searchText: TextView = searchDialogView.findViewById(R.id.searchText)
                        val searchTextPlaceholder =
                            "${resources.getString(R.string.info_search_queue)} '${query}'"
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
                    UIHelper.showSnackBarShortTop(root, msg)
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
                    resources.getString(R.string.err_room_queue_add)
                )
            }
        }
    }

    override fun onPlayClicked(view: SwipeLayout, song: Song) {
        view.close()

        request(Singleton.apiClient().play(song), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
            }
        })
    }

    override fun onUpvoteClicked(view: SwipeLayout, song: Song) {
        view.close()
        val db = roomActivity.db

        if (roomActivity.room?.let { db.isVotedBefore(it, song) }!!) {
            UIHelper.showSnackBarShortTop(root, resources.getString(R.string.err_song_vote))
        } else {
            request(Singleton.apiClient().upvoteSong(song.id), object : Callback<Int> {
                override fun onSuccess(obj: Int) {
                    roomActivity.room?.let { db.insertVotedSong(it, song) }
                    UIHelper.showSnackBarShortTop(
                        root,
                        "${song.songName} ${resources.getString(R.string.info_song_upvoted)}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortTop(root, msg)
                }
            })
        }
    }

    override fun onDownvoteClicked(view: SwipeLayout, song: Song) {
        view.close()
        val db = roomActivity.db

        if (roomActivity.room?.let { db.isVotedBefore(it, song) }!!) {
            UIHelper.showSnackBarShortTop(root, resources.getString(R.string.err_song_vote))
        } else {
            request(Singleton.apiClient().downvoteSong(song.id), object : Callback<Int> {
                override fun onSuccess(obj: Int) {
                    roomActivity.room?.let { db.insertVotedSong(it, song) }
                    UIHelper.showSnackBarShortTop(
                        root,
                        "${song.songName} ${resources.getString(R.string.info_song_downvoted)}"
                    )
                }

                override fun onError(msg: String) {
                    UIHelper.showSnackBarShortTop(root, msg)
                }
            })
        }
    }

    override fun onDeleteClicked(view: SwipeLayout, song: Song) {
        view.close()
        val position = adapter.songs.indexOf(song)
        adapter.remove(song)

        request(Singleton.apiClient().removeSongFromRoom(song.id), object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortTop(root, msg)
                adapter.add(song, position)
            }
        })
    }

    override fun onOpen(layout: SwipeLayout?) {
    }

    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
    }

    override fun onStartOpen(layout: SwipeLayout?) {
        val currentRole = roomViewModel.roomUserModel.value?.roomUser?.role
        if (currentRole == Role.ROOM_USER.role) {
            layout?.findViewById<ImageButton>(R.id.swipePlayButton)?.visibility = View.GONE
            layout?.findViewById<ImageButton>(R.id.swipeDeleteButton)?.visibility = View.GONE
        } else {
            layout?.findViewById<ImageButton>(R.id.swipePlayButton)?.visibility = View.VISIBLE
            layout?.findViewById<ImageButton>(R.id.swipeDeleteButton)?.visibility = View.VISIBLE
        }
    }

    override fun onStartClose(layout: SwipeLayout?) {
    }

    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
    }

    override fun onClose(layout: SwipeLayout?) {
    }

}