package vip.yazilim.p2g.android.activity

import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.item_player.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_ERROR
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_STATUS
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_USER_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.constant.enums.RoomStatus
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.Room
import vip.yazilim.p2g.android.entity.RoomUser
import vip.yazilim.p2g.android.entity.Song
import vip.yazilim.p2g.android.entity.UserDevice
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.RoomUserModel
import vip.yazilim.p2g.android.service.RoomWebSocketService
import vip.yazilim.p2g.android.ui.room.DeviceAdapter
import vip.yazilim.p2g.android.ui.room.PlayerAdapter
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.ui.room.RoomViewModelFactory
import vip.yazilim.p2g.android.ui.room.roomchat.RoomChatFragment
import vip.yazilim.p2g.android.ui.room.roomqueue.RoomQueueFragment
import vip.yazilim.p2g.android.ui.room.roomusers.RoomUsersFragment
import vip.yazilim.p2g.android.util.helper.TAG
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarErrorIndefinite
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarInfo
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarPlayerError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastLong
import vip.yazilim.p2g.android.util.refrofit.Singleton
import vip.yazilim.p2g.android.util.refrofit.TokenAuthenticator
import vip.yazilim.p2g.android.util.sqlite.DBHelper
import java.util.concurrent.TimeUnit


class RoomActivity : AppCompatActivity(),
    PlayerAdapter.OnItemClickListener,
    PlayerAdapter.OnSeekBarChangeListener,
    DeviceAdapter.OnItemClickListener {
    val db by lazy { DBHelper(this) }

    var room: Room? = null
    var roomModel: RoomModel? = null

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var slidingUpPanel: SlidingUpPanelLayout
    private lateinit var playerRecyclerView: RecyclerView
    private lateinit var deviceDialog: AlertDialog
    private var roomWsReconnectCounter = 0

    private var clearRoomQueueMenuItem: MenuItem? = null

    @Volatile
    private var isPlaying = false

    @Volatile
    private var isSeeking = false

    @Volatile
    private var songCurrentMs = 0

    @Volatile
    internal var skipFlag = false

    @Volatile
    lateinit var playerSong: Song

    companion object {
        private const val PLAYER_TAG = "Player"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        setupViewPager()
        setupViewModelBase()
        setupRoomModel()
        startRoomWebSocketService(broadcastReceiver)
        setupViewModel()
        setupSlidingUpPanel()
        setupPlayer()
        Thread(playerTimer).start()

        roomViewModel.loadRoomUserMe()
        room?.id?.let { roomViewModel.loadSongs(it) }
        room?.id?.let { roomViewModel.loadRoomUsers(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        request(Singleton.apiClient().leaveRoom(), null)
    }

    // Setups
    private fun setupViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                removeBadgeAt(position)

                when (position) {
                    0 -> {
                        canUserAddAndControlSongs(roomViewModel.roomUserModel.value?.roomUser)
                        setPlayerVisibility(true)
                    }
                    else -> {
                        fab.hide()
                        setPlayerVisibility(false)
                    }
                }

                closeKeyboard()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.bringToFront()
    }

    private fun setPlayerVisibility(show: Boolean) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 300
        transition.addTarget(R.id.playerRecyclerView)
        TransitionManager.beginDelayedTransition(slidingUpPanel, transition)

        slidingUpPanel.panelState = if (show) COLLAPSED else HIDDEN
        playerRecyclerView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupRoomModel() {
        val roomFromIntent = intent.getParcelableExtra<Room>("room")
        val roomModelFromIntent = intent.getParcelableExtra<RoomModel>("roomModel")

        when {
            roomFromIntent != null -> {
                title = roomFromIntent.name
                room = roomFromIntent
                getRoomModel(roomFromIntent.id)
            }
            roomModelFromIntent != null -> {
                title = roomModelFromIntent.room?.name
                room = roomModelFromIntent.room
                roomModel = roomModelFromIntent
            }
            else -> {
                setTitle(R.string.title_room)
            }
        }
    }

    private fun setupViewModel() {
        roomViewModel.playerSong.observe(this, renderPlayerSong)
        roomViewModel.roomUserModel.observe(this, renderRoomUserModel)
    }

    private fun setupSlidingUpPanel() {
        slidingUpPanel = findViewById(R.id.slidingUpPanel)

        slidingUpPanel.addPanelSlideListener(object :
            SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(view: View, v: Float) {}

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (viewPager.currentItem == 0) {
                    when (newState) {
                        DRAGGING -> {
                            if (previousState == COLLAPSED) {
                                fab.hide()
                                showMaximizedPlayer()
                            }
                        }
                        COLLAPSED -> {
                            roomViewModel.roomUserModel.value?.let { canUserAddAndControlSongs(it.roomUser) }
                            showMinimizedPlayer()
                        }
                        else -> {
                        }
                    }
                }
            }
        })

        slidingUpPanel.setFadeOnClickListener {
            showMinimizedPlayer()
        }
    }

    private fun setupPlayer() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView)
        playerRecyclerView.setHasFixedSize(true)
        playerRecyclerView.layoutManager = LinearLayoutManager(this)

        // PlayerAdapter
        playerAdapter = PlayerAdapter(roomViewModel.playerSong.value, this, this)
        playerRecyclerView.adapter = playerAdapter
    }

    private val playerTimer = Runnable {
        while (true) {
            if (isPlaying && !isSeeking) {
                runOnUiThread {
                    seek_bar_exp.progress = songCurrentMs
                    seek_bar.progress = songCurrentMs
                    song_current.text = songCurrentMs.getHumanReadableTimestamp()
                    Log.v(PLAYER_TAG, "Song is playing! Views updated.")
                }
                songCurrentMs += 1000
                if (songCurrentMs >= roomViewModel.playerSong.value?.durationMs!!) {
                    isPlaying = false
                    skipHelper()
                    Log.v(PLAYER_TAG, "Song is finished!")
                    runOnUiThread {
                        song_current.text =
                            roomViewModel.playerSong.value?.durationMs!!.getHumanReadableTimestamp()
                        playPause_button.setImageResource(R.drawable.ic_play_circle_filled_white_64dp)
                        playPause_button_mini.setImageResource(R.drawable.ic_play_arrow_white_24dp)
                    }
                }
            }
            TimeUnit.SECONDS.sleep(1)
        }
    }

    private fun skipHelper() {
        if (skipFlag && roomViewModel.roomUserModel.value?.roomUser?.role.equals(Role.ROOM_OWNER.role)) {
            Log.v(PLAYER_TAG, "Skipping next song.")
            skipFlag = false
            if (::playerSong.isInitialized && playerSong.repeatFlag) {
                onSeekPerformed(0)
            } else {
                onNextClicked()
            }
        }
    }

    private fun setupViewModelBase() {
        roomViewModel =
            ViewModelProvider(this, RoomViewModelFactory()).get(RoomViewModel::class.java)
    }

    // Observers
    private val renderPlayerSong = Observer<Song> { song ->
        playerAdapter.updatePlayerSong(song)

        if (song != null) {
            isPlaying = song.songStatus == SongStatus.PLAYING.songStatus

            playerSong = song
            songCurrentMs = RoomViewModel.getCurrentSongMs(song)

            Log.d(PLAYER_TAG, "Is ${song.songName} playing? = $isPlaying")
            Log.d(PLAYER_TAG, "CURRENT MS $songCurrentMs")
        } else {
            isPlaying = false
            Log.d(PLAYER_TAG, "Not playing any song!")
        }
    }

    private val renderRoomUserModel = Observer<RoomUserModel> { roomUserModel ->
        if (viewPager.currentItem == 0) {
            canUserAddAndControlSongs(roomUserModel.roomUser)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_room, menu)
        clearRoomQueueMenuItem = menu?.findItem(R.id.clear_queue)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync_with_room -> syncWithRoom(roomViewModel.roomUserModel.value?.roomUser)
            R.id.select_device -> selectDevice()
            R.id.clear_queue -> clearQueue()
            android.R.id.home -> leaveRoom()
        }
        return true
    }

    private fun syncWithRoom(roomUser: RoomUser?) {
        request(
            roomUser?.let { Singleton.apiClient().syncWithRoom(it) },
            object : Callback<Boolean> {
                override fun onSuccess(obj: Boolean) {
                    if (obj) {
                        viewPager.showSnackBarInfo(resources.getString(R.string.info_sync))
                    } else {
                        viewPager.showSnackBarInfo(resources.getString(R.string.info_not_playing))
                    }
                }

                override fun onError(msg: String) {
                    viewPager.showSnackBarError(msg)
                }
            })
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0 && slidingUpPanel.panelState != COLLAPSED) {
            showMinimizedPlayer()
        }
    }

    private fun leaveRoom() {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    request(Singleton.apiClient().leaveRoom(), null)

                    val loginIntent = Intent(this@RoomActivity, MainActivity::class.java)
                    startActivity(loginIntent)

                    stopRoomWebSocketService(broadcastReceiver)
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.info_leave_room))
            .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
            .show()
    }

    private fun stopRoomWebSocketService(broadcastReceiver: BroadcastReceiver) {
        // stop service and unregister service
        val stopServiceIntent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
        stopServiceIntent.putExtra("roomId", room?.id)
        stopService(stopServiceIntent)

        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            Log.d(TAG, "Room Web Socket not registered.")
        }
    }

    private fun startRoomWebSocketService(broadcastReceiver: BroadcastReceiver) {
        // start service and register service
        val intent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
        intent.putExtra("roomId", room?.id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_SONG_LIST_RECEIVE)
        intentFilter.addAction(ACTION_USER_LIST_RECEIVE)
        intentFilter.addAction(ACTION_MESSAGE_RECEIVE)
        intentFilter.addAction(ACTION_ROOM_SOCKET_ERROR)
        intentFilter.addAction(ACTION_ROOM_STATUS)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun clearQueue() {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    request(
                        room?.id?.let { Singleton.apiClient().clearQueue(it) },
                        object : Callback<Boolean> {
                            override fun onSuccess(obj: Boolean) {
                                viewPager.showSnackBarInfo(resources.getString(R.string.info_queue_cleared))
                            }

                            override fun onError(msg: String) {
                                viewPager.showSnackBarError(msg)
                            }
                        })
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.info_clear_queue))
            .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
            .show()
    }

    private fun selectDevice() {
        request(Singleton.apiClient().getUserDevices(), object : Callback<MutableList<UserDevice>> {
            override fun onSuccess(obj: MutableList<UserDevice>) {
                val deviceDialogView =
                    View.inflate(this@RoomActivity, R.layout.dialog_select_device, null)
                val mBuilder =
                    MaterialAlertDialogBuilder(this@RoomActivity).setView(deviceDialogView)
                deviceDialog = mBuilder.show()

                // Adapter start and update with requested search model
                val selectDeviceRecyclerView: RecyclerView =
                    deviceDialogView.findViewById(R.id.selectDeviceRecyclerView)
                selectDeviceRecyclerView.layoutManager = LinearLayoutManager(this@RoomActivity)
                selectDeviceRecyclerView.setHasFixedSize(true)

                val deviceAdapter = DeviceAdapter(mutableListOf(), this@RoomActivity)
                selectDeviceRecyclerView.adapter = deviceAdapter

                selectDeviceRecyclerView.addItemDecoration(object : DividerItemDecoration(
                    selectDeviceRecyclerView.context,
                    (selectDeviceRecyclerView.layoutManager as LinearLayoutManager).orientation
                ) {})

                deviceAdapter.update(obj)
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarError(msg)
            }
        })
    }


    private fun getRoomModel(roomId: Long) {
        // Get room model if not exists

        request(Singleton.apiClient().getRoomModel(roomId), object : Callback<RoomModel> {
            override fun onSuccess(obj: RoomModel) {
                roomModel = obj
            }

            override fun onError(msg: String) {
            }
        })
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_SONG_LIST_RECEIVE -> {
                    val songListFromIntent =
                        intent.getParcelableArrayListExtra<Song>(ACTION_SONG_LIST_RECEIVE)
                    songListFromIntent?.let { songList ->
                        if (songList.isNullOrEmpty()) {
                            roomViewModel.songList.postValue(mutableListOf())
                            roomViewModel.playerSong.postValue(null)
                        } else {
                            roomViewModel.songList.postValue(songList)
                            roomViewModel.playerSong.postValue(roomViewModel.getCurrentSong(songList))
                            showBadgeAt(0)
                        }
                    }
                }
                ACTION_ROOM_SOCKET_ERROR -> {
                    if (roomWsReconnectCounter < 22) {
                        stopRoomWebSocketService(this)
                        TokenAuthenticator.refreshToken()
                        startRoomWebSocketService(this)
                        roomWsReconnectCounter++
                        viewPager.showSnackBarInfo(resources.getString(R.string.err_room_websocket_reconnect))
                    } else {
                        viewPager.showSnackBarErrorIndefinite(resources.getString(R.string.err_room_websocket_closed))
                    }
                }
                ACTION_ROOM_STATUS -> {
                    val status: String? = intent.getStringExtra(ACTION_ROOM_STATUS)
                    if (status.equals(RoomStatus.CLOSED.status)) {
                        val roomPlaceholder = resources.getString(R.string.title_room)
                        val closedPlaceholder = resources.getString(R.string.info_closed)
                        context?.showToastLong("$roomPlaceholder ${room?.name} $closedPlaceholder - ${room?.ownerId}")
                        request(Singleton.apiClient().leaveRoom(), null)

                        val leaveIntent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(leaveIntent)

                        stopRoomWebSocketService(this)
                    }
                }
                ACTION_USER_LIST_RECEIVE -> {
                    val userListFromIntent =
                        intent.getParcelableArrayListExtra<RoomUserModel>(ACTION_USER_LIST_RECEIVE)
                    userListFromIntent.let { userList ->
                        roomViewModel.roomUserModelList.value = userList
                    }
                    showBadgeAt(1)
                }
                ACTION_MESSAGE_RECEIVE -> {
                    val chatMessage: ChatMessage? =
                        intent.getParcelableExtra(ACTION_MESSAGE_RECEIVE)
                    chatMessage?.let { roomViewModel.newMessage.postValue(chatMessage) }
                    showBadgeAt(2)
                }
            }
        }
    }


    // Helpers
    fun canUserAddAndControlSongs(roomUser: RoomUser?): Boolean {
        if (roomUser != null) {
            return if (roomUser.role == Role.ROOM_USER.role) {
                fab.hide()
                playPause_button_mini.visibility = View.GONE
                playerController.visibility = View.GONE
                clearRoomQueueMenuItem?.isVisible = false
                false
            } else {
                fab.show()
                playerController.visibility = View.VISIBLE
                playPause_button_mini.visibility = View.VISIBLE
                clearRoomQueueMenuItem?.isVisible = true
                true
            }
        }
        return false
    }

    inner class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RoomQueueFragment()
                2 -> RoomChatFragment()
                1 -> RoomUsersFragment()
                else -> throw IllegalArgumentException()
            }
        }

        private val tabTitles = arrayOf(
            R.string.title_queue,
            R.string.title_users,
            R.string.title_chat
        )

        override fun getPageTitle(position: Int): CharSequence? {
            return context.resources.getString(tabTitles[position])
        }

        override fun getCount(): Int {
            return tabTitles.size
        }
    }

    private fun showMinimizedPlayer() {
        player_mini.visibility = View.VISIBLE
        slidingUpPanel.panelState = COLLAPSED
    }

    private fun showMaximizedPlayer() {
        player_mini.visibility = View.GONE
        slidingUpPanel.panelState = EXPANDED
    }

    override fun onPlayPauseMiniClicked() =
        request(room?.id?.let { Singleton.apiClient().playPause(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarError(msg)
            }
        })

    override fun onPlayerMiniClicked() {
        showMaximizedPlayer()
    }

    override fun onPlayPauseClicked() =
        request(room?.id?.let { Singleton.apiClient().playPause(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarPlayerError(msg)
            }
        })

    override fun onNextClicked() =
        request(room?.id?.let { Singleton.apiClient().next(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarPlayerError(msg)
            }
        })

    override fun onPreviousClicked() =
        request(room?.id?.let { Singleton.apiClient().previous(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarPlayerError(msg)
            }
        })

    override fun onRepeatClicked() =
        request(room?.id?.let { Singleton.apiClient().repeat(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarPlayerError(msg)
            }
        })

    private fun onSeekPerformed(ms: Int) =
        request(room?.id?.let { Singleton.apiClient().seek(it, ms) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                viewPager.showSnackBarPlayerError(msg)
            }
        })

    override fun onSeekBarChanged(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(sb: SeekBar) {
                onSeekPerformed(sb.progress)
                isSeeking = false
            }

            override fun onStartTrackingTouch(sb: SeekBar) {
                isSeeking = true
            }

            override fun onProgressChanged(
                sb: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                song_current?.text = progress.getHumanReadableTimestamp()
            }
        }
    }

    override fun onDeviceClicked(userDevice: UserDevice) {
        if (::deviceDialog.isInitialized) {
            deviceDialog.dismiss()
        }

        request(
            Singleton.apiClient().saveUsersActiveDevice(userDevice),
            object : Callback<UserDevice> {
                override fun onSuccess(obj: UserDevice) {
                    viewPager.showSnackBarInfo(resources.getString(R.string.info_device_change))
                }

                override fun onError(msg: String) {
                    viewPager.showSnackBarError(msg)
                }
            })
    }

    fun closeKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showBadgeAt(pos: Int) {
        if (viewPager.currentItem != pos) {
            val badge: BadgeDrawable? = tabLayout.getTabAt(pos)?.orCreateBadge
            badge?.isVisible = true
        }
    }

    fun removeBadgeAt(pos: Int) {
        tabLayout.getTabAt(pos)?.removeBadge()
    }
}