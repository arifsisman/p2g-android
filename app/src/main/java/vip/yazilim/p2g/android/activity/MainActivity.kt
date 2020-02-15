package vip.yazilim.p2g.android.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import vip.yazilim.p2g.android.model.p2g.User
import vip.yazilim.p2g.android.model.websocket.ChatMessage
import vip.yazilim.p2g.android.service.UserWebSocketService
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

    companion object {
        private val TAG = MainActivity::class.simpleName
        private const val ACTION_STRING_SERVICE = "ToService"
        private const val ACTION_STRING_ACTIVITY = "ToActivity"
    }

    private val activityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Toast.makeText(applicationContext, "received message in activity..!", Toast.LENGTH_SHORT).show()
            Log.v(TAG, "received message in activity..!")
        }
    }

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

        val intentFilter = IntentFilter(ACTION_STRING_ACTIVITY)
        //Map the intent filter to the receiver
        registerReceiver(activityReceiver, intentFilter)

        val user = intent.getParcelableExtra<User>("user")
        user?.id?.let { startUserWebSocket(it) }
        sendBroadcast()
    }

//    /** Called when the activity is first created.  */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main)
//        //STEP2: register the receiver
//        if (activityReceiver != null) { //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
//            val intentFilter = IntentFilter(ACTION_STRING_ACTIVITY)
//            //Map the intent filter to the receiver
//            registerReceiver(activityReceiver, intentFilter)
//        }
//        //Start the service on launching the application
//        startService(Intent(this, MyService::class.java))
//        findViewById<View>(R.id.button).setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                Log.d("SampleActivity", "Sending broadcast to service")
//                sendBroadcast()
//            }
//        })
//    }

//    override fun startActivity(intent: Intent?) {
//        super.startActivity(intent)
//        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
//    }
//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.from_right_out, R.anim.from_left_in)
//    }

//    override fun onResume() {
//        super.onResume()
//        connectRoomWebSocket(1)
//    }

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
        return true
    }

    private fun startUserWebSocket(userId: String) {
        val intent = Intent(this@MainActivity, UserWebSocketService::class.java)
        intent.putExtra("userId", userId)
        startService(intent)
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_SERVICE"
    private fun sendBroadcast() {
        val intent = Intent()
        intent.action = ACTION_STRING_SERVICE
        sendBroadcast(intent)
    }

    @SuppressLint("CheckResult")
    private fun connectRoomWebSocket(roomId: Long) {
        roomWSClient = getRoomWebSocketClient(roomId)

        roomWSClient.connect()

        roomWSClient.lifecycle()
            .subscribe({
                when (it.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i(TAG, it.toString())
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.i(TAG, it.toString())
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.i(TAG, it.toString())
                    }
                    else -> Log.i(TAG, it.toString())
                }
            }, { t: Throwable? ->
                Log.d(TAG, t?.message.toString())
            })

        roomWSClient.topic("/p2g/room/$roomId/messages")
            .subscribe({
                Log.d(TAG, it.payload)
            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })

        roomWSClient.topic("/p2g/room/$roomId/songs")
            .subscribe({
                Log.d(TAG, it.payload)
            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })

        roomWSClient.topic("/p2g/room/$roomId/status")
            .subscribe({
                Log.d(TAG, it.payload)
            }, { t: Throwable? -> Log.d(TAG, t?.message.toString()) })

        val gsonBuilder = GsonBuilder()
        val gson = registerLocalDateTime(gsonBuilder).create()

        val chatMessage = ChatMessage("TEST", "TEST", 1, "TEST", LocalDateTime.now())
        val chatMessageJson = gson.toJson(chatMessage)

        roomWSClient.send("/p2g/room/$roomId", chatMessageJson).subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::roomWSClient.isInitialized) {
            roomWSClient.disconnect()
        }
    }
}
