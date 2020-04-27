package vip.yazilim.p2g.android.activity

import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.item_player.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.Api
import vip.yazilim.p2g.android.api.Api.queue
import vip.yazilim.p2g.android.api.Api.queueAndCallbackOnFailure
import vip.yazilim.p2g.android.constant.GeneralConstants.PLAYER_UPDATE_MS
import vip.yazilim.p2g.android.constant.GeneralConstants.WEBSOCKET_RECONNECT_DELAY
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_MESSAGE_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_CLOSED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_CONNECTED
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_RECONNECTING
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_STATUS_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_USER_LIST_RECEIVE
import vip.yazilim.p2g.android.constant.WebSocketActions.CHECK_WEBSOCKET_CONNECTION
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.constant.enums.RoomStatus
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.entity.*
import vip.yazilim.p2g.android.model.p2g.ChatMessage
import vip.yazilim.p2g.android.model.p2g.RoomStatusModel
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
import vip.yazilim.p2g.android.util.helper.TimeHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarError
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarInfo
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showSnackBarWarning
import vip.yazilim.p2g.android.util.helper.UIHelper.Companion.showToastLong
import vip.yazilim.p2g.android.util.helper.debug
import vip.yazilim.p2g.android.util.helper.release

class RoomActivity : BaseActivity(),
    PlayerAdapter.OnItemClickListener,
    PlayerAdapter.OnSeekBarChangeListener,
    DeviceAdapter.OnItemClickListener,
    RewardedVideoAdListener {
    lateinit var room: Room
    lateinit var user: User
    lateinit var roomUser: RoomUser

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var deviceDialog: AlertDialog
    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var lastSync: LocalDateTime

    private var clearRoomQueueMenuItem: MenuItem? = null
    private var durationHandler: Handler = Handler()

    private lateinit var adId: String
    private lateinit var mRewardedVideoAd: RewardedVideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val roomFromIntent = intent.getParcelableExtra<Room>("room")

        if (roomFromIntent == null) {
            finish()
        } else {
            room = roomFromIntent
            title = room.name

            setupAd()
            startRoomWebSocketService()
            setupViewPager()
            setupViewModel()
            setupSlidingUpPanel()
            setupPlayer()
            setupNetworkConnectivityManager()
            registerRoomWebSocketReceiver(broadcastReceiver)
            updateSeekBarTime.run()
        }
    }

    private fun setupAd() {
        MobileAds.initialize(this)

        release {
            adId = "ca-app-pub-9988109607477807/5824550161"
        }
        debug {
            adId = "ca-app-pub-3940256099942544/5224354917"
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

        loadRewardedVideoAd()
    }

    private fun loadRewardedVideoAd() {
        if (this::adId.isInitialized) {
            mRewardedVideoAd.loadAd(adId, AdRequest.Builder().build())

            mRewardedVideoAd.show()
            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRoomWebSocketService()
        unregisterReceiver(broadcastReceiver)
        mRewardedVideoAd.destroy(this)
    }

    override fun onResume() {
        super.onResume()

        mRewardedVideoAd.resume(this)

        // Check socket connection
        checkWebSocketConnection()

        //Try request if unauthorized activity returns to LoginActivity for refresh access token and build authorized API client
        Api.client.getUserDevices()
            .queueAndCallbackOnFailure(onFailure = { view_pager.showSnackBarError(it) })
    }

    private fun setupNetworkConnectivityManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager =
                applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                }

                override fun onLost(network: Network?) {
                    roomViewModel.onMessageError.postValue(resources.getString(R.string.err_network_closed))
                }
            })
        }
    }

    // Setups
    private fun setupViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter

        view_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
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

                tab_layout.getTabAt(position)?.removeBadge()
                closeKeyboard()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tab_layout.setupWithViewPager(view_pager)

        tab_layout.getTabAt(0)?.setIcon(R.drawable.ic_queue_music_white_24dp)
        tab_layout.getTabAt(1)?.setIcon(R.drawable.ic_people_white_24dp)
        tab_layout.getTabAt(2)?.setIcon(R.drawable.ic_chat_white_24dp)
    }

    private fun setPlayerVisibility(show: Boolean) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 300
        transition.addTarget(player_recycler_view)
        TransitionManager.beginDelayedTransition(sliding_up_panel, transition)

        sliding_up_panel.panelState = if (show) COLLAPSED else HIDDEN
        player_recycler_view.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupSlidingUpPanel() {
        sliding_up_panel.addPanelSlideListener(object :
            SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(view: View, v: Float) {}

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (view_pager.currentItem == 0) {
                    when (newState) {
                        DRAGGING -> {
                            if (previousState == COLLAPSED) {
                                fab.hide()
                                showMaximizedPlayer()
                            }
                        }
                        COLLAPSED -> {
                            canUserAddAndControlSongs(roomViewModel.roomUserModel.value?.roomUser)
                            showMinimizedPlayer()
                        }
                        else -> {
                        }
                    }
                }
            }
        })

        sliding_up_panel.setFadeOnClickListener { showMinimizedPlayer() }
    }

    private fun setupPlayer() {
        player_recycler_view.setHasFixedSize(true)
        player_recycler_view.layoutManager = LinearLayoutManager(this)

        // PlayerAdapter
        playerAdapter = PlayerAdapter(roomViewModel.playerSong.value, this, this)
        player_recycler_view.adapter = playerAdapter
    }

    private fun setupViewModel() {
        roomViewModel =
            ViewModelProvider(this, RoomViewModelFactory()).get(RoomViewModel::class.java)
        roomViewModel.playerSong.observe(this, renderPlayerSong)
        roomViewModel.roomUserModel.observe(this, renderRoomUserModel)
        roomViewModel.songCurrentMs.observe(this, renderSongCurrentMs)
    }

    // Observers
    private val renderPlayerSong = Observer<Song> { song ->
        playerAdapter.updatePlayerSong(song)

        if (song == null) {
            roomViewModel.isPlaying.postValue(false)
        } else {
            roomViewModel.songCurrentMs.postValue(RoomViewModel.getCurrentSongMs(song))
            roomViewModel.isPlaying.postValue(song.songStatus == SongStatus.PLAYING.songStatus)
        }
    }

    private val renderRoomUserModel = Observer<RoomUserModel> { roomUserModel ->
        if (view_pager.currentItem == 0) {
            canUserAddAndControlSongs(roomUserModel.roomUser)
        }
    }

    private val renderSongCurrentMs = Observer<Int> { ms ->
        runOnUiThread {
            seek_bar_exp.progress = ms
            seek_bar.progress = ms
            song_current.text = ms.getHumanReadableTimestamp()
            Log.v(TAG, "Song current ms = $ms")
        }
        if (ms >= roomViewModel.playerSong.value?.durationMs?.minus(PLAYER_UPDATE_MS) ?: 0) {
            roomViewModel.isPlaying.postValue(false)
            runOnUiThread {
                song_current?.text =
                    roomViewModel.playerSong.value?.durationMs?.getHumanReadableTimestamp()
                playPause_button.setImageResource(R.drawable.ic_play_circle_filled_white_64dp)
                playPause_button_mini.setImageResource(R.drawable.ic_play_arrow_white_24dp)
            }
            Log.v(TAG, "Song is finished!")
        }
    }

    private val updateSeekBarTime: Runnable = object : Runnable {
        override fun run() {

            val isPlaying = roomViewModel.isPlaying.value
            val isSeeking = roomViewModel.isSeeking.value
            if (isPlaying != null && isSeeking != null && isPlaying && !isSeeking) {
                val currentMs = roomViewModel.songCurrentMs.value
                if (currentMs != null) {
                    roomViewModel.songCurrentMs.postValue(currentMs + PLAYER_UPDATE_MS)
                }
            }

            durationHandler.postDelayed(this, PLAYER_UPDATE_MS.toLong())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_room, menu)
        clearRoomQueueMenuItem = menu?.findItem(R.id.clear_queue)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sync_with_room -> {
                checkWebSocketConnection()
                syncWithRoom()
            }
            R.id.select_device -> selectDevice()
            R.id.clear_queue -> clearQueue()
            android.R.id.home -> leaveRoom()
        }
        return true
    }

    private fun syncWithRoom() {
        if (!this::lastSync.isInitialized || Duration
                .between(lastSync, TimeHelper.getLocalDateTimeZonedUTC())
                .toMillis()
                .toInt() > WEBSOCKET_RECONNECT_DELAY
        ) {
            Api.client.syncWithRoom().queue(
                onSuccess = {
                    lastSync = TimeHelper.getLocalDateTimeZonedUTC()
                    if (it) {
                        view_pager.showSnackBarInfo(resources.getString(R.string.info_sync))
                    } else {
                        view_pager.showSnackBarInfo(resources.getString(R.string.info_not_playing))
                    }
                },
                onFailure = { view_pager.showSnackBarError(it) })
        }
    }

    override fun onBackPressed() {
        if (view_pager.currentItem == 0 && sliding_up_panel.panelState != COLLAPSED) {
            showMinimizedPlayer()
        }
    }

    private fun leaveRoom() {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Api.client.leaveRoom().queue({}, {})
                    finish()
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setMessage(resources.getString(R.string.info_leave_room))
            .setPositiveButton(resources.getString(R.string.info_yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.info_no), dialogClickListener)
            .show()
    }

    private fun stopRoomWebSocketService() {
        if (this::room.isInitialized) {
            val stopServiceIntent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
            stopServiceIntent.putExtra("roomId", room.id)
            stopService(stopServiceIntent)
        }
    }

    private fun startRoomWebSocketService() {
        if (this::room.isInitialized) {
            val intent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
            intent.putExtra("roomId", room.id)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    private fun registerRoomWebSocketReceiver(broadcastReceiver: BroadcastReceiver) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_ROOM_SOCKET_RECONNECTING)
        intentFilter.addAction(ACTION_ROOM_SOCKET_CONNECTED)
        intentFilter.addAction(ACTION_ROOM_SOCKET_CLOSED)

        intentFilter.addAction(ACTION_SONG_LIST_RECEIVE)
        intentFilter.addAction(ACTION_USER_LIST_RECEIVE)
        intentFilter.addAction(ACTION_MESSAGE_RECEIVE)
        intentFilter.addAction(ACTION_ROOM_STATUS_RECEIVE)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun clearQueue() {
        val dialogClickListener = DialogInterface.OnClickListener { _, ans ->
            when (ans) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Api.client.clearQueue(room.id)
                        .queue(
                            onSuccess = { view_pager.showSnackBarInfo(resources.getString(R.string.info_queue_cleared)) },
                            onFailure = { view_pager.showSnackBarError(it) }
                        )
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
        Api.client.getUserDevices().queue(onSuccess = {
            val deviceDialogView =
                View.inflate(this@RoomActivity, R.layout.dialog_select_device, null)
            val mBuilder =
                MaterialAlertDialogBuilder(this@RoomActivity).setView(deviceDialogView)
            deviceDialog = mBuilder.show()

            // Adapter start and update with requested search model
            val selectDeviceRecyclerView: RecyclerView =
                deviceDialogView.findViewById(R.id.select_device_recycler_view)
            selectDeviceRecyclerView.layoutManager = LinearLayoutManager(this@RoomActivity)
            selectDeviceRecyclerView.setHasFixedSize(true)

            val deviceAdapter = DeviceAdapter(mutableListOf(), this@RoomActivity)
            selectDeviceRecyclerView.adapter = deviceAdapter

            selectDeviceRecyclerView.addItemDecoration(object : DividerItemDecoration(
                selectDeviceRecyclerView.context,
                (selectDeviceRecyclerView.layoutManager as LinearLayoutManager).orientation
            ) {})

            deviceAdapter.update(it)
        }, onFailure = { view_pager.showSnackBarError(it) })
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
//                            roomViewModel.playerSong.postValue(roomViewModel.getCurrentSong(songList))
                            roomViewModel.playerSong.postValue(songList[0])
                            showBadgeAt(0)
                        }
                    }
                }
                ACTION_ROOM_SOCKET_CONNECTED -> {
                    view_pager.showSnackBarInfo(resources.getString(R.string.info_room_websocket_connected))
                    Api.client.syncWithRoom()
                        .queueAndCallbackOnFailure(onFailure = { view_pager.showSnackBarError(it) })
                    roomViewModel.loadRoomUserMe()
                    roomViewModel.loadSongs(room.id)
                    roomViewModel.loadRoomUsers(room.id)
                }
                ACTION_ROOM_SOCKET_CLOSED -> {
                    view_pager.showSnackBarError(resources.getString(R.string.warn_room_websocket_closed))
                    checkWebSocketConnection()
                }
                ACTION_ROOM_SOCKET_RECONNECTING -> {
                    view_pager.showSnackBarWarning(resources.getString(R.string.warn_room_websocket_reconnecting))
                }
                ACTION_ROOM_STATUS_RECEIVE -> {
                    val roomStatusModel: RoomStatusModel? =
                        intent.getParcelableExtra(ACTION_ROOM_STATUS_RECEIVE)
                    if (roomStatusModel?.roomStatus != null && roomStatusModel.roomStatus.status == RoomStatus.CLOSED.status) {
                        context?.showToastLong(roomStatusModel.reason)
                        finish()
                    }
                }
                ACTION_USER_LIST_RECEIVE -> {
                    val userListFromIntent =
                        intent.getParcelableArrayListExtra<RoomUserModel>(
                            ACTION_USER_LIST_RECEIVE
                        )
                    if (userListFromIntent != null && roomViewModel.roomUserModelList.value?.size != userListFromIntent.size) {
                        showBadgeAt(1)
                    }
                    userListFromIntent.let { roomViewModel.roomUserModelList.postValue(it) }
                }
                ACTION_MESSAGE_RECEIVE -> {
                    val chatMessage: ChatMessage? =
                        intent.getParcelableExtra(ACTION_MESSAGE_RECEIVE)
                    chatMessage?.let { roomViewModel.newMessage.postValue(it) }
                    if (chatMessage?.roomUser?.name != "Info") {
                        showBadgeAt(2)
                    }
                }
            }
        }
    }


    // Helpers
    fun canUserAddAndControlSongs(roomUser: RoomUser?): Boolean {
        if (roomUser != null) {
            return if (roomUser.roomRole == Role.ROOM_USER.role) {
                fab.hide()
                playPause_button_mini.visibility = View.GONE
                player_controller_buttons.visibility = View.GONE
                clearRoomQueueMenuItem?.isVisible = false
                false
            } else {
                fab.show()
                player_controller_buttons.visibility = View.VISIBLE
                playPause_button_mini.visibility = View.VISIBLE
                clearRoomQueueMenuItem?.isVisible = true
                true
            }
        }
        return false
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> RoomQueueFragment()
                2 -> RoomChatFragment()
                1 -> RoomUsersFragment()
                else -> throw IllegalArgumentException()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return null
        }

        override fun getCount(): Int {
            return 3
        }
    }

    private fun showMinimizedPlayer() {
        player_mini.visibility = View.VISIBLE
        sliding_up_panel.panelState = COLLAPSED
    }

    private fun showMaximizedPlayer() {
        player_mini.visibility = View.GONE
        sliding_up_panel.panelState = EXPANDED
    }

    override fun onPlayerMiniClicked() {
        showMaximizedPlayer()
    }

    override fun onPlayPauseMiniClicked() = Api.client.playPause(room.id).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    override fun onPlayPauseClicked() = Api.client.playPause(room.id).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    override fun onNextClicked() = Api.client.next(room.id).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    override fun onPreviousClicked() = Api.client.previous(room.id).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    override fun onRepeatClicked() = Api.client.repeat(room.id).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    private fun onSeekPerformed(ms: Int) = Api.client.seek(room.id, ms).queueAndCallbackOnFailure(
        onFailure = { player_coordinator_layout.showSnackBarError(it) }
    )

    override fun onSeekBarChanged(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(sb: SeekBar) {
                onSeekPerformed(sb.progress)
                roomViewModel.isSeeking.postValue(false)
            }

            override fun onStartTrackingTouch(sb: SeekBar) {
                roomViewModel.isSeeking.postValue(true)
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

        Api.client.saveUsersActiveDevice(userDevice).queue(
            onSuccess = { view_pager.showSnackBarInfo(resources.getString(R.string.info_device_change)) },
            onFailure = { view_pager.showSnackBarError(it) }
        )
    }

    fun closeKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showBadgeAt(pos: Int) {
        if (view_pager.currentItem != pos) {
            val badge: BadgeDrawable? = tab_layout.getTabAt(pos)?.orCreateBadge
            badge?.isVisible = true
        }
    }

    private fun checkWebSocketConnection() {
        sendBroadcast(Intent(CHECK_WEBSOCKET_CONNECTION))
    }

    override fun onRewardedVideoAdClosed() {
//        loadRewardedVideoAd()
        Log.d(TAG, "onRewardedVideoAdClosed")
    }

    override fun onRewardedVideoAdLeftApplication() {
        Log.d(TAG, "onRewardedVideoAdLeftApplication")
    }

    override fun onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show()
        Log.d(TAG, "onRewardedVideoAdLoaded")
    }

    override fun onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened")
    }

    override fun onRewardedVideoCompleted() {
        Log.d(TAG, "onRewardedVideoCompleted")
    }

    override fun onRewarded(p0: RewardItem?) {
        Log.d(TAG, p0.toString())
    }

    override fun onRewardedVideoStarted() {
        Log.d(TAG, "onRewardedVideoStarted")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Log.d(TAG, p0.toString())
    }

}