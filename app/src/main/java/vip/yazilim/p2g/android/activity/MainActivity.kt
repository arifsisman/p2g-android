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
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.LocalDateTime
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import vip.yazilim.p2g.android.R
import vip.yazilim.p2g.android.constant.ApiConstants
import vip.yazilim.p2g.android.constant.GeneralConstants.LOG_TAG
import vip.yazilim.p2g.android.data.websocket.ChatMessage
import vip.yazilim.p2g.android.util.gson.ThreeTenGsonAdapter.registerLocalDateTime
import vip.yazilim.p2g.android.util.sqlite.DBHelper


/**
 * @author mustafaarifsisman - 21.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class MainActivity : AppCompatActivity() {

    private val db by lazy { DBHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        if (!db.isUserExists()) {
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        } else {
            val user = db.readUser()
            Log.d(LOG_TAG, user.email)
            connectRoomWebSocket("1")
        }

        val navView: BottomNavigationView = nav_view
        val navController = nav_host_fragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.from_right_out, R.anim.from_left_in)
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

    @SuppressLint("CheckResult")
    private fun connectRoomWebSocket(roomId: String) {
        val stompClient: StompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            ApiConstants.BASE_WS_URL_ROOM + roomId
        ).withClientHeartbeat(0).withServerHeartbeat(0)

        stompClient.connect()

        stompClient.lifecycle().subscribe {
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
        }

        stompClient.topic("/p2g/room/$roomId/messages").subscribe { message ->
            Log.d(LOG_TAG, message.payload)
        }

        stompClient.topic("/p2g/room/$roomId/songs").subscribe { songList ->
            Log.d(LOG_TAG, songList.payload)
        }

        stompClient.topic("/p2g/room/$roomId/status").subscribe { roomStatus ->
            Log.d(LOG_TAG, roomStatus.payload)
        }

//        val moshi = Moshi.Builder().build()
//        val adapter: JsonAdapter<ChatMessage> = moshi.adapter(ChatMessage::class.java)

        val gsonBuilder = GsonBuilder()
        val gson = registerLocalDateTime(gsonBuilder).create()

        val chatMessage = ChatMessage("TEST", "TEST", "TEST", "TEST", LocalDateTime.now())
        val chatMessageJson = gson.toJson(chatMessage)

        stompClient.send("/p2g/room/$roomId", chatMessageJson).subscribe()
    }

}
