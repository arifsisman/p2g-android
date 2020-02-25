package vip.yazilim.p2g.android.activity

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.*
import kotlinx.android.synthetic.main.activity_room.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.constant.enums.Role
import vip.yazilim.p2g.android.constant.enums.SongStatus
import vip.yazilim.p2g.android.model.p2g.*
import vip.yazilim.p2g.android.service.RoomWebSocketService
import vip.yazilim.p2g.android.ui.room.PlayerAdapter
import vip.yazilim.p2g.android.ui.room.RoomViewModel
import vip.yazilim.p2g.android.ui.room.RoomViewModelFactory
import vip.yazilim.p2g.android.ui.room.roomqueue.RoomQueueFragment
import vip.yazilim.p2g.android.util.helper.RoomHelper
import vip.yazilim.p2g.android.util.helper.TimeHelper.Companion.getHumanReadableTimestamp
import vip.yazilim.p2g.android.util.helper.UIHelper
import vip.yazilim.p2g.android.util.refrofit.Singleton


class RoomActivity : AppCompatActivity(), PlayerAdapter.OnItemClickListener,
    PlayerAdapter.OnSeekBarChangeListener {
    var room: Room? = null
    var roomModel: RoomModel? = null
    var roomUser: RoomUser? = null

    lateinit var roomViewModel: RoomViewModel

    lateinit var playerAdapter: PlayerAdapter
    private lateinit var viewPager: ViewPager
    lateinit var slidingUpPanel: SlidingUpPanelLayout

    companion object {
        private const val ACTION_SONG_LIST = "SongList"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        setupViewPager()

        setupViewModelBase()

        setupRoomModel()

        setupRoomSocketService()

        getRoomUserMe()

        setupViewModel()

        setupSlidingUpPanel()

        setupPlayer()
    }

    // Setups
    private fun setupViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        viewPager = findViewById(R.id.view_pager)
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

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
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

    private fun setupRoomSocketService() {
        // start service and register service
        val intent = Intent(this@RoomActivity, RoomWebSocketService::class.java)
        intent.putExtra("roomId", room?.id)
        startService(intent)

        val intentFilter = IntentFilter(ACTION_SONG_LIST)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun setupViewModel() {
        roomViewModel.playerSong.observe(this, renderPlayerSong)
    }

    private fun setupSlidingUpPanel() {
        slidingUpPanel = findViewById(R.id.slidingUpContainer)

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
    }

    private fun setupPlayer() {
        val playerRecyclerView = findViewById<View>(R.id.playerRecyclerView) as RecyclerView
        playerRecyclerView.setHasFixedSize(true)
        playerRecyclerView.layoutManager = LinearLayoutManager(this)

        // PlayerAdapter
        playerAdapter = PlayerAdapter(roomViewModel.playerSong.value, this, this)
        playerRecyclerView.adapter = playerAdapter
    }

    private fun setupViewModelBase() {
        roomViewModel =
            ViewModelProvider(this, RoomViewModelFactory()).get(RoomViewModel::class.java)
    }

    // Observer
    private val renderPlayerSong = Observer<Song> { song ->
        playerAdapter.updatePlayerSong(song)

        if (song.songStatus == SongStatus.PLAYING.songStatus) {
            handlePlayingSong(song)
        }
    }

    private fun handlePlayingSong(song: Song) {
        var maxMs = song.durationMs.toLong()
        var currentMs = RoomHelper.getSongCurrentMs(song)

        val seekBar: SeekBar = findViewById(R.id.seek_bar)
        val seekBarExp: SeekBar = findViewById(R.id.seek_bar_exp)

        val songCurrent: TextView = findViewById(R.id.song_current)
        val songMax: TextView = findViewById(R.id.song_max)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

                    //TODO: disconnect from room socket
                }
            }
        }

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want leave room ? \n(If you are owner of the room, it will be closed)")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
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
                                UIHelper.showSnackBarShort(viewPager, msg)
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
            val songListFromIntent = intent?.getParcelableArrayListExtra<Song>("songList")
            songListFromIntent?.let { songList ->
                roomViewModel.songList.value = songList

                val song = roomViewModel.getCurrentSong(songList)
                playerAdapter.updatePlayerSong(song)
            }
        }
    }


    // Helpers
    fun canUserAddAndControlSongs(roomUser: RoomUser?): Boolean {
        val controllerButtons: View = findViewById(R.id.player_controller)
        if (roomUser != null) {
            return if (roomUser.role == Role.ROOM_MODERATOR.role || roomUser.role == Role.ROOM_ADMIN.role || roomUser.role == Role.ROOM_OWNER.role) {
                fab.show()
                controllerButtons.visibility = View.VISIBLE
                true
            } else {
                fab.hide()
                controllerButtons.visibility = View.GONE
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
            R.string.queue_title,
            R.string.users_title,
            R.string.chat_title,
            R.string.invite_title
        )

        override fun getPageTitle(position: Int): CharSequence? {
            return context.resources.getString(tabTitles[position])
        }

        override fun getCount(): Int {
            return tabTitles.size
        }
    }

    private fun showMinimizedPlayer() {
        val playerMini: ConstraintLayout = findViewById(R.id.player_mini)
        playerMini.visibility = View.VISIBLE
    }

    private fun showMaximizedPlayer() {
        val playerMini: ConstraintLayout = findViewById(R.id.player_mini)
        playerMini.visibility = View.GONE
    }

    override fun onPlayPauseClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNextClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPreviousClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRepeatClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSeekBarChanged(
        seekBar: SeekBar,
        songCurrent: TextView,
        songMax: TextView
    ): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(sb: SeekBar) {
                seekBar.progress = sb.progress
            }

            override fun onStartTrackingTouch(sb: SeekBar) {
            }

            override fun onProgressChanged(
                sb: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                songCurrent.text = getHumanReadableTimestamp(progress.toLong())
            }
        }
    }

}