package vip.yazilim.p2g.android.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.LocalDateTime
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.websocket.ChatMessage
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter.registerLocalDateTime
import vip.yazilim.p2g.android.util.sqlite.DBHelper
import vip.yazilim.p2g.android.util.stomp.WebSocketClient.Companion.getRoomWebSocketClient


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val db by lazy { DBHelper(this) }
    private lateinit var user: User
    private lateinit var roomWSClient: StompClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        val navView: BottomNavigationView = nav_view
        val navController = nav_host_fragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_invites,
                R.id.navigation_friends,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // custom code
//        user = db.readUser()
//        connectRoomWebSocket(1)
    }

//    override fun startActivity(intent: Intent?) {
//        super.startActivity(intent)
//        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
//    }
//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.from_right_out, R.anim.from_left_in)
//    }

    override fun onResume() {
        super.onResume()
        connectRoomWebSocket(1)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.fragments[0].javaClass.simpleName) {
            "FriendsFragment" -> {
                Log.v(LOG_TAG, "back from friends")
            }
            "RoomInvitesFragment" -> {
                Log.v(LOG_TAG, "back from room invites")
            }
            else -> supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Logout -> {
                db.deleteAllData()
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::roomWSClient.isInitialized) {
            roomWSClient.disconnect()
        }
    }

    @SuppressLint("CheckResult")
    private fun connectRoomWebSocket(roomId: Long) {
        roomWSClient = getRoomWebSocketClient(roomId)

        roomWSClient.connect()

        roomWSClient.lifecycle()
            .subscribe({
                when (it.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i(LOG_TAG, it.toString())
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.i(LOG_TAG, it.toString())
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.i(LOG_TAG, it.toString())
                    }
                    else -> Log.i(LOG_TAG, it.toString())
                }
            }, { t: Throwable? ->
                Log.d(LOG_TAG, t?.message.toString())
            })

        roomWSClient.topic("/p2g/room/$roomId/messages")
            .subscribe({
                Log.d(LOG_TAG, it.payload)
            }, { t: Throwable? -> Log.d(LOG_TAG, t?.message.toString()) })

        roomWSClient.topic("/p2g/room/$roomId/songs")
            .subscribe({
                Log.d(LOG_TAG, it.payload)
            }, { t: Throwable? -> Log.d(LOG_TAG, t?.message.toString()) })

        roomWSClient.topic("/p2g/room/$roomId/status")
            .subscribe({
                Log.d(LOG_TAG, it.payload)
            }, { t: Throwable? -> Log.d(LOG_TAG, t?.message.toString()) })

        val gsonBuilder = GsonBuilder()
        val gson = registerLocalDateTime(gsonBuilder).create()

        val chatMessage = ChatMessage("TEST", "TEST", 1, "TEST", LocalDateTime.now())
        val chatMessageJson = gson.toJson(chatMessage)

        roomWSClient.send("/p2g/room/$roomId", chatMessageJson).subscribe()
    }

}
