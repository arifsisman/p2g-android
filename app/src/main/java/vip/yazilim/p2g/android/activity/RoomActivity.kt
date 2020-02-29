package vip.yazilim.p2g.android.activity

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.dialog_select_device.view.*
import kotlinx.android.synthetic.main.row_player.*
import org.threeten.bp.Duration
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_SOCKET_ERROR
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_ROOM_STATUS
import vip.yazilim.p2g.android.constant.WebSocketActions.ACTION_SONG_LIST_RECEIVED
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.constant.enums.RoomStatus
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.service.RoomWebSocketService
import vip.yazilim.p2g.android.ui.room.DeviceAdapter
import vip.yazilim.p2g.android.ui.room.PlayerAdapter
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.ui.room.RoomViewModelFactory
import vip.yazilim.p2g.android.ui.room.roomqueue.RoomQueueFragment
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getLocalDateTimeZonedUTC
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton
import vip.yazilim.p2g.android.util.sqlite.DBHelper
import java.util.concurrent.TimeUnit


class RoomActivity : AppCompatActivity(),
    PlayerAdapter.OnItemClickListener,
    PlayerAdapter.OnSeekBarChangeListener,
    DeviceAdapter.OnItemClickListener {
    val db by lazy { DBHelper(this) }

    var room: Room? = null
    var roomModel: RoomModel? = null
    var roomUser: RoomUser? = null

    lateinit var roomViewModel: RoomViewModel
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var slidingUpPanel: SlidingUpPanelLayout

    @Volatile
    private var isPlaying = false
    @Volatile
    private var isSeeking = false
    @Volatile
    private var songCurrentMs = 0

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
        getRoomUserMe()
        setupViewModel()
        setupSlidingUpPanel()
        setupPlayer()
        Thread(playerTimer).start()
        room?.id?.let { roomViewModel.loadSongs(it) }
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
                when (position) {
                    0 -> roomUser?.let { canUserAddAndControlSongs(it) }
                    else -> fab.hide()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.bringToFront()
    }

    private fun setupRoomModel() {
        val roomFromIntent = intent.getParcelableExtra<Room>("room")
        val roomModelFromIntent = intent.getParcelableExtra<RoomModel>("roomModel")
        val roomModelSimplifiedFromIntent =
            intent.getParcelableExtra<RoomModelSimplified>("roomModelSimplified")

        when {
            roomFromIntent != null -> {
                title = roomFromIntent.name
                room = roomFromIntent
                getRoomModel(roomFromIntent.id)
            }
            roomModelSimplifiedFromIntent != null -> {
                title = roomModelSimplifiedFromIntent.room?.name
                room = roomModelSimplifiedFromIntent.room
                roomModelSimplifiedFromIntent.room?.id?.let { getRoomModel(it) }
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
                when (newState) {
                    DRAGGING -> {
                        if (previousState == COLLAPSED) {
                            fab.hide()
                            showMaximizedPlayer()
                        }
                    }
                    COLLAPSED -> {
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
                        roomUser?.let { canUserAddAndControlSongs(it) }
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

        slidingUpPanel.setFadeOnClickListener {
            showMinimizedPlayer()
        }
    }

    private fun setupPlayer() {
        val playerRecyclerView: RecyclerView = findViewById(R.id.playerRecyclerView)
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
                    Log.v(PLAYER_TAG, "Song is finished!")
                    runOnUiThread {
                        playPause_button.setImageResource(R.drawable.ic_play_circle_filled_black_64dp)
                    }
                }
            }
            TimeUnit.SECONDS.sleep(1)
        }
    }

    private fun setupViewModelBase() {
        roomViewModel =
            ViewModelProvider(this, RoomViewModelFactory()).get(RoomViewModel::class.java)
    }

    // Observer
    private val renderPlayerSong = Observer<Song> { song ->
        playerAdapter.updatePlayerSong(song)

        if (song != null) {
            isPlaying = song.songStatus == SongStatus.PLAYING.songStatus

            if (song.playingTime != null) {
                val passed =
                    Duration.between(song.playingTime, getLocalDateTimeZonedUTC()).toMillis()
                        .toInt()
                songCurrentMs = when {
                    passed > song.durationMs -> {
                        song.durationMs
                    }
                    song.currentMs > passed -> {
                        song.currentMs
                    }
                    else -> {
                        passed
                    }
                }
            } else {
                songCurrentMs = 0
            }

            Log.d(PLAYER_TAG, "Is ${song.songName} playing? = $isPlaying")
            Log.d(PLAYER_TAG, "CURRENT MS $songCurrentMs")
        } else {
            isPlaying = false
            Log.d(PLAYER_TAG, "Not playing any song!")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select_device -> {
                selectDevice()
            }
            R.id.clear_queue -> {
                clearQueue()
            }
            R.id.leave -> {
                leaveRoom()
            }
            android.R.id.home -> {
                leaveRoom()
            }
        }
        return true
    }

    override fun onBackPressed() {
        leaveRoom()
    }

    override fun onDestroy() {
        super.onDestroy()
        request(Singleton.apiClient().leaveRoom(), null)
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

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want leave room ? \n(If you are owner of the room, it will be closed)")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    private fun stopRoomWebSocketService(broadcastReceiver: BroadcastReceiver) {
        // stop service and unregister service
        val stopServiceIntent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
        stopServiceIntent.putExtra("roomId", room?.id)
        stopService(stopServiceIntent)

        unregisterReceiver(broadcastReceiver)
    }

    private fun startRoomWebSocketService(broadcastReceiver: BroadcastReceiver) {
        // start service and register service
        val intent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
        intent.putExtra("roomId", room?.id)
        startService(intent)

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_SONG_LIST_RECEIVED)
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
                            }

                            override fun onError(msg: String) {
                                UIHelper.showSnackBarShortRoom(viewPager, msg)
                            }
                        })
                }
            }
        }

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want clear room queue ?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    private fun selectDevice() {
        request(Singleton.apiClient().getUserDevices(), object : Callback<MutableList<UserDevice>> {
            override fun onSuccess(obj: MutableList<UserDevice>) {
                val deviceDialogView =
                    View.inflate(this@RoomActivity, R.layout.dialog_select_device, null)
                val mBuilder = AlertDialog.Builder(this@RoomActivity).setView(deviceDialogView)
                val mAlertDialog = mBuilder.show()

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

                val cancelButton: Button = deviceDialogView.dialog_cancel_button
                cancelButton.setOnClickListener { mAlertDialog.cancel() }
            }

            override fun onError(msg: String) {
                UIHelper.showSnackBarShortRoom(viewPager, msg)
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

    private fun getRoomUserMe() {
        // Get room user to decide role and show control & add song events

        request(Singleton.apiClient().getRoomUserMe(), object : Callback<RoomUser> {
            override fun onSuccess(obj: RoomUser) {
                roomUser = obj
                canUserAddAndControlSongs(obj)
            }

            override fun onError(msg: String) {
            }
        })
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when {
                action.equals(ACTION_SONG_LIST_RECEIVED) -> {
                    val songListFromIntent = intent?.getParcelableArrayListExtra<Song>("songList")
                    songListFromIntent?.let { songList ->
                        if (songList.isNullOrEmpty()) {
                            roomViewModel.songList.value = mutableListOf()
                            roomViewModel.playerSong.value = null
                            roomViewModel._isEmptyList.postValue(true)
                        } else {
                            roomViewModel.songList.value = songList
                            roomViewModel.playerSong.value = roomViewModel.getCurrentSong(songList)
                        }
                    }
                }
                action.equals(ACTION_ROOM_SOCKET_ERROR) -> {
                    stopRoomWebSocketService(this)
                    startRoomWebSocketService(this)
                }
                action.equals(ACTION_ROOM_STATUS) -> {
                    val status: String? = intent?.getStringExtra("roomStatus")
                    if (status.equals(RoomStatus.CLOSED.status)) {
                        UIHelper.showToastLong(context, "Room ${room?.name} by ${room?.ownerId}")
                        request(Singleton.apiClient().leaveRoom(), null)

                        val leaveIntent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(leaveIntent)

                        stopRoomWebSocketService(this)
                    }
                }
            }
        }
    }


    // Helpers
    fun canUserAddAndControlSongs(roomUser: RoomUser?): Boolean {
        if (roomUser != null) {
            return if (roomUser.role == Role.ROOM_MODERATOR.role || roomUser.role == Role.ROOM_ADMIN.role || roomUser.role == Role.ROOM_OWNER.role) {
                fab.show()
                playerController.visibility = View.VISIBLE
                true
            } else {
                fab.hide()
                playerController.visibility = View.GONE
                false
            }
        }
        return false
    }

    inner class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> { // RoomQueueFragment
                    RoomQueueFragment(roomViewModel)
                }
                1 -> { //TODO RoomUsersFragment
                    RoomQueueFragment(roomViewModel)
                }
                2 -> { //TODO RoomChatFragment
                    RoomQueueFragment(roomViewModel)
                }
                3 -> { //TODO RoomInviteFragment
                    RoomQueueFragment(roomViewModel)
                }
                else -> throw IllegalArgumentException()
            }
        }

        private val tabTitles = arrayOf(
            R.string.title_queue,
            R.string.title_users,
            R.string.title_chat,
            R.string.title_invite
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

    override fun onPlayerMiniClicked() {
        showMaximizedPlayer()
    }

    override fun onPlayPauseClicked() =
        request(room?.id?.let { Singleton.apiClient().playPause(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showPlayerError(seek_bar_exp, msg)
            }
        })

    override fun onNextClicked() =
        request(room?.id?.let { Singleton.apiClient().next(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showPlayerError(seek_bar_exp, msg)
            }
        })

    override fun onPreviousClicked() =
        request(room?.id?.let { Singleton.apiClient().previous(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showPlayerError(seek_bar_exp, msg)
            }
        })

    override fun onRepeatClicked() =
        request(room?.id?.let { Singleton.apiClient().repeat(it) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showPlayerError(seek_bar_exp, msg)
            }
        })

    private fun onSeekPerformed(ms: Int) =
        request(room?.id?.let { Singleton.apiClient().seek(it, ms) }, object : Callback<Boolean> {
            override fun onSuccess(obj: Boolean) {
            }

            override fun onError(msg: String) {
                UIHelper.showPlayerError(seek_bar_exp, msg)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}