package vip.yazilim.p2g.android.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_room.*
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.api.generic.Callback
import vip.yazilim.p2g.android.api.generic.request
import vip.yazilim.p2g.android.model.p2g.Room
import vip.yazilim.p2g.android.model.p2g.RoomModel
import vip.yazilim.p2g.android.model.p2g.RoomModelSimplified
import vip.yazilim.p2g.android.ui.room.roomqueue.RoomQueueFragment
import vip.yazilim.p2g.android.util.refrofit.Singleton


class RoomActivity : AppCompatActivity() {
    var room: Room? = null
    var roomModel: RoomModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.view_pager)
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
                    0 -> fab.show()
                    else -> fab.hide()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.bringToFront()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                    request(Singleton.apiClient().leaveRoom(), object : Callback<Boolean> {
                        override fun onSuccess(obj: Boolean) {
                            val loginIntent = Intent(this@RoomActivity, MainActivity::class.java)
                            startActivity(loginIntent)

                            //TODO: disconnect from room socket
                        }

                        override fun onError(msg: String) {
                        }
                    })
                }
            }
        }

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want leave room ? \n(If you are owner of the room, it will be closed)")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    private fun getRoomModel(roomId: Long) =
        request(Singleton.apiClient().getRoomModel(roomId), object : Callback<RoomModel> {
            override fun onSuccess(obj: RoomModel) {
                roomModel = obj
            }

            override fun onError(msg: String) {
            }
        })

    private val tabTitles = arrayOf(
        R.string.queue_title,
        R.string.users_title,
        R.string.chat_title,
        R.string.invite_title
    )

    inner class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> { // RoomQueueFragment
                    RoomQueueFragment()
                }
                1 -> { //TODO RoomUsersFragment
                    RoomQueueFragment()
                }
                2 -> { //TODO RoomChatFragment
                    RoomQueueFragment()
                }
                3 -> { //TODO RoomInviteFragment
                    RoomQueueFragment()
                }
                else -> throw IllegalArgumentException()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context.resources.getString(tabTitles[position])
        }

        override fun getCount(): Int {
            return tabTitles.size
        }
    }

}