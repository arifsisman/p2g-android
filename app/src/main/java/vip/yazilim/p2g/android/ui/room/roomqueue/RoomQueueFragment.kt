package vip.yazilim.p2g.android.ui.room.roomqueue

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.activity.RoomActivity
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.constant.enums.SearchType
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.model.p2g.SearchModel
import vip.yazilim.p2g.android.ui.FragmentBase
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.closeKeyboardSoft
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarInfo
import kotlin.coroutines.CoroutineContext


/**
 * @author mustafaarifsisman - 20.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class RoomQueueFragment :
    FragmentBase(R.layout.fragment_room_queue),
    SearchAdapter.OnItemClickListener,
    RoomQueueAdapter.OnItemClickListener,
    SwipeLayout.SwipeListener,
    CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private lateinit var roomActivity: RoomActivity
    private lateinit var adapter: RoomQueueAdapter

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchDialogView: View

    private lateinit var roomViewModel: RoomViewModel

    private lateinit var queryEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomViewModel = ViewModelProvider(activity as RoomActivity).get(RoomViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        roomViewModel = ViewModelProvider(activity as RoomActivity).get(RoomViewModel::class.java)
    }

    override fun setupUI() {
        roomActivity = activity as RoomActivity

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(activity)

        // QueueAdapter
        adapter = RoomQueueAdapter(
            roomViewModel.songList.value ?: mutableListOf()
            , this
            , this
        )

        recycler_view.adapter = adapter

        // recyclerView divider
        recycler_view.addItemDecoration(object : DividerItemDecoration(
            recycler_view.context,
            (recycler_view.layoutManager as LinearLayoutManager).orientation
        ) {})

        // Search with floating action button
        val fab: FloatingActionButton? = activity?.findViewById(R.id.fab)
        fab?.setOnClickListener { showSearchDialog() }

        swipe_refresh_container.setOnRefreshListener {
            refreshQueueEvent()
        }
    }

    override fun setupViewModel() {
        setupDefaultObservers(roomViewModel)
        roomViewModel.songList.observe(this, renderRoomQueue)
    }

    private fun refreshQueueEvent() =
        Api.client.getRoomSongs(roomActivity.room.id).queue(onSuccess = {
            swipe_refresh_container.isRefreshing = false
            roomViewModel.songList.postValue(it)
        }, onFailure = {
            swipe_refresh_container.isRefreshing = false
            roomViewModel.onMessageError.postValue(resources.getString(R.string.err_room_queue_refresh))
        })

    // Observer
    private val renderRoomQueue = Observer<MutableList<Song>> { songList ->
        adapter.update(songList)
    }

    private fun showSearchDialog() {
        searchDialogView = View.inflate(context, R.layout.dialog_spotify_search, null)
        val mBuilder = context?.let {
            MaterialAlertDialogBuilder(it)
                .setView(searchDialogView)
        }
        mBuilder?.show()

        queryEditText = searchDialogView.dialog_query

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

        searchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!searchRecyclerView.hasFocus()) {
                    searchRecyclerView.requestFocus()
                }
            }
        })

        queryEditText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                context?.closeKeyboardSoft(queryEditText.windowToken)
            }
        }

        queryEditText.addTextChangedListener(object : TextWatcher {
            private var searchFor = ""

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor)
                    return

                searchFor = searchText

                launch {
                    delay(500)  //debounce timeOut
                    if (searchText != searchFor)
                        return@launch

                    if (!s.isNullOrEmpty()) {
                        searchAdapter.clear()
                        Api.client.searchSpotify(s.toString())
                            .queue(onSuccess = { searchAdapter.update(it) },
                                onFailure = { searchDialogView.showSnackBarError(it) })
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit
        })

        Api.client.getRecommendations().queue(onSuccess = { searchAdapter.update(it) })

    }

    override fun onSearchItemClicked(searchModel: SearchModel) {
        queryEditText.windowToken.let { context?.closeKeyboardSoft(it) }

        Api.client.addSongWithSearchModel(roomActivity.room.id, searchModel).queue(onSuccess = {
            if (searchModel.type == SearchType.SONG) {
                searchDialogView.showSnackBarInfo("${searchModel.name} queued.")
            } else {
                searchDialogView.showSnackBarInfo("${it.size} songs queued.")
            }
        }, onFailure = { searchDialogView.showSnackBarError(it) })
    }

    override fun onPlayClicked(view: SwipeLayout, song: Song) {
        view.close()

        Api.client.play(song).queue(onFailure = { roomViewModel.onMessageError.postValue(it) })
    }

    override fun onUpvoteClicked(view: SwipeLayout, song: Song) {
        view.close()
        Api.client.upvoteSong(song.id).queue(onSuccess = {
            roomViewModel.onMessageInfo.postValue(
                "${song.songName} ${resources.getString(R.string.info_song_upvoted)}"
            )
        }, onFailure = { roomViewModel.onMessageError.postValue(it) })
    }

    override fun onDownvoteClicked(view: SwipeLayout, song: Song) {
        view.close()
        Api.client.downvoteSong(song.id).queue(onSuccess = {
            roomViewModel.onMessageInfo.postValue(
                "${song.songName} ${resources.getString(R.string.info_song_downvoted)}"
            )
        }, onFailure = { roomViewModel.onMessageError.postValue(it) })
    }

    override fun onDeleteClicked(view: SwipeLayout, song: Song) {
        view.close()
        val position = adapter.songs.indexOf(song)
        adapter.remove(song)

        Api.client.removeSongFromRoom(song.id).queue(onFailure = {
            roomViewModel.onMessageError.postValue(it)
            adapter.add(song, position)
        })
    }

    override fun onOpen(layout: SwipeLayout?) {
    }

    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
    }

    override fun onStartOpen(layout: SwipeLayout?) {
//        val currentRole = roomViewModel.roomUserRole.value
//        if (currentRole == Role.ROOM_USER.role) {
//            layout?.swipePlayButton?.visibility = View.GONE
//            layout?.swipeDeleteButton?.visibility = View.GONE
//        } else {
//            layout?.swipePlayButton?.visibility = View.VISIBLE
//            layout?.swipeDeleteButton?.visibility = View.VISIBLE
//        }
    }

    override fun onStartClose(layout: SwipeLayout?) {
    }

    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
    }

    override fun onClose(layout: SwipeLayout?) {
    }

}